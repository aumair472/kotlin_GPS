package com.geosnap.core.database.di

import android.content.Context
import androidx.room.Room
import com.geosnap.core.database.GeoSnapDatabase
import com.geosnap.core.database.dao.LocationDao
import com.geosnap.core.database.dao.MediaDao
import com.geosnap.core.database.dao.ReportDao
import com.geosnap.core.database.dao.ReportExportDao
import com.geosnap.core.database.dao.ReportMediaDao
import com.geosnap.core.database.dao.TemplateDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GeoSnapDatabase =
        Room.databaseBuilder(context, GeoSnapDatabase::class.java, GeoSnapDatabase.NAME)
            // No destructive fallback: migrations are explicit and tested (DATABASE.md).
            .build()

    @Provides fun provideMediaDao(db: GeoSnapDatabase): MediaDao = db.mediaDao()
    @Provides fun provideLocationDao(db: GeoSnapDatabase): LocationDao = db.locationDao()
    @Provides fun provideReportDao(db: GeoSnapDatabase): ReportDao = db.reportDao()
    @Provides fun provideReportMediaDao(db: GeoSnapDatabase): ReportMediaDao = db.reportMediaDao()
    @Provides fun provideReportExportDao(db: GeoSnapDatabase): ReportExportDao = db.reportExportDao()
    @Provides fun provideTemplateDao(db: GeoSnapDatabase): TemplateDao = db.templateDao()
}
