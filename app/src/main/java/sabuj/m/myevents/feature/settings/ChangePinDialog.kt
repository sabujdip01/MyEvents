package sabuj.m.myevents.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.labelL
import sabuj.m.myevents.core.theme.titleM
import sabuj.m.myevents.core.ui.AppButtonRow

@Composable
fun ChangePinDialog(
    onDismiss: () -> Unit,
    onConfirm: (currentPin: String, newPin: String) -> Boolean, // returns true if current PIN matched & updated
) {
    var currentPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var failedAttempts by remember { mutableStateOf(0) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape          = RoundedCornerShape(Radius.dialog),
            colors         = CardDefaults.cardColors(containerColor = AppColor.surface),
            modifier       = Modifier.width(374.dp).padding(Spacing.sm),
        ) {
            Column(
                modifier = Modifier.padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Text(text = "Change PIN", style = titleM, color = AppColor.text)

                OutlinedTextField(
                    value          = currentPin,
                    onValueChange  = { if (it.length <= 4 && it.all { c -> c.isDigit() }) currentPin = it },
                    label          = { Text("Current PIN", style = bodyM) },
                    singleLine     = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation(),
                    shape          = RoundedCornerShape(Radius.field),
                    colors         = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = AppColor.primary,
                        unfocusedBorderColor = AppColor.outline,
                    ),
                )

                OutlinedTextField(
                    value          = newPin,
                    onValueChange  = { if (it.length <= 4 && it.all { c -> c.isDigit() }) newPin = it },
                    label          = { Text("New 4-digit PIN", style = bodyM) },
                    singleLine     = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation(),
                    shape          = RoundedCornerShape(Radius.field),
                    colors         = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = AppColor.primary,
                        unfocusedBorderColor = AppColor.outline,
                    ),
                )

                OutlinedTextField(
                    value          = confirmPin,
                    onValueChange  = { if (it.length <= 4 && it.all { c -> c.isDigit() }) confirmPin = it },
                    label          = { Text("Confirm New PIN", style = bodyM) },
                    singleLine     = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation(),
                    shape          = RoundedCornerShape(Radius.field),
                    colors         = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = AppColor.primary,
                        unfocusedBorderColor = AppColor.outline,
                    ),
                )

                if (errorMessage != null) {
                    Text(text = errorMessage!!, style = bodyM, color = AppColor.danger)
                }

                Spacer(Modifier.height(Spacing.xs))

                AppButtonRow(
                    positiveText = "Save PIN",
                    onPositive   = {
                        if (currentPin.length != 4 || newPin.length != 4 || confirmPin.length != 4) {
                            errorMessage = "PINs must be 4 digits"
                        } else if (newPin != confirmPin) {
                            errorMessage = "New PINs do not match"
                        } else {
                            val success = onConfirm(currentPin, newPin)
                            if (!success) {
                                failedAttempts++
                                if (failedAttempts >= 3) {
                                    onDismiss()
                                } else {
                                    errorMessage = "Current PIN is incorrect (${3 - failedAttempts} attempts left)"
                                }
                            }
                        }
                    },
                    negativeText = "Cancel",
                    onNegative   = onDismiss,
                )
            }
        }
    }
}

@sabuj.m.myevents.core.preview.LightDarkPreviews
@Composable
private fun ChangePinDialogPreview() {
    sabuj.m.myevents.core.theme.AppTheme {
        ChangePinDialog(
            onDismiss = {},
            onConfirm = { _, _ -> true },
        )
    }
}
