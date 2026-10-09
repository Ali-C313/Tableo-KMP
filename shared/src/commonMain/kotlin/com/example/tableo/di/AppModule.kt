package com.example.tableo.di

import com.example.tableo.data.remote.ExchangeRateApi
import com.example.tableo.data.repository.AuthRepository
import com.example.tableo.data.repository.ExchangeRateRepository
import com.example.tableo.data.repository.MenuRepository
import com.example.tableo.data.repository.OnboardingRepository
import com.example.tableo.data.repository.DeliveryPreferencesRepository
import com.example.tableo.data.repository.LanguageRepository
import com.example.tableo.data.repository.ThemeRepository
import com.example.tableo.data.repository.UserRepository
import com.example.tableo.data.repository.OrderRepository
import com.example.tableo.data.repository.RestaurantRepository
import com.example.tableo.data.repository.StaffRepository
import com.example.tableo.data.repository.StorageRepository
import com.example.tableo.data.repository.impl.AuthRepositoryImpl
import com.example.tableo.data.repository.impl.ExchangeRateRepositoryImpl
import com.example.tableo.data.repository.impl.MenuRepositoryImpl
import com.example.tableo.data.repository.impl.OnboardingRepositoryImpl
import com.example.tableo.data.repository.impl.DeliveryPreferencesRepositoryImpl
import com.example.tableo.data.repository.impl.LanguageRepositoryImpl
import com.example.tableo.data.repository.impl.ThemeRepositoryImpl
import com.example.tableo.data.repository.impl.UserRepositoryImpl
import com.example.tableo.data.repository.impl.OrderRepositoryImpl
import com.example.tableo.data.repository.impl.RestaurantRepositoryImpl
import com.example.tableo.data.repository.impl.StaffRepositoryImpl
import com.example.tableo.data.repository.impl.StorageRepositoryImpl
import com.example.tableo.ui.customer.auth.CustomerAuthViewModel
import com.example.tableo.ui.customer.orderDetail.MyOrderDetailViewModel
import com.example.tableo.ui.customer.orders.MyOrdersViewModel
import com.example.tableo.ui.customer.orders.OrderUpdatesViewModel
import com.example.tableo.ui.customer.tracking.OrderTrackingViewModel
import com.example.tableo.ui.driver.orders.DriverOrdersViewModel
import com.example.tableo.ui.guest.cart.CartViewModel
import com.example.tableo.ui.guest.delivery.DeliveryViewModel
import com.example.tableo.ui.guest.menu.ExchangeRateViewModel
import com.example.tableo.ui.guest.menu.MenuViewModel
import com.example.tableo.ui.guest.restaurantList.RestaurantListViewModel
import com.example.tableo.ui.onboarding.OnboardingViewModel
import com.example.tableo.ui.settings.SettingsViewModel
import com.example.tableo.ui.splash.SplashViewModel
import com.example.tableo.ui.staff.analytics.StaffAnalyticsViewModel
import com.example.tableo.ui.staff.archive.StaffArchiveViewModel
import com.example.tableo.ui.staff.dashboard.StaffDashboardViewModel
import com.example.tableo.ui.staff.menu.StaffMenuItemEditViewModel
import com.example.tableo.ui.staff.menu.StaffMenuViewModel
import com.example.tableo.ui.staff.shift.StaffShiftViewModel
import com.example.tableo.ui.staff.team.StaffTeamViewModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.storage.storage
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** Plattform-Teil: GPS, Benachrichtigungen und DataStore sind auf Android und iOS verschieden. */
expect val platformModule: Module

val appModule = module {
    single { Firebase.firestore }
    single { Firebase.auth }
    single { Firebase.storage }

    single<MenuRepository> { MenuRepositoryImpl(get()) }
    single<OrderRepository> { OrderRepositoryImpl(get()) }
    single<RestaurantRepository> { RestaurantRepositoryImpl(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<StaffRepository> { StaffRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<OnboardingRepository> { OnboardingRepositoryImpl(get()) }
    single<ThemeRepository> { ThemeRepositoryImpl(get()) }
    single<LanguageRepository> { LanguageRepositoryImpl(get()) }
    single<DeliveryPreferencesRepository> { DeliveryPreferencesRepositoryImpl(get()) }

    single { ExchangeRateApi() }
    single<ExchangeRateRepository> { ExchangeRateRepositoryImpl(get()) }

    single<StorageRepository> { StorageRepositoryImpl(get()) }

    // bewusst als "single" statt viewModelOf, damit derselbe Warenkorb über mehrere Screens hinweg erhalten bleibt
    single { CartViewModel(get(), get()) }
    // genauso single, damit Adresse und Abholung/Lieferung überall gleich sind
    single { DeliveryViewModel(get(), get()) }
    // läuft die ganze Zeit mit und meldet Änderungen an den eigenen Bestellungen
    single { OrderUpdatesViewModel(get(), get(), get()) }
    // genauso für Mitarbeiter: Dienst-Schalter und Nachricht bei neuen Bestellungen
    single { StaffShiftViewModel(get(), get(), get(), get()) }

    viewModelOf(::MenuViewModel)
    viewModelOf(::ExchangeRateViewModel)
    viewModelOf(::RestaurantListViewModel)
    viewModelOf(::StaffDashboardViewModel)
    viewModelOf(::StaffArchiveViewModel)
    viewModelOf(::StaffMenuViewModel)
    viewModelOf(::StaffMenuItemEditViewModel)
    viewModelOf(::StaffTeamViewModel)
    viewModelOf(::CustomerAuthViewModel)
    viewModelOf(::MyOrdersViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::StaffAnalyticsViewModel)
    viewModelOf(::SplashViewModel)
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::DriverOrdersViewModel)
    viewModelOf(::OrderTrackingViewModel)
    viewModelOf(::MyOrderDetailViewModel)
}