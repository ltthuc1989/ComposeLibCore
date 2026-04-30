package com.ltthuc.testcomponent.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ltthuc.testcomponent.presentation.model.TestComponentEvent
import com.ltthuc.testcomponent.presentation.model.TestComponentState
import com.ltthuc.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TestComponentViewModel @Inject constructor() : BaseViewModel() {

    var state by mutableStateOf(TestComponentState())
        private set

    fun dispatch(event: TestComponentEvent) {
        state = when (event) {
            is TestComponentEvent.SelectTab -> state.copy(selectedTab = event.index)
        }
    }
}
