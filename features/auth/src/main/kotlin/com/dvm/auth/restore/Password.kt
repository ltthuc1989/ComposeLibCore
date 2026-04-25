package com.dvm.auth.restore

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dvm.ui.components.EditTextField
import androidx.compose.foundation.layout.imePadding
import com.dvm.ui.R as CoreR
import androidx.compose.foundation.layout.navigationBarsPadding

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun Password(
    password: String,
    confirmPassword: String,
    onPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onSave: () -> Unit
) {
    val passwordFocus = FocusRequester()
    val confirmPasswordFocus = FocusRequester()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(null) {
        passwordFocus.requestFocus()
    }

    Text(stringResource(CoreR.string.password_restoration_password_description))
    Spacer(modifier = Modifier.padding(bottom = 15.dp))
    EditTextField(
        text = password,
        label = stringResource(CoreR.string.password_restoration_password_label),
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(passwordFocus),
        onValueChange = { onPasswordChanged(it) },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(onNext = { confirmPasswordFocus.requestFocus() })
    )
    EditTextField(
        text = confirmPassword,
        label = stringResource(CoreR.string.password_restoration_confirm_password_label),
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(confirmPasswordFocus),
        onValueChange = { onConfirmPasswordChanged(it) },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = {
            keyboardController?.hide()
            onSave()
        })
    )
    Button(
        enabled = password.isNotEmpty() && password == confirmPassword,
        modifier = Modifier
            .fillMaxWidth()
            .imePadding().navigationBarsPadding(),
        onClick = {
            keyboardController?.hide()
            onSave()
        }
    ) {
        Text(stringResource(CoreR.string.password_restoration_password_button))
    }
}