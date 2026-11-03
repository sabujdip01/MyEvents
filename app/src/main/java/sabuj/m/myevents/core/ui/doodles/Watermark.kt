package sabuj.m.myevents.core.ui.doodles

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.labelS

/**
 * Subtle watermark text overlaid at the bottom-centre of a container.
 * Used on export and PDF previews. Alpha defaults to 12 % so it's
 * barely visible but readable.
 *
 * @param text  the watermark string, e.g. "My Events".
 */
@Composable
fun Watermark(
    text: String,
    modifier: Modifier = Modifier,
    alpha: Float = 0.12f,
) {
    Box(
        modifier          = modifier.fillMaxSize(),
        contentAlignment  = Alignment.BottomCenter,
    ) {
        Text(
            text      = text,
            style     = labelS,
            color     = AppColor.text3,
            textAlign = TextAlign.Center,
            modifier  = Modifier
                .alpha(alpha)
                .padding(bottom = Spacing.md),
        )
    }
}

@LightDarkPreviews
@Composable
private fun WatermarkPreview() {
    AppTheme {
        Watermark(text = "My Events")
    }
}

