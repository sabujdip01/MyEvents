package sabuj.m.myevents.core.ui

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM

/**
 * App-wide bottom sheet wrapper. All add/edit sheets use this component
 * to guarantee consistent radius (28 dp), scrim, and drag handle.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest  = onDismiss,
        sheetState        = sheetState,
        shape             = RoundedCornerShape(topStart = Radius.sheet, topEnd = Radius.sheet),
        containerColor    = AppColor.surface,
        scrimColor        = AppColor.scrim,
        modifier          = modifier,
        content           = content,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@LightDarkPreviews
@Composable
private fun AppSheetPreview() {
    AppTheme {
        AppSheet(onDismiss = {}) {
            Text("Sheet content", style = bodyM, color = AppColor.text, modifier = Modifier.padding(Spacing.lg))
        }
    }
}

