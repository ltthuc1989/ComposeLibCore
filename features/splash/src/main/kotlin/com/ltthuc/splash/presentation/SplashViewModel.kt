package com.ltthuc.splash.presentation

import androidx.lifecycle.viewModelScope
import com.ltthuc.navigation.api.Navigator
import com.ltthuc.navigation.api.model.Destination
import com.ltthuc.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltViewModel
internal class SplashViewModel @Inject constructor(
    private val navigator: Navigator,
) : BaseViewModel() {

    init {
        viewModelScope.launch {
            delay(MIN_SPLASH_DURATION_MS)
            navigator.goTo(Destination.Main)
        }
    }

    private companion object {
        const val MIN_SPLASH_DURATION_MS = 1000L
    }
}
