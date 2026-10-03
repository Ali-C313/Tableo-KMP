package com.example.tableo.di

import com.example.tableo.data.repository.LocationRepository
import com.example.tableo.data.repository.NotificationRepository
import com.example.tableo.data.repository.impl.DATA_STORE_FILE_NAME
import com.example.tableo.data.repository.impl.LocationRepositoryImpl
import com.example.tableo.data.repository.impl.NotificationRepositoryImpl
import com.example.tableo.data.repository.impl.createDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

/** Alles, was auf Android den Context braucht: GPS, Benachrichtigungen, lokaler Speicher. */
actual val platformModule: Module = module {
    single<LocationRepository> { LocationRepositoryImpl(androidContext()) }
    single<NotificationRepository> { NotificationRepositoryImpl(androidContext()) }
    single { createDataStore { androidContext().filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath } }
}
