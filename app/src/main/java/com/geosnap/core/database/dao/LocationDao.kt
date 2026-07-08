package com.geosnap.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.geosnap.core.database.entity.LocationEntity

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(location: LocationEntity)

    @Query("SELECT * FROM locations WHERE id = :id")
    suspend fun getById(id: String): LocationEntity?

    @Query("DELETE FROM locations WHERE id NOT IN (SELECT location_id FROM media_items WHERE location_id IS NOT NULL) AND id NOT IN (SELECT report_location_id FROM reports WHERE report_location_id IS NOT NULL)")
    suspend fun deleteOrphans()
}
