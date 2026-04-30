package com.ltthuc.testcomponent.presentation.model

sealed interface TestComponentEvent {
    data class SelectTab(val index: Int) : TestComponentEvent
}
