package sabuj.m.myevents

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.titleL

/**
 * Single activity — the root of the Compose UI.
 * Phase 0: renders the app with AppTheme to verify the theme compiles.
 * Phase 1 will replace the body with the splash/lock/NavHost logic.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // TODO Phase 1: read palette + isDark from DataStore / Settings
            AppTheme {
                PlaceholderScreen()
            }
        }
    }
}

/** Temporary placeholder — replaced in Phase 1. */
@Composable
private fun PlaceholderScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "My Events — Phase 0 ✓",
            style = titleL,
            color = AppColor.primary,
        )
    }
}