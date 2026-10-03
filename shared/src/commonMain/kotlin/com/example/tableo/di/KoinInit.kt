package com.example.tableo.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

/**
 * Startet Koin einmal beim App-Start. Android ruft das aus der Application auf
 * (mit androidContext), iOS aus dem MainViewController.
 */
fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(appModule, platformModule)
    }
}
