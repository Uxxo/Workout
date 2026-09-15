package com.example.personalworkoutnotebook.ui.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.personalworkoutnotebook.R
import com.example.personalworkoutnotebook.dao.DbModule // Импортируем наш модуль БД
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class SettingsActivity : AppCompatActivity() {

    // Контракт для ЭКСПОРТА (создание файла)
    private val exportLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri: Uri? ->
        if (uri != null) {
            val success = BackupManagerII.exportDatabase(this, uri)
            if (success) {
                Toast.makeText(this, "База данных успешно сохранена!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Ошибка при экспорте файла", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Контракт для ИМПОРТА (выбор файла)
    private val importLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                // Закрываем Room, чтобы отпустить файловые дескрипторы
                DbModule.closeDb()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val success = BackupManagerII.importDatabase(this, uri)
            if (success) {
                Toast.makeText(this, "Данные успешно импортированы! Перезапуск...", Toast.LENGTH_LONG).show()

                // Чистый перезапуск приложения для повторной инициализации DbModule с новыми файлами
                val intent = packageManager.getLaunchIntentForPackage(packageName)
                intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Ошибка при восстановлении данных", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val btnExport = findViewById<Button>(R.id.btn_export_db)
        val btnImport = findViewById<Button>(R.id.btn_import_db)

        btnExport.setOnClickListener {
            try {
                // Подготавливаем файл (сливаем все последние данные)
                DbModule.prepareDbForExport()
                // Запускаем системное окно сохранения
                exportLauncher.launch("workout_backup_${System.currentTimeMillis()}.db")
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Ошибка подготовки данных: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        btnImport.setOnClickListener {
            importLauncher.launch(arrayOf("*/*"))
        }
    }
}

// Утилитарный объект для работы с файлами Room
object BackupManagerII {
    private const val DATABASE_NAME = "workouts-db"

    fun exportDatabase(context: Context, destUri: Uri): Boolean {
        return try {
            val dbFile: File = context.getDatabasePath(DATABASE_NAME)
            if (!dbFile.exists()) return false

            context.contentResolver.openOutputStream(destUri)?.use { outputStream ->
                FileInputStream(dbFile).use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun importDatabase(context: Context, srcUri: Uri): Boolean {
        return try {
            val dbFile: File = context.getDatabasePath(DATABASE_NAME)

            // Удаляем старые файлы логов перед перезаписью, чтобы они не конфликтовали
            File(dbFile.path + "-wal").delete()
            File(dbFile.path + "-shm").delete()
            if (dbFile.exists()) dbFile.delete()

            // Записываем новый файл из бэкапа
            context.contentResolver.openInputStream(srcUri)?.use { inputStream ->
                FileOutputStream(dbFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}


