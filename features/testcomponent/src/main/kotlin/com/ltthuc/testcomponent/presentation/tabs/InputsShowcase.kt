package com.ltthuc.testcomponent.presentation.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.ltthuc.ui.components.AppTextField
import com.ltthuc.ui.themes.Dimens

@Composable
fun InputsShowcase(modifier: Modifier = Modifier) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(Dimens.spacingM),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingM),
    ) {
        SectionLabel("Plain")
        AppTextField(
            value = name,
            onValueChange = { name = it },
            label = "Name",
            placeholder = "Enter your name",
        )
        SectionLabel("With error")
        AppTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            placeholder = "user@example.com",
            leadingIcon = Icons.Default.Email,
            errorText = if (email.isNotEmpty() && !email.contains("@")) "Invalid email" else null,
            helperText = if (email.isEmpty()) "We never share your email" else null,
            keyboardType = KeyboardType.Email,
        )
        SectionLabel("Password (toggle visibility)")
        AppTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            leadingIcon = Icons.Default.Lock,
            trailingIcon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
            onTrailingIconClick = { passwordVisible = !passwordVisible },
            isPassword = !passwordVisible,
        )
        SectionLabel("Multiline")
        AppTextField(
            value = notes,
            onValueChange = { notes = it },
            label = "Notes",
            placeholder = "Up to 5 lines",
            singleLine = false,
            maxLines = 5,
        )
    }
}
