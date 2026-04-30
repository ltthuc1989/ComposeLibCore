package com.ltthuc.network.impl.di

import android.content.Context
import com.ltthuc.network.api.SampleApi
import com.ltthuc.network.api.TokenRefresher
import com.ltthuc.network.impl.ApiService
import com.ltthuc.network.impl.NoOpTokenRefresher
import com.ltthuc.network.impl.TokenAuthenticator
import com.ltthuc.network.impl.api.DefaultSampleApi
import com.ltthuc.network.impl.buildApiService
import com.ltthuc.network.impl.buildOkHttpClient
import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient

@Module
@InstallIn(SingletonComponent::class)
internal interface NetworkModule {

    @Binds
    fun bindSampleApi(impl: DefaultSampleApi): SampleApi

    @Binds
    fun bindTokenRefresher(impl: NoOpTokenRefresher): TokenRefresher

    companion object {

        private const val DEFAULT_BASE_URL = "https://jsonplaceholder.typicode.com/"

        @Provides
        @Singleton
        fun provideGson(): Gson = Gson()

        @Provides
        @Singleton
        fun provideOkHttpClient(authenticator: TokenAuthenticator): OkHttpClient =
            buildOkHttpClient(authenticator)

        @Provides
        @Singleton
        fun provideApiService(
            @ApplicationContext context: Context,
            client: OkHttpClient,
            gson: Gson,
        ): ApiService = buildApiService(context, client, DEFAULT_BASE_URL, gson)
    }
}
