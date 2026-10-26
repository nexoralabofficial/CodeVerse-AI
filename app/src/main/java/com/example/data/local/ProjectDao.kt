package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
  @Query("SELECT * FROM projects ORDER BY updatedAt DESC")
  fun getAllProjects(): Flow<List<ProjectEntity>>

  @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
  suspend fun getProjectById(id: Long): ProjectEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProject(project: ProjectEntity): Long

  @Update
  suspend fun updateProject(project: ProjectEntity)

  @Delete
  suspend fun deleteProject(project: ProjectEntity)

  @Query("SELECT * FROM project_files WHERE projectId = :projectId ORDER BY isMain DESC, name ASC")
  fun getFilesForProject(projectId: Long): Flow<List<ProjectFileEntity>>

  @Query("SELECT * FROM project_files WHERE projectId = :projectId ORDER BY isMain DESC, name ASC")
  suspend fun getFilesForProjectOnce(projectId: Long): List<ProjectFileEntity>

  @Query("SELECT * FROM project_files WHERE id = :id LIMIT 1")
  suspend fun getFileById(id: Long): ProjectFileEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertFile(file: ProjectFileEntity): Long

  @Update
  suspend fun updateFile(file: ProjectFileEntity)

  @Query("UPDATE project_files SET content = :content WHERE id = :fileId")
  suspend fun updateFileContent(fileId: Long, content: String)

  @Delete
  suspend fun deleteFile(file: ProjectFileEntity)

  @Query("DELETE FROM project_files WHERE projectId = :projectId")
  suspend fun deleteFilesForProject(projectId: Long)
}
