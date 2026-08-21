package com.ltthuc.appupdate.api

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AppUpdateEntryPoint {
    fun appUpdateController(): AppUpdateController
}
