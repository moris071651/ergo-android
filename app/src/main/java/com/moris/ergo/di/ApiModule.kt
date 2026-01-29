package com.moris.ergo.di

import android.content.Context
import com.moris.ergo.data.api.ErgoServerApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    @Singleton
    fun provideErgoServerApi(@ApplicationContext context: Context):
            ErgoServerApi = ErgoServerApi(context)
}
