package com.dvm.auth.register

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dvm.auth.register.model.RegisterEvent
import com.dvm.auth.register.model.RegisterState
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

@Composable
internal fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state: RegisterState = viewModel.state

    Drawer(selected = DrawerItem.NONE) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
            DefaultAppBar(
                title = { Text(stringResource(CoreR.string.registration_appbar_title)) },
                navigationIcon = {
                    AppBarIconBack(
                        onNavigateUp = { viewModel.dispatch(RegisterEvent.Back) }
                    )
                }
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {

                var firstName by rememberSaveable { mutableStateOf("") }
                var lastName by rememberSaveable { mutableStateOf("") }
                var email by rememberSaveable { mutableStateOf("") }
                var password by rememberSaveable { mutableStateOf("") }

                val lastNameFocus = remember { FocusRequester() }
                val emailFocus = remember { FocusRequester() }
                val passwordFocus = remember { FocusRequester() }

                EditTextField(
                    text = firstName,
                    label = stringResource(CoreR.string.registration_field_name),
                    error = state.firstNameError,
                    enabled = !state.progress,
                    onValueChange = {
                        firstName = it
                        viewModel.dispatch(RegisterEvent.ChangeFirstName)
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { lastNameFocus.requestFocus() }
                    )
                )
                EditTextField(
                    text = lastName,
                    label = stringResource(CoreR.string.registration_field_last_name),
                    error = state.lastNameError,
                    enabled = !state.progress,
                    onValueChange = {
                        lastName = it
                        viewModel.dispatch(RegisterEvent.ChangeLastName)
                    },
                    modifier = Modifier.focusRequester(lastNameFocus),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { emailFocus.requestFocus() }
                    )
                )
                EditTextField(
                    text = email,
                    label = stringResource(CoreR.string.registration_field_email),
                    error = state.emailError,
                    enabled = !state.progress,
                    onValueChange = {
                        email = it
                        viewModel.dispatch(RegisterEvent.ChangeEmail)
                    },
                    modifier = Modifier.focusRequester(emailFocus),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { passwordFocus.requestFocus() }
                    )
                )
                EditTextField(
                    text = password,
                    label = stringResource(CoreR.string.registration_field_password),
                    error = state.passwordError,
                    enabled = !state.progress,
                    onValueChange = {
                        password = it
                        viewModel.dispatch(RegisterEvent.ChangePassword)
                    },
                    modifier = Modifier.focusRequester(passwordFocus),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            viewModel.dispatch(
                                RegisterEvent.Register(
                                    firstName = firstName,
                                    lastName = lastName,
                                    email = email,
                                    password = password,
                                )
                            )
                        }
                    )
                )

                Spacer(Modifier.height(10.dp))
                ProgressButton(
                    text = stringResource(CoreR.string.registration_button_register),
                    progress = state.progress,
                    onClick = {
                        viewModel.dispatch(
                            RegisterEvent.Register(
                                firstName = firstName,
                                lastName = lastName,
                                email = email,
                                password = password,
                            )
                        )
                    }
                )
                Spacer(Modifier.height(15.dp))
                OutlinedButton(
                    enabled = !state.progress,
                    onClick = { viewModel.dispatch(RegisterEvent.Login) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding().navigationBarsPadding()
                ) {
                    Text(text = stringResource(CoreR.string.registration_button_login))
                }
                Spacer(Modifier.height(100.dp))
            }
        }
    }

    if (state.alert != null) {
        val onDismiss = { viewModel.dispatch(RegisterEvent.DismissAlert) }
        Alert(
            message = stringResource(state.alert),
            onDismiss = onDismiss,
            buttons = { AlertButton(onClick = onDismiss) }
        )
    }
}

