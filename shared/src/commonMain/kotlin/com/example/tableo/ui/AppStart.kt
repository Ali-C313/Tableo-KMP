package com.example.tableo.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.tableo.data.enums.LegalPage
import com.example.tableo.data.enums.OrderType
import com.example.tableo.data.model.StaffAccess
import com.example.tableo.navigation.Route
import com.example.tableo.ui.customer.account.AccountScreen
import com.example.tableo.ui.customer.auth.CustomerAuthScreen
import com.example.tableo.ui.customer.orderDetail.MyOrderDetailScreen
import com.example.tableo.ui.customer.orders.MyOrdersScreen
import com.example.tableo.ui.customer.orders.OrderUpdatesViewModel
import com.example.tableo.ui.staff.shift.StaffShiftViewModel
import com.example.tableo.ui.customer.tracking.OrderTrackingScreen
import com.example.tableo.ui.driver.orders.DriverOrdersScreen
import com.example.tableo.ui.guest.cart.CartScreen
import com.example.tableo.ui.guest.cart.CartViewModel
import com.example.tableo.ui.guest.menu.MenuScreen
import com.example.tableo.ui.guest.orderConfirmation.OrderConfirmationScreen
import com.example.tableo.ui.guest.orderDetails.OrderDetailsScreen
import com.example.tableo.ui.guest.restaurantList.RestaurantListScreen
import com.example.tableo.ui.legal.LegalScreen
import com.example.tableo.ui.onboarding.OnboardingScreen
import com.example.tableo.ui.settings.SettingsScreen
import com.example.tableo.ui.splash.SplashScreen
import com.example.tableo.ui.staff.analytics.StaffAnalyticsScreen
import com.example.tableo.ui.staff.archive.StaffArchiveScreen
import com.example.tableo.ui.staff.dashboard.StaffDashboardScreen
import com.example.tableo.ui.staff.menu.StaffMenuItemEditScreen
import com.example.tableo.ui.staff.menu.StaffMenuScreen
import com.example.tableo.ui.staff.team.StaffTeamScreen
import org.koin.compose.koinInject

