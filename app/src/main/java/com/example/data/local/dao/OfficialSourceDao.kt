package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.OfficialSourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OfficialSourceDao {

    @Query("SELECT * FROM official_sources WHERE isVisible = 1 ORDER BY sortOrder ASC, name ASC")
    fun getAllVisibleSources(): Flow<List<OfficialSourceEntity>>

    @Query("SELECT * FROM official_sources ORDER BY sortOrder ASC, name ASC")
    fun getAllSources(): Flow<List<OfficialSourceEntity>>

    @Query("SELECT * FROM official_sources WHERE isVisible = 1 AND sectionType = :sectionType ORDER BY sortOrder ASC, name ASC")
    fun getSourcesBySection(sectionType: String): Flow<List<OfficialSourceEntity>>

    @Query("SELECT * FROM official_sources WHERE isVisible = 1 AND isFavorite = 1 ORDER BY sortOrder ASC, name ASC")
    fun getFavoriteSources(): Flow<List<OfficialSourceEntity>>

    @Query("SELECT * FROM official_sources WHERE id = :id LIMIT 1")
    suspend fun getSourceById(id: String): OfficialSourceEntity?

    @Query("""
        SELECT * FROM official_sources 
        WHERE isVisible = 1 
        AND (name LIKE '%' || :query || '%' 
             OR companyOrEntity LIKE '%' || :query || '%' 
             OR description LIKE '%' || :query || '%' 
             OR category LIKE '%' || :query || '%' 
             OR requirements LIKE '%' || :query || '%')
        ORDER BY sortOrder ASC, name ASC
    """)
    fun searchSources(query: String): Flow<List<OfficialSourceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(source: OfficialSourceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sources: List<OfficialSourceEntity>)

    @Update
    suspend fun update(source: OfficialSourceEntity)

    @Query("UPDATE official_sources SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: String, isFavorite: Boolean)

    @Query("UPDATE official_sources SET isVisible = :isVisible WHERE id = :id")
    suspend fun updateVisibility(id: String, isVisible: Boolean)

    @Query("DELETE FROM official_sources WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT COUNT(*) FROM official_sources")
    suspend fun getCount(): Int

    @Query("SELECT * FROM official_sources")
    suspend fun getAllList(): List<OfficialSourceEntity>
}
