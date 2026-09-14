package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.AdminAuditLogEntity
import com.example.data.local.entities.AppSectionConfigEntity
import com.example.data.local.entities.CustomFieldDefinitionEntity
import com.example.data.local.entities.SystemCategoryEntity
import com.example.data.local.entities.SystemExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppSectionConfigDao {
    @Query("SELECT * FROM app_section_configs ORDER BY sortOrder ASC")
    fun getAllSections(): Flow<List<AppSectionConfigEntity>>

    @Query("SELECT * FROM app_section_configs WHERE isVisible = 1 ORDER BY sortOrder ASC")
    fun getVisibleSections(): Flow<List<AppSectionConfigEntity>>

    @Query("SELECT * FROM app_section_configs WHERE isVisible = 1 AND showInBottomNav = 1 ORDER BY bottomNavOrder ASC, sortOrder ASC")
    fun getBottomNavSections(): Flow<List<AppSectionConfigEntity>>

    @Query("SELECT * FROM app_section_configs WHERE id = :id LIMIT 1")
    suspend fun getSectionById(id: String): AppSectionConfigEntity?

    @Query("SELECT * FROM app_section_configs WHERE isDefaultStartScreen = 1 LIMIT 1")
    suspend fun getDefaultStartScreen(): AppSectionConfigEntity?

    @Query("SELECT COUNT(*) FROM app_section_configs")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(section: AppSectionConfigEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sections: List<AppSectionConfigEntity>)

    @Query("UPDATE app_section_configs SET isVisible = :visible WHERE id = :id")
    suspend fun updateVisibility(id: String, visible: Boolean)

    @Query("UPDATE app_section_configs SET showInBottomNav = :showInBottom, bottomNavOrder = :order WHERE id = :id")
    suspend fun updateBottomNavConfig(id: String, showInBottom: Boolean, order: Int)

    @Query("UPDATE app_section_configs SET isDefaultStartScreen = (CASE WHEN id = :id THEN 1 ELSE 0 END)")
    suspend fun setDefaultStartScreen(id: String)

    @Query("UPDATE app_section_configs SET displayName = :name, description = :desc, iconName = :icon, sortOrder = :order, isVisible = :visible WHERE id = :id")
    suspend fun updateSection(id: String, name: String, desc: String, icon: String, order: Int, visible: Boolean)

    @Query("DELETE FROM app_section_configs WHERE id = :id AND isCustom = 1")
    suspend fun deleteCustomSection(id: String)
}

@Dao
interface CustomFieldDefinitionDao {
    @Query("SELECT * FROM custom_field_definitions ORDER BY sortOrder ASC")
    fun getAllFields(): Flow<List<CustomFieldDefinitionEntity>>

    @Query("SELECT * FROM custom_field_definitions WHERE targetEntity = :target ORDER BY sortOrder ASC")
    fun getFieldsForTarget(target: String): Flow<List<CustomFieldDefinitionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(field: CustomFieldDefinitionEntity)

    @Query("DELETE FROM custom_field_definitions WHERE id = :id")
    suspend fun deleteField(id: String)
}

@Dao
interface SystemExpenseDao {
    @Query("SELECT * FROM system_expenses WHERE isDeleted = 0 ORDER BY date DESC, createdDate DESC")
    fun getAllActiveExpenses(): Flow<List<SystemExpenseEntity>>

    @Query("SELECT * FROM system_expenses WHERE isDeleted = 1 ORDER BY createdDate DESC")
    fun getDeletedExpenses(): Flow<List<SystemExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(expense: SystemExpenseEntity)

    @Query("UPDATE system_expenses SET isDeleted = 1, syncStatus = 'PENDING_SYNC' WHERE id = :id")
    suspend fun softDelete(id: String)

    @Query("UPDATE system_expenses SET isDeleted = 0, syncStatus = 'PENDING_SYNC' WHERE id = :id")
    suspend fun restore(id: String)

    @Query("DELETE FROM system_expenses WHERE id = :id")
    suspend fun permanentDelete(id: String)
}

@Dao
interface SystemCategoryDao {
    @Query("SELECT * FROM system_categories ORDER BY sortOrder ASC, name ASC")
    fun getAllCategories(): Flow<List<SystemCategoryEntity>>

    @Query("SELECT * FROM system_categories WHERE scope = :scope ORDER BY sortOrder ASC, name ASC")
    fun getCategoriesByScope(scope: String): Flow<List<SystemCategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(category: SystemCategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<SystemCategoryEntity>)

    @Query("DELETE FROM system_categories WHERE id = :id")
    suspend fun deleteCategory(id: String)
}

@Dao
interface AdminAuditLogDao {
    @Query("SELECT * FROM admin_audit_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<AdminAuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: AdminAuditLogEntity)

    @Query("DELETE FROM admin_audit_logs")
    suspend fun clearLogs()
}
