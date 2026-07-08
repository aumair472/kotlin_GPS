package com.geosnap.core.location.di

import android.content.Context
import com.geosnap.core.location.FusedLocationGateway
import com.geosnap.core.location.LocationGateway
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationProvidesModule {
    @Provides
    @Singleton
    fun provideFusedClient(@ApplicationContext context: Context): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationBindsModule {
    @dagger.Binds
    @Singleton
    abstract fun bindLocationGateway(impl: FusedLocationGateway): LocationGateway

    @dagger.Binds
    @Singleton
    abstract fun bindAddressResolver(impl: com.geosnap.core.location.AndroidAddressResolver): com.geosnap.core.location.AddressResolver
}
