package com.ltthuc.billing.impl.di

import com.ltthuc.billing.api.BillingApi
import com.ltthuc.billing.impl.BillingManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class BillingModule {

    @Binds
    @Singleton
    abstract fun bindBillingApi(impl: BillingManagerImpl): BillingApi
}
