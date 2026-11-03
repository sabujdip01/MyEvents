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
import sabuj.m.myevents.data.sample.SampleDataManager
import sabuj.m.myevents.data.security.PinHasher
import sabuj.m.myevents.data.security.PinStore
import sabuj.m.myevents.data.settings.SettingsDataStore
import sabuj.m.myevents.data.settings.ThemeMode
import sabuj.m.myevents.data.settings.UserSettings
import sabuj.m.myevents.domain.model.Event
import sabuj.m.myevents.domain.usecase.ObserveEventsUseCase
import sabuj.m.myevents.domain.usecase.ObserveExpensesUseCase
import sabuj.m.myevents.domain.usecase.ObserveInviteesUseCase
import javax.inject.Inject

data class SettingsUiState(
    val settings: UserSettings = UserSettings(),
    val events: List<Event> = emptyList(),
    val isChangePinOpen: Boolean = false,
    val isExportDialogOpen: Boolean = false,
    val snackbarMessage: String? = null,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
    private val pinStore: PinStore,
    private val pinHasher: PinHasher,
    private val syncRepo: sabuj.m.myevents.data.remote.FirestoreSyncRepository,
    private val observeEvents: ObserveEventsUseCase,
    private val observeExpenses: ObserveExpensesUseCase,
    private val observeInvitees: ObserveInviteesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
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
            pinStore.savePin(pinHasher.hash(newPin))
            hideChangePinDialog()
            _uiState.update { it.copy(snackbarMessage = "PIN changed successfully") }
            true
        } else false
    }

    fun syncNow() {
        viewModelScope.launch {
            _uiState.update { it.copy(snackbarMessage = "Syncing with cloud...") }
            val success = syncRepo.performSync()
            if (success) {
                _uiState.update { it.copy(snackbarMessage = "Sync completed successfully") }
            } else {
                _uiState.update { it.copy(snackbarMessage = "Sync failed. Check connection.") }
            }
        }
    }

    fun showExportDialog() {
        _uiState.update { it.copy(isExportDialogOpen = true) }
    }

    fun hideExportDialog() {
        _uiState.update { it.copy(isExportDialogOpen = false) }
    }

    fun exportEventData(context: Context, eventId: String) {
        viewModelScope.launch {
            val event = uiState.value.events.find { it.id == eventId } ?: return@launch
            val expenses = observeExpenses(eventId).first()
            val invitees = observeInvitees(eventId).first()
            CsvWriter.exportAndShare(context, event.title, expenses, invitees)
            hideExportDialog()
        }
    }

    fun snackbarDismissed() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
