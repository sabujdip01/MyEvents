package sabuj.m.myevents.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.ui.doodles.BgBlobs

/**
 * The standard screen shell used on every screen — design.md section 4.1.
 * Provides:
 *  - `background` colour fill
 *  - `.bg-blobs` at top-right
 *  - Floating `.menu-btn` at top-left (status bar + 8 dp)
 *  - 16 dp side padding for [content]
 *
 * @param onMenuClick Callback for the menu button. Null hides the button (e.g. lock screens).
 * @param content     Screen content; receives a Modifier with side padding applied.
 */
@Composable
fun ScreenScaffold(
    modifier: Modifier = Modifier,
    onMenuClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppColor.background),
    ) {
        // Background blobs (decorative, behind everything)
        BgBlobs()

        // Main content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.lg),
        ) {
            content()
        }

        // Floating menu button — top-left above all content
        if (onMenuClick != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(start = Spacing.lg, top = Spacing.sm),
            ) {
                MenuButton(onClick = onMenuClick)
            }
        }
    }
}
