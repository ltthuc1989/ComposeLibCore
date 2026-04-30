package com.ltthuc.preferences.impl.di

import com.ltthuc.preferences.api.DatastoreRepository
import com.ltthuc.preferences.impl.DefaultDatastoreRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface DatastoreModule {
    @Binds
    fun provideDatastoreRepository(repository: DefaultDatastoreRepository): DatastoreRepository
}