/** Navigationsgraph der ganzen App. */
@Composable
fun AppStart(
    orderUpdatesViewModel: OrderUpdatesViewModel = koinInject(),
    staffShiftViewModel: StaffShiftViewModel = koinInject()
) {
    val navController = rememberNavController()

    // ist ein Kunde angemeldet, bekommt er Nachrichten zu seinen Bestellungen
    // Mitarbeiter im Dienst bekommen Nachrichten zu neuen Bestellungen
    LaunchedEffect(Unit) {
        orderUpdatesViewModel.start()
        staffShiftViewModel.start()
    }

    // Wechsel zwischen den drei Reitern unten (Start, Bestellungen, Konto):
    // springt immer zur Startseite zurück und legt den Reiter darauf, damit sich beim
    // Hin- und Herwechseln kein langer Rückstapel aufbaut
    val goToHomeTab: () -> Unit = {
        navController.popBackStack(Route.RestaurantList, inclusive = false)
    }
    val goToOrdersTab: () -> Unit = {
        navController.navigate(Route.MyOrders) {
            popUpTo(Route.RestaurantList)
            launchSingleTop = true
        }
    }
    val goToSettingsTab: () -> Unit = {
        navController.navigate(Route.Settings) {
            popUpTo(Route.RestaurantList)
            launchSingleTop = true
        }
    }

    NavHost(navController = navController, startDestination = Route.Splash) {

        composable<Route.Splash> {
            SplashScreen(
                onFinished = { showOnboarding ->
                    val destination = if (showOnboarding) Route.Onboarding else Route.RestaurantList
                    navController.navigate(destination) {
                        popUpTo(Route.Splash) { inclusive = true }
                    }
                }
            )
        }

        composable<Route.Onboarding> {
            OnboardingScreen(
                onFinished = {
                    navController.navigate(Route.RestaurantList) {
                        popUpTo(Route.Onboarding) { inclusive = true }
                    }
                }
            )
        }

        composable<Route.RestaurantList>(
            enterTransition = { if (initialState.destination.isTab()) EnterTransition.None else null },
            exitTransition = { if (targetState.destination.isTab()) ExitTransition.None else null },
            popEnterTransition = { if (initialState.destination.isTab()) EnterTransition.None else null },
            popExitTransition = { if (targetState.destination.isTab()) ExitTransition.None else null }
        ) {
            RestaurantListScreen(
                onRestaurantSelected = { restaurant -> navController.navigate(Route.GuestMenu(restaurant.id, restaurant.name)) },
                onOrdersTabClick = goToOrdersTab,
                onSettingsTabClick = goToSettingsTab
            )
        }

        composable<Route.GuestMenu> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.GuestMenu>()
            MenuScreen(
                onBackClick = { navController.popBackStack() },
                onContinueClick = { navController.navigate(Route.OrderDetails(route.restaurantId, route.restaurantName)) }
            )
        }

        composable<Route.OrderDetails> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.OrderDetails>()
            OrderDetailsScreen(
                restaurantName = route.restaurantName,
                onBackClick = { navController.popBackStack() },
                onConfirmed = { orderType, tableNumber, pickupTime ->
                    navController.navigate(
                        Route.Cart(
                            restaurantId = route.restaurantId,
                            restaurantName = route.restaurantName,
                            orderType = orderType.name,
                            tableNumber = tableNumber,
                            pickupTime = pickupTime
                        )
                    )
                }
            )
        }

        composable<Route.Cart> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.Cart>()
            CartScreen(
                restaurantId = route.restaurantId, restaurantName = route.restaurantName,
                orderType = OrderType.valueOf(route.orderType), tableNumber = route.tableNumber, pickupTime = route.pickupTime,
                onBackClick = { navController.popBackStack() },
                onOrderPlaced = {
                    navController.navigate(Route.OrderConfirmation(route.restaurantName)) { popUpTo(Route.RestaurantList) }
                }
            )
        }

        composable<Route.OrderConfirmation> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.OrderConfirmation>()
            OrderConfirmationScreen(
                restaurantName = route.restaurantName,
                onBackToStart = { navController.popBackStack(Route.RestaurantList, inclusive = false) },
                onTrackOrder = { orderId -> navController.navigate(Route.OrderTracking(orderId)) }
            )
        }

        composable<Route.OrderTracking> {
            OrderTrackingScreen(onBackClick = { navController.popBackStack() })
        }

        composable<Route.StaffDashboard> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.StaffDashboard>()
            StaffDashboardScreen(
                onBackClick = { navController.popBackStack() },
                onArchiveClick = { navController.navigate(Route.StaffArchive(route.restaurantId, route.restaurantName)) },
                onManageMenuClick = { navController.navigate(Route.StaffMenuManagement(route.restaurantId, route.restaurantName)) },
                onTeamClick = { navController.navigate(Route.StaffTeam(route.restaurantId, route.restaurantName)) },
                onAnalyticsClick = { navController.navigate(Route.StaffAnalytics(route.restaurantId, route.restaurantName)) }
            )
        }

        composable<Route.StaffArchive> {
            StaffArchiveScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Route.StaffMenuManagement> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.StaffMenuManagement>()
            StaffMenuScreen(
                onAddItemClick = { navController.navigate(Route.StaffMenuItemEdit(route.restaurantId)) },
                onEditItemClick = { menuItemId -> navController.navigate(Route.StaffMenuItemEdit(route.restaurantId, menuItemId)) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Route.StaffMenuItemEdit> {
            StaffMenuItemEditScreen(
                onSaved = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Route.StaffTeam> {
            StaffTeamScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Route.CustomerAuth> {
            CustomerAuthScreen(
                onAuthSuccess = { staffAccess ->
                    orderUpdatesViewModel.start()
                    staffShiftViewModel.start()
                    // Fahrer landen im Fahrer-Bereich, Mitarbeiter im Dashboard, Kunden bei ihren Bestellungen
                    when {
                        staffAccess == null ->
                            navController.navigate(Route.MyOrders) { popUpTo(Route.RestaurantList) }

                        staffAccess.isDriver ->
                            navController.navigate(staffAccess.toDriverRoute()) { popUpTo(Route.RestaurantList) }

                        else ->
                            navController.navigate(Route.StaffDashboard(staffAccess.restaurant.id, staffAccess.restaurant.name)) {
                                popUpTo(Route.RestaurantList)
                            }
                    }
                }
            )
        }

        composable<Route.MyOrders>(
            enterTransition = { if (initialState.destination.isTab()) EnterTransition.None else null },
            exitTransition = { if (targetState.destination.isTab()) ExitTransition.None else null },
            popEnterTransition = { if (initialState.destination.isTab()) EnterTransition.None else null },
            popExitTransition = { if (targetState.destination.isTab()) ExitTransition.None else null }
        ) {
            MyOrdersScreen(
                onReorder = { restaurantId, restaurantName ->
                    navController.navigate(Route.GuestMenu(restaurantId, restaurantName))
                },
                onTrackOrder = { orderId -> navController.navigate(Route.OrderTracking(orderId)) },
                onOrderClick = { orderId -> navController.navigate(Route.MyOrderDetail(orderId)) },
                onHomeTabClick = goToHomeTab,
                onSettingsTabClick = goToSettingsTab
            )
        }

        composable<Route.MyOrderDetail> {
            val cartViewModel: CartViewModel = koinInject()
            MyOrderDetailScreen(
                onBackClick = { navController.popBackStack() },
                onTrackOrder = { orderId -> navController.navigate(Route.OrderTracking(orderId)) },
                onReorder = { order ->
                    cartViewModel.reorder(order)
                    navController.navigate(Route.GuestMenu(order.restaurantId, order.restaurantName))
                }
            )
        }

        // Kontoinformationen: Name, E-Mail, Passwort ändern
        composable<Route.Account> {
            AccountScreen(onBackClick = { navController.popBackStack() })
        }

        composable<Route.Settings>(
            enterTransition = { if (initialState.destination.isTab()) EnterTransition.None else null },
            // Beim Öffnen des Restaurant-Dashboards weicht die Konto-Seite nach links aus
            exitTransition = {
                when {
                    targetState.destination.hasRoute<Route.StaffDashboard>() ->
                        slideOutHorizontally(targetOffsetX = { -it / 3 }) + fadeOut()
                    targetState.destination.isTab() -> ExitTransition.None
                    else -> null
                }
            },
            // Beim Zurückkommen schiebt sie sich von links wieder herein
            popEnterTransition = {
                when {
                    initialState.destination.hasRoute<Route.StaffDashboard>() ->
                        slideInHorizontally(initialOffsetX = { -it / 3 }) + fadeIn()
                    initialState.destination.isTab() -> EnterTransition.None
                    else -> null
                }
            },
            popExitTransition = { if (targetState.destination.isTab()) ExitTransition.None else null }
        ) {
            SettingsScreen(
                onHomeTabClick = goToHomeTab,
                onOrdersTabClick = goToOrdersTab,
                onAccountInfoClick = { navController.navigate(Route.Account) },
                onLoginClick = { navController.navigate(Route.CustomerAuth) },
                onStaffDashboardClick = { restaurant -> navController.navigate(Route.StaffDashboard(restaurant.id, restaurant.name)) },
                onDriverAreaClick = { access -> navController.navigate(access.toDriverRoute()) },
                onLoggedOut = {
                    orderUpdatesViewModel.stop()
                    staffShiftViewModel.stop()
                    goToHomeTab()
                },
                onLegalClick = { page -> navController.navigate(Route.Legal(page.name)) }
            )
        }

        composable<Route.Legal> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.Legal>()
            LegalScreen(
                page = LegalPage.valueOf(route.page),
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Route.DriverOrders> {
            DriverOrdersScreen(onBackClick = { navController.popBackStack() })
        }

        composable<Route.StaffAnalytics> {
            StaffAnalyticsScreen(onBackClick = { navController.popBackStack() })
        }
    }
}

/** Die drei Reiter unten. Zwischen ihnen gibt es keine Animation, damit die Leiste ruhig stehen bleibt. */
private fun NavDestination.isTab(): Boolean =
    hasRoute<Route.RestaurantList>() || hasRoute<Route.MyOrders>() || hasRoute<Route.Settings>()

/** Fahrer-Bereich des eigenen Restaurants, der Name kommt aus dem Team-Profil. */
private fun StaffAccess.toDriverRoute() = Route.DriverOrders(
    restaurantId = restaurant.id,
    restaurantName = restaurant.name,
    driverName = profile.name.ifBlank { "Fahrer" }
)
