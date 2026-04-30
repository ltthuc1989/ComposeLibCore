package com.trithuc.app

import com.ltthuc.preferences.api.DatastoreRepository
import com.ltthuc.ui.base.BaseViewModel
import com.ltthuc.utils.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope

@HiltViewModel
class AppSharedViewModel @Inject constructor(
    private val datastore: DatastoreRepository,
) : BaseViewModel() {

    val themeMode: StateFlow<ThemeMode> = datastore.themeMode()
        .stateIn(viewModelScope, SharingStarted.Eagerly, ThemeMode.System)

    fun setThemeMode(mode: ThemeMode) = launchAsync(showProgress = false) {
        datastore.setThemeMode(mode)
    }
}
