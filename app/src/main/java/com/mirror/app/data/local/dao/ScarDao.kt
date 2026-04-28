package com.mirror.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mirror.app.data.local.entity.ScarEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScarDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(scar: ScarEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplaceAll(scars: List<ScarEntity>)

    @Query("SELECT * FROM scars ORDER BY startDate ASC")
    fun observeAll(): Flow<List<ScarEntity>>

    @Query("SELECT * FROM scars ORDER BY startDate ASC")
    suspend fun getAll(): List<ScarEntity>

    @Query("DELETE FROM scars")
    suspend fun deleteAll()
}
