package com.example.personalworkoutnotebook

import android.app.Application
import com.example.personalworkoutnotebook.dao.DbModule
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        DbModule.initDb(applicationContext)
    }
}