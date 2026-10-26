package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val name: String,
  val languageId: String,
  val description: String = "",
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "project_files")
data class ProjectFileEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val projectId: Long,
  val name: String,
  val content: String,
  val isMain: Boolean = false
)
