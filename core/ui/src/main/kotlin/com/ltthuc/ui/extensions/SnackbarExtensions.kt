package com.ltthuc.ui.extensions

import androidx.compose.material3.SnackbarHostState
import com.ltthuc.ui.base.BaseViewModel

suspend fun SnackbarHostState.showError(error: BaseViewModel.AppError) {
    val message = when (error) {
        is BaseViewModel.AppError.Raw -> error.text
        is BaseViewModel.AppError.Message -> "" // resolution requires Context — caller should resolve before
        is BaseViewModel.AppError.App -> error.exception.message ?: "Error"
    }
    if (message.isNotEmpty()) {
        showSnackbar(message)
    }
}
