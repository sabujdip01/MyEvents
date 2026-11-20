package sabuj.m.myevents.feature.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sabuj.m.myevents.core.theme.PaletteId
import sabuj.m.myevents.core.util.CsvWriter
import sabuj.m.myevents.data.remote.FirebaseAuthManager
import sabuj.m.myevents.data.remote.FirestoreSyncRepository
import sabuj.m.myevents.data.security.PinHasher
import sabuj.m.myevents.data.security.PinStore
import sabuj.m.myevents.data.settings.SettingsDataStore
import sabuj.m.myevents.data.settings.ThemeMode
import sabuj.m.myevents.data.settings.UserSettings
import sabuj.m.myevents.domain.model.Event
import sabuj.m.myevents.domain.repository.BookingRepository
import sabuj.m.myevents.domain.repository.EventRepository
import sabuj.m.myevents.domain.repository.InviteeRepository
import sabuj.m.myevents.domain.repository.TaskRepository
import sabuj.m.myevents.domain.usecase.ObserveBookingsUseCase
import sabuj.m.myevents.domain.usecase.ObserveEventsUseCase
import sabuj.m.myevents.domain.usecase.ObserveExpensesUseCase
import sabuj.m.myevents.domain.usecase.ObserveInviteesUseCase
import sabuj.m.myevents.domain.usecase.ObserveTasksUseCase
import javax.inject.Inject

