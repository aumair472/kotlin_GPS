package com.geosnap.core.data.di

import com.geosnap.core.common.AppCompatLocaleManager
import com.geosnap.core.common.LocaleManager
import com.geosnap.core.common.SystemTimeSource
import com.geosnap.core.common.TimeSource
import com.geosnap.core.data.MediaRepository
import com.geosnap.core.data.MediaRepositoryImpl
import com.geosnap.core.data.ReportRepository
import com.geosnap.core.data.ReportRepositoryImpl
import com.geosnap.core.data.SettingsRepository
import com.geosnap.core.data.SettingsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton
    abstract fun bindMediaRepository(impl: MediaRepositoryImpl): MediaRepository

    @Binds @Singleton
    abstract fun bindReportRepository(impl: ReportRepositoryImpl): ReportRepository

    @Binds @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds @Singleton
    abstract fun bindTimeSource(impl: SystemTimeSource): TimeSource

    @Binds @Singleton
    abstract fun bindLocaleManager(impl: AppCompatLocaleManager): LocaleManager
}
