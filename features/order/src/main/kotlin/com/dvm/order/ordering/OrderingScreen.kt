package com.dvm.order.ordering

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dvm.drawer_api.Drawer
import com.dvm.order.ordering.model.OrderingEvent
import com.dvm.order.ordering.model.OrderingFields
import com.dvm.order.ordering.model.OrderingState
import com.dvm.ui.components.Alert
import com.dvm.ui.components.AlertButton
import com.dvm.ui.components.AppBarIconBack
import com.dvm.ui.components.DefaultAppBar
import com.dvm.ui.components.LoadingScrim
import com.dvm.utils.DrawerItem
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import com.dvm.ui.R as CoreR
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.navigationBarsPadding

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun OrderingScreen(
    address: String,
    viewModel: OrderingViewModel = hiltViewModel()
) {
    val state: OrderingState = viewModel.state

    LaunchedEffect(address) {
        viewModel.dispatch(OrderingEvent.ChangeAddress(address))
    }

    Drawer(selected = DrawerItem.ORDERS) {
        Column(Modifier.fillMaxSize()) {
            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
            DefaultAppBar(
                title = { Text(stringResource(CoreR.string.ordering_appbar_title)) },
                navigationIcon = {
                    AppBarIconBack {
                        viewModel.dispatch(OrderingEvent.Back)
                    }
                },
            )

            var fields by rememberSaveable { mutableStateOf(OrderingFields()) }

            Column(
                Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                var isEditing by rememberSaveable { mutableStateOf(false) }

                val addressFocus = remember { FocusRequester() }
                val entranceFocus = remember { FocusRequester() }
                val floorFocus = remember { FocusRequester() }
                val apartmentFocus = remember { FocusRequester() }
                val intercomFocus = remember { FocusRequester() }
                val commentFocus = remember { FocusRequester() }

                val keyboardController = LocalSoftwareKeyboardController.current

                Spacer(Modifier.height(16.dp))

                // hack
                // actually we need to set readOnly = !isEditing for OrderingTextField instead of if/else
                // but there's strange bug of not showing keyboard when focus requested
                if (isEditing) {
                    OrderingTextField(
                        value = state.address,
                        onValueChange = {
                            viewModel.dispatch(OrderingEvent.ChangeAddress(it))
                        },
                        singleLine = false,
                        imeAction = ImeAction.Next,
                        modifier = Modifier.focusRequester(addressFocus),
                        keyboardActions = KeyboardActions(
                            onNext = { entranceFocus.requestFocus() }
                        ),
                        startText = {
                            LaunchedEffect(Unit) {
                                addressFocus.requestFocus()
                            }
                            Text(stringResource(CoreR.string.ordering_field_address))
                        }
                    )
                } else {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "${stringResource(CoreR.string.ordering_field_address)}${state.address}",
                        modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                    )
                    Divider()
                }

                Spacer(modifier = Modifier.height(25.dp))
                Row(Modifier.fillMaxWidth()) {
                    Button(
                        enabled = !isEditing || state.address.isNotEmpty(),
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp),
                        onClick = {
                            isEditing = !isEditing
                            if (!isEditing) {
                                keyboardController?.hide()
                            }
                        }
                    ) {
                        if (isEditing) {
                            Text(stringResource(CoreR.string.ordering_button_apply))
                        } else {
                            Text(stringResource(CoreR.string.ordering_button_fill))
                        }
                    }
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp),
                        onClick = { viewModel.dispatch(OrderingEvent.OpenMap) }
                    ) {
                        Text(stringResource(CoreR.string.ordering_button_use_map))
                    }
                }
                Spacer(Modifier.height(8.dp))
                OrderingTextField(
                    value = fields.entrance,
                    onValueChange = { fields = fields.copy(entrance = it) },
                    readOnly = !isEditing,
                    singleLine = true,
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                    modifier = Modifier.focusRequester(entranceFocus),
                    keyboardActions = KeyboardActions(
                        onNext = { floorFocus.requestFocus() }
                    ),
                    startText = { Text(stringResource(CoreR.string.ordering_field_entrance)) }
                )
                OrderingTextField(
                    value = fields.floor,
                    onValueChange = { fields = fields.copy(floor = it) },
                    readOnly = !isEditing,
                    singleLine = true,
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                    modifier = Modifier.focusRequester(floorFocus),
                    keyboardActions = KeyboardActions(
                        onNext = { apartmentFocus.requestFocus() }
                    ),
                    startText = { Text(stringResource(CoreR.string.ordering_field_floor)) }
                )
                OrderingTextField(
                    value = fields.apartment,
                    onValueChange = { fields = fields.copy(apartment = it) },
                    readOnly = !isEditing,
                    singleLine = true,
                    imeAction = ImeAction.Next,
                    modifier = Modifier.focusRequester(apartmentFocus),
                    keyboardActions = KeyboardActions(
                        onNext = { intercomFocus.requestFocus() }
                    ),
                    startText = { Text(stringResource(CoreR.string.ordering_field_apartment)) }
                )
                OrderingTextField(
                    value = fields.intercom,
                    onValueChange = { fields = fields.copy(intercom = it) },
                    readOnly = !isEditing,
                    singleLine = true,
                    imeAction = ImeAction.Next,
                    modifier = Modifier.focusRequester(intercomFocus),
                    keyboardActions = KeyboardActions(
                        onNext = { commentFocus.requestFocus() }
                    ),
                    startText = { Text(stringResource(CoreR.string.ordering_field_intercom)) }
                )
                OrderingTextField(
                    value = fields.comment,
                    onValueChange = { fields = fields.copy(comment = it) },
                    readOnly = !isEditing,
                    singleLine = true,
                    modifier = Modifier.focusRequester(commentFocus),
                    keyboardActions = KeyboardActions(
                        onAny = {
                            isEditing = false
                            keyboardController?.hide()
                        }
                    ),
                    startText = { Text(stringResource(CoreR.string.ordering_field_comment)) }
                )
            }
            Button(
                enabled = state.address.isNotEmpty(),
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .imePadding().navigationBarsPadding(),
                onClick = { viewModel.dispatch(OrderingEvent.MakeOrder(fields)) }
            ) {
                Text(stringResource(CoreR.string.ordering_button_create_order))
            }
        }
    }

    if (state.alert != null) {
        val onDismiss = {
            viewModel.dispatch(OrderingEvent.DismissAlert)
        }
        Alert(
            message = stringResource(state.alert),
            onDismiss = onDismiss,
            buttons = { AlertButton(onClick = onDismiss) }
        )
    }

    if (state.progress) {
        LoadingScrim()
    }
}

@Composable
private fun OrderingTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    singleLine: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    startText: @Composable () -> Unit
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 18.dp, bottom = 4.dp),
        readOnly = readOnly,
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        textStyle = MaterialTheme.typography.body1,
        keyboardActions = keyboardActions,
        decorationBox = { innerTextField ->
            Row {
                startText()
                innerTextField()
            }
        }
    )
    Divider()
}