package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.model.LanguageId
import com.example.model.LanguageRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [ProjectEntity::class, ProjectFileEntity::class],
  version = 1,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun projectDao(): ProjectDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "codeverse_ai_db"
        )
          .addCallback(object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
              super.onCreate(db)
              CoroutineScope(Dispatchers.IO).launch {
                seedInitialProjects(getDatabase(context))
              }
            }
          })
          .build()
        INSTANCE = instance
        instance
      }
    }

    private suspend fun seedInitialProjects(database: AppDatabase) {
      val dao = database.projectDao()

      // 1. Python Project
      val pyLang = LanguageRegistry.getById(LanguageId.PYTHON)
      val pyProjectId = dao.insertProject(
        ProjectEntity(
          name = "Python Algorithms",
          languageId = LanguageId.PYTHON.name,
          description = "Fibonacci sequence & math algorithms"
        )
      )
      dao.insertFile(
        ProjectFileEntity(
          projectId = pyProjectId,
          name = "main.py",
          content = pyLang.defaultTemplate,
          isMain = true
        )
      )

      // 2. Web Project
      val htmlLang = LanguageRegistry.getById(LanguageId.HTML)
      val webProjectId = dao.insertProject(
        ProjectEntity(
          name = "Cyber Web Canvas",
          languageId = LanguageId.HTML.name,
          description = "Interactive HTML5 card with live DOM preview"
        )
      )
      dao.insertFile(
        ProjectFileEntity(
          projectId = webProjectId,
          name = "index.html",
          content = htmlLang.defaultTemplate,
          isMain = true
        )
      )

      // 3. C++ Project
      val cppLang = LanguageRegistry.getById(LanguageId.CPP)
      val cppProjectId = dao.insertProject(
        ProjectEntity(
          name = "C++ Performance",
          languageId = LanguageId.CPP.name,
          description = "High-performance vector operations"
        )
      )
      dao.insertFile(
        ProjectFileEntity(
          projectId = cppProjectId,
          name = "main.cpp",
          content = cppLang.defaultTemplate,
          isMain = true
        )
      )

      // 4. Java Project
      val javaLang = LanguageRegistry.getById(LanguageId.JAVA)
      val javaProjectId = dao.insertProject(
        ProjectEntity(
          name = "Java Modules",
          languageId = LanguageId.JAVA.name,
          description = "Object-oriented code structure"
        )
      )
      dao.insertFile(
        ProjectFileEntity(
          projectId = javaProjectId,
          name = "Main.java",
          content = javaLang.defaultTemplate,
          isMain = true
        )
      )
    }
  }
}
