package com.ltthuc.appupdate.di

import android.content.Context
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AppUpdateModule {

    @Singleton
    @Provides
    fun providesAppUpdateManager(@ApplicationContext context: Context): AppUpdateManager =
        AppUpdateManagerFactory.create(context)
}
