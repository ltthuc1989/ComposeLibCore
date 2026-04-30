package com.trithuc.app.di

import com.ltthuc.utils.logging.ILogger
import com.ltthuc.utils.logging.TimberLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object LoggingModule {

    @Provides
    @Singleton
    fun provideLogger(): ILogger = TimberLogger()
}
