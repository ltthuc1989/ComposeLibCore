package com.dvm.auth.restore

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dvm.ui.components.EditTextField
import androidx.compose.foundation.layout.imePadding
import com.dvm.ui.R as CoreR
import androidx.compose.foundation.layout.navigationBarsPadding

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun Email(
    email: String,
    onEmailChanged: (String) -> Unit,
    onSend: () -> Unit
) {
    val focus = FocusRequester()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(null) {
        focus.requestFocus()
    }

    Text(stringResource(CoreR.string.password_restoration_email_description))
    Spacer(modifier = Modifier.padding(bottom = 15.dp))
    EditTextField(
        text = email,
        label = stringResource(CoreR.string.password_restoration_email_label),
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focus),
        onValueChange = { onEmailChanged(it) },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
    )
    Button(
        enabled = email.isNotEmpty(),
        modifier = Modifier
            .fillMaxWidth()
            .imePadding().navigationBarsPadding(),
        onClick = {
            keyboardController?.hide()
            onSend()
        }
    ) {
        Text(stringResource(CoreR.string.password_restoration_email_button))
    }
}