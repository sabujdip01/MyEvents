package sabuj.m.myevents.core.preview

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview

/**
 * Multi-preview annotation used on every screen and reusable component.
 * Apply this once; never write separate @Preview annotations.
 *
 * 360 × 800 dp — the reference screen size from design.md.
 */
@Preview(
    name = "Light",
    uiMode = UI_MODE_NIGHT_NO,
    showBackground = true,
    widthDp = 360,
    heightDp = 800,
)
@Preview(
    name = "Dark",
    uiMode = UI_MODE_NIGHT_YES,
    showBackground = true,
    widthDp = 360,
    heightDp = 800,
)
annotation class LightDarkPreviews
