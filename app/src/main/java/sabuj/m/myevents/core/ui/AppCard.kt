package sabuj.m.myevents.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM

/**
 * Base elevated card used across list screens.
 * Wraps content in a [Card] with the app's surface colour and card radius.
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Radius.card),
            colors = CardDefaults.cardColors(containerColor = AppColor.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            content = { Column(modifier = Modifier.padding(Spacing.lg), content = content) },
        )
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Radius.card),
            colors = CardDefaults.cardColors(containerColor = AppColor.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            content = { Column(modifier = Modifier.padding(Spacing.lg), content = content) },
        )
    }
}

@LightDarkPreviews
@Composable
private fun AppCardPreview() {
    AppTheme {
        AppCard(modifier = Modifier.padding(Spacing.lg)) {
            Text("Card content", style = bodyM, color = AppColor.text)
        }
    }
}

