package com.ltthuc.testcomponent.presentation.model

import androidx.compose.runtime.Immutable

@Immutable
data class TestComponentState(
    val selectedTab: Int = 0,
)
