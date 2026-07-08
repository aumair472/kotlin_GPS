package com.geosnap.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.geosnap.core.database.dao.LocationDao
import com.geosnap.core.database.dao.MediaDao
import com.geosnap.core.database.dao.ReportDao
import com.geosnap.core.database.dao.ReportExportDao
import com.geosnap.core.database.dao.ReportMediaDao
import com.geosnap.core.database.dao.TemplateDao
import com.geosnap.core.database.entity.LocationEntity
import com.geosnap.core.database.entity.MediaEntity
import com.geosnap.core.database.entity.ReportEntity
import com.geosnap.core.database.entity.ReportExportEntity
import com.geosnap.core.database.entity.ReportMediaEntity
import com.geosnap.core.database.entity.TemplateEntity

/**
 * Local source of truth. Schemas are exported under `app/schemas/`; never use destructive
 * migration in release. Foreign keys enabled (configured in [DatabaseModule]).
 */
@Database(
    entities = [
        MediaEntity::class,
        LocationEntity::class,
        ReportEntity::class,
        ReportMediaEntity::class,
        ReportExportEntity::class,
        TemplateEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class GeoSnapDatabase : RoomDatabase() {
    abstract fun mediaDao(): MediaDao
    abstract fun locationDao(): LocationDao
    abstract fun reportDao(): ReportDao
    abstract fun reportMediaDao(): ReportMediaDao
    abstract fun reportExportDao(): ReportExportDao
    abstract fun templateDao(): TemplateDao

    companion object {
        const val NAME = "geosnap.db"
    }
}
