package com.ltthuc.ui.base

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ltthuc.utils.AppException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    sealed interface AppError {
        data class App(val exception: AppException) : AppError
        data class Message(@StringRes val resId: Int) : AppError
        data class Raw(val text: String) : AppError
    }

    private val _progress = MutableStateFlow(false)
    val progress: StateFlow<Boolean> = _progress.asStateFlow()

    private val _errorMessage = MutableSharedFlow<AppError>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val errorMessage: SharedFlow<AppError> = _errorMessage.asSharedFlow()

    protected open fun manageException(throwable: Throwable) {
        val error = when (throwable) {
            is AppException -> AppError.App(throwable)
            else -> AppError.Raw(throwable.localizedMessage ?: "Unknown error")
        }
        _errorMessage.tryEmit(error)
        _progress.value = false
    }

    private val handler = CoroutineExceptionHandler { _, throwable -> manageException(throwable) }

    protected fun launchAsync(
        showProgress: Boolean = true,
        onError: (Throwable) -> Unit = ::manageException,
        block: suspend CoroutineScope.() -> Unit,
    ): Job = viewModelScope.launch(handler) {
        if (showProgress) _progress.value = true
        try {
            block()
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (throwable: Throwable) {
            onError(throwable)
        } finally {
            if (showProgress) _progress.value = false
        }
    }
}
