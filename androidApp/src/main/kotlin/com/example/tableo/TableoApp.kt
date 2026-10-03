package com.example.tableo

import android.app.Application
import com.example.tableo.di.initKoin
import org.koin.android.ext.koin.androidContext

/** Startet Koin beim App-Start, mit Context für GPS, Benachrichtigungen und DataStore. */
class TableoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@TableoApp)
        }
    }
}