data class SettingsUiState(
    val settings: UserSettings = UserSettings(),
    val events: List<Event> = emptyList(),
    val isChangePinOpen: Boolean = false,
    val isExportDialogOpen: Boolean = false,
    val isAuthDialogOpen: Boolean = false,
    val userEmail: String? = null,
    val isSignedIn: Boolean = false,
    val snackbarMessage: String? = null,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
    private val pinStore: PinStore,
    private val pinHasher: PinHasher,
    private val authManager: FirebaseAuthManager,
    private val syncRepo: FirestoreSyncRepository,
    private val eventRepo: EventRepository,
    private val bookingRepo: BookingRepository,
    private val taskRepo: TaskRepository,
    private val inviteeRepo: InviteeRepository,
    private val observeEvents: ObserveEventsUseCase,
    private val observeExpenses: ObserveExpensesUseCase,
    private val observeInvitees: ObserveInviteesUseCase,
    private val observeBookings: ObserveBookingsUseCase,
    private val observeTasks: ObserveTasksUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        checkUserAccount()
        viewModelScope.launch {
            settingsDataStore.userSettingsFlow.collect { userSettings ->
                _uiState.update { it.copy(settings = userSettings) }
            }
        }
        viewModelScope.launch {
            observeEvents().collect { events ->
                _uiState.update { it.copy(events = events) }
            }
        }
    }

    fun checkUserAccount() {
        val user = authManager.currentUser
        _uiState.update {
            it.copy(
                userEmail = user?.email?.ifBlank { null } ?: user?.displayName?.ifBlank { null },
                isSignedIn = user != null && !user.isAnonymous,
            )
        }
    }

    fun showAuthDialog() {
        _uiState.update { it.copy(isAuthDialogOpen = true) }
    }

    fun hideAuthDialog() {
        _uiState.update { it.copy(isAuthDialogOpen = false) }
    }

    fun signInWithEmail(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(snackbarMessage = "Signing in...") }
            val result = authManager.signInWithEmail(email, pass)
            if (result.isSuccess) {
                hideAuthDialog()
                checkUserAccount()
                _uiState.update { it.copy(snackbarMessage = "Signed in successfully! Pulling cloud data...") }
                syncNow()
            } else {
                val err = result.exceptionOrNull()?.localizedMessage ?: "Invalid email or password"
                _uiState.update { it.copy(snackbarMessage = "Sign in failed: $err") }
            }
        }
    }

    fun signUpWithEmail(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(snackbarMessage = "Creating account...") }
            val result = authManager.signUpWithEmail(email, pass)
            if (result.isSuccess) {
                hideAuthDialog()
                checkUserAccount()
                _uiState.update { it.copy(snackbarMessage = "Account created! Syncing with cloud...") }
                syncNow()
            } else {
                val err = result.exceptionOrNull()?.localizedMessage ?: "Could not create account"
                _uiState.update { it.copy(snackbarMessage = "Sign up failed: $err") }
            }
        }
    }

    fun signOut() {
        authManager.signOut()
        checkUserAccount()
        _uiState.update { it.copy(snackbarMessage = "Signed out") }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { settingsDataStore.setThemeMode(mode) }
    }

    fun setPaletteId(palette: PaletteId) {
        viewModelScope.launch { settingsDataStore.setPaletteId(palette) }
    }

    fun setNotificationsMaster(enabled: Boolean) {
        viewModelScope.launch { settingsDataStore.setNotificationsMaster(enabled) }
    }

    fun setNotifyEventCountdowns(enabled: Boolean) {
        viewModelScope.launch { settingsDataStore.setNotifyEventCountdowns(enabled) }
    }

    fun setNotifyPaymentDues(enabled: Boolean) {
        viewModelScope.launch { settingsDataStore.setNotifyPaymentDues(enabled) }
    }

    fun setNotifyTasks(enabled: Boolean) {
        viewModelScope.launch { settingsDataStore.setNotifyTasks(enabled) }
    }

    fun setNotifyBudgetAlerts(enabled: Boolean) {
        viewModelScope.launch { settingsDataStore.setNotifyBudgetAlerts(enabled) }
    }

    fun setFingerprintEnabled(enabled: Boolean) {
        pinStore.isBiometricEnabled = enabled
        viewModelScope.launch { settingsDataStore.setFingerprintEnabled(enabled) }
    }

    fun showChangePinDialog() {
        _uiState.update { it.copy(isChangePinOpen = true) }
    }

    fun hideChangePinDialog() {
        _uiState.update { it.copy(isChangePinOpen = false) }
    }

    fun changePin(currentPin: String, newPin: String): Boolean {
        val hash = pinStore.getHash() ?: return false
        return if (pinHasher.verify(currentPin, hash)) {
            val newHash = pinHasher.hash(newPin)
            pinStore.savePin(newHash)
            hideChangePinDialog()
            _uiState.update { it.copy(snackbarMessage = "PIN changed successfully") }

            viewModelScope.launch {
                runCatching {
                    val uid = authManager.ensureSignedIn()
                    syncRepo.pushPinToRemote(uid, newHash)
                }
            }
            true
        } else false
    }

    fun syncNow() {
        viewModelScope.launch {
            _uiState.update { it.copy(snackbarMessage = "Syncing with cloud...") }
            val result = syncRepo.performSync()
            if (result.isSuccess) {
                _uiState.update { it.copy(snackbarMessage = "Data sync completed successfully!") }
            } else {
                val err = result.exceptionOrNull()?.localizedMessage ?: "Check connection"
                _uiState.update { it.copy(snackbarMessage = "Sync failed: $err") }
            }
        }
    }

    fun showExportDialog() {
        _uiState.update { it.copy(isExportDialogOpen = true) }
    }

    fun hideExportDialog() {
        _uiState.update { it.copy(isExportDialogOpen = false) }
    }

    fun exportEventToUri(context: Context, eventId: String, uri: android.net.Uri) {
        viewModelScope.launch {
            val event = uiState.value.events.find { it.id == eventId } ?: return@launch
            val bookings = observeBookings.byEvent(eventId).first()
            val invitees = observeInvitees(eventId).first()
            val tasks = observeTasks(eventId).first()
            val expenses = observeExpenses(eventId).first()
            val extraCards = settingsDataStore.extraCardsFlow.first()

            val csvString = CsvWriter.generateCsvString(
                event      = event,
                bookings   = bookings,
                invitees   = invitees,
                tasks      = tasks,
                expenses   = expenses,
                extraCards = extraCards,
            )

            val success = CsvWriter.writeToUri(context, uri, csvString)
            hideExportDialog()
            if (success) {
                _uiState.update { it.copy(snackbarMessage = "Export saved to File Manager successfully!") }
            } else {
                _uiState.update { it.copy(snackbarMessage = "Failed to save export file.") }
            }
        }
    }

    fun importCsvData(context: Context, uri: android.net.Uri) {
        viewModelScope.launch {
            try {
                val inputStream = context.contentResolver.openInputStream(uri) ?: return@launch
                val data = sabuj.m.myevents.core.util.CsvReader.parse(inputStream)
                if (data != null) {
                    eventRepo.upsert(data.event)
                    data.bookings.forEach { b -> bookingRepo.upsertBooking(b) }
                    data.tasks.forEach { t -> taskRepo.upsertTask(t) }
                    data.invitees.forEach { inv -> inviteeRepo.upsertInvitee(inv) }
                    if (data.extraCards > 0) {
                        settingsDataStore.setExtraCards(data.extraCards)
                    }
                    _uiState.update { it.copy(snackbarMessage = "Event \"${data.event.title}\" imported successfully!") }
                } else {
                    _uiState.update { it.copy(snackbarMessage = "Failed to parse CSV file. Invalid format.") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(snackbarMessage = "Import failed: ${e.localizedMessage}") }
            }
        }
    }

    fun snackbarDismissed() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
