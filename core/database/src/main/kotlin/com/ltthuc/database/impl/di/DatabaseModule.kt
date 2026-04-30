package com.ltthuc.database.impl.di

import android.content.Context
import com.ltthuc.database.api.SampleRepository
import com.ltthuc.database.impl.AppDatabase
import com.ltthuc.database.impl.buildDatabase
import com.ltthuc.database.impl.dao.SampleDao
import com.ltthuc.database.impl.repositories.DefaultSampleRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface DatabaseModule {

    @Binds
    @Singleton
    fun bindSampleRepository(impl: DefaultSampleRepository): SampleRepository

    companion object {

        @Provides
        @Singleton
        fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
            buildDatabase(context)

        @Provides
        fun provideSampleDao(database: AppDatabase): SampleDao = database.sampleDao()
    }
}
