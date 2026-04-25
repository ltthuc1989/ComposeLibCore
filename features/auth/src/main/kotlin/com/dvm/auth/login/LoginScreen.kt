package com.dvm.auth.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dvm.auth.login.model.LoginEvent
import com.dvm.auth.login.model.LoginState
import com.dvm.drawer_api.Drawer
import com.dvm.ui.components.Alert
import com.dvm.ui.components.AlertButton
import com.dvm.ui.components.AppBarIconBack
import com.dvm.ui.components.DefaultAppBar
import com.dvm.ui.components.EditTextField
import com.dvm.ui.components.ProgressButton
import com.dvm.utils.DrawerItem
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import com.dvm.ui.R as CoreR
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.navigationBarsPadding

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state: LoginState = viewModel.state

    val keyboardController = LocalSoftwareKeyboardController.current

    Drawer(selected = DrawerItem.NONE) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                DefaultAppBar(
                    title = { Text(stringResource(CoreR.string.login_appbar_title)) },
                    navigationIcon = {
                        AppBarIconBack(onNavigateUp = { viewModel.dispatch(LoginEvent.Back) })
                    }
                )
            }

            Column {

                var email by rememberSaveable { mutableStateOf("") }
                var password by rememberSaveable { mutableStateOf("") }

                val passwordFocusRequest = remember { FocusRequester() }

                val login = {
                    viewModel.login(
                        email = email,
                        password = password,
                    ) {
                        keyboardController?.hide()
                    }
                }

                EditTextField(
                    text = email,
                    label = stringResource(CoreR.string.login_field_email),
                    error = state.emailError,
                    enabled = !state.progress,
                    onValueChange = {
                        email = it
                        viewModel.dispatch(LoginEvent.ChangeLogin)
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { passwordFocusRequest.requestFocus() }
                    )
                )
                EditTextField(
                    text = password,
                    label = stringResource(CoreR.string.login_field_password),
                    error = state.passwordError,
                    enabled = !state.progress,
                    onValueChange = {
                        password = it
                        viewModel.dispatch(LoginEvent.ChangePassword)
                    },
                    modifier = Modifier.focusRequester(passwordFocusRequest),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { login() }
                    )
                )
                Spacer(Modifier.height(30.dp))

                ProgressButton(
                    text = stringResource(CoreR.string.login_button_login),
                    progress = state.progress,
                    onClick = {
                        if (!state.progress) {
                            login()
                        }
                    }
                )

                Spacer(Modifier.height(10.dp))
                OutlinedButton(
                    enabled = !state.progress,
                    onClick = { viewModel.dispatch(LoginEvent.Register) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(CoreR.string.login_button_registartion))
                }
            }

            Column {
                TextButton(
                    enabled = !state.progress,
                    onClick = { viewModel.dispatch(LoginEvent.RestorePassword) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(CoreR.string.login_button_restore_password))
                }
                Spacer(Modifier.imePadding().navigationBarsPadding())
            }
        }
    }

    if (state.alert != null) {
        val onDismiss = { viewModel.dispatch(LoginEvent.DismissAlert) }
        Alert(
            message = stringResource(state.alert),
            onDismiss = onDismiss,
            buttons = { AlertButton(onClick = onDismiss) }
        )
    }
}