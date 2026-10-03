package com.example.tableo.di

import com.example.tableo.data.repository.LocationRepository
import com.example.tableo.data.repository.NotificationRepository
import com.example.tableo.data.repository.impl.DATA_STORE_FILE_NAME
import com.example.tableo.data.repository.impl.LocationRepositoryImpl
import com.example.tableo.data.repository.impl.NotificationRepositoryImpl
import com.example.tableo.data.repository.impl.createDataStore
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

/** iOS-Teil: GPS über CoreLocation, Benachrichtigungen über UserNotifications, DataStore im Documents-Ordner. */
actual val platformModule: Module = module {
    single<LocationRepository> { LocationRepositoryImpl() }
    single<NotificationRepository> { NotificationRepositoryImpl() }
    single { createDataStore { "${documentDirectory()}/$DATA_STORE_FILE_NAME" } }
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val url = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null
    )
    return requireNotNull(url?.path) { "Documents-Ordner nicht gefunden" }
}
