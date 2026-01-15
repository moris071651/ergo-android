package com.moris.ergo.di

import android.content.Context
import com.moris.ergo.data.api.AddressRepository
import com.moris.ergo.data.api.AddressRepositoryImpl
import com.moris.ergo.data.api.ErgoServerApi
import com.moris.ergo.data.repository.BookingRepository
import com.moris.ergo.data.repository.BookingRepositoryImpl
import com.moris.ergo.data.repository.ListingRepository
import com.moris.ergo.data.repository.ListingRepositoryImpl
import com.moris.ergo.data.repository.UserRepository
import com.moris.ergo.data.repository.UserRepositoryImpl
import com.moris.ergo.data.repository.WorkerRepository
import com.moris.ergo.data.repository.WorkerRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideErgoServerApi(@ApplicationContext context: Context):
            ErgoServerApi = ErgoServerApi(context)

    @Provides
    @Singleton
    fun provideListingRepository(api: ErgoServerApi): ListingRepository =
        ListingRepositoryImpl(api)

    @Provides
    @Singleton
    fun provideUserRepository(api: ErgoServerApi): UserRepository =
        UserRepositoryImpl(api)

    @Provides
    @Singleton
    fun provideAddressRepository(api: ErgoServerApi): AddressRepository =
        AddressRepositoryImpl(api)

    @Provides
    @Singleton
    fun provideWorkerRepository(api: ErgoServerApi): WorkerRepository =
        WorkerRepositoryImpl(api)

    @Provides
    @Singleton
    fun provideBookingRepository(api: ErgoServerApi): BookingRepository =
        BookingRepositoryImpl(api)
}
