package com.moris.ergo.di

import com.moris.ergo.data.repository.AddressRepository
import com.moris.ergo.data.repository.AddressRepositoryImpl
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
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
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
