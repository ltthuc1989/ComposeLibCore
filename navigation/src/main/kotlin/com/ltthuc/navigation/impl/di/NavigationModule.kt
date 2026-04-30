package com.ltthuc.navigation.impl.di

import com.ltthuc.navigation.api.Navigator
import com.ltthuc.navigation.impl.DefaultNavigator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface NavigationModule {

    @Singleton
    @Binds
    fun provideNavigator(navigator: DefaultNavigator): Navigator
}