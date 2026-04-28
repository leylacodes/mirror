package com.mirror.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mirror.app.AppContainer
import com.mirror.app.ui.screen.checkin.CheckInScreen
import com.mirror.app.ui.screen.checkin.CheckInViewModelFactory
import com.mirror.app.ui.screen.history.HistoryScreen
import com.mirror.app.ui.screen.history.HistoryViewModelFactory
import com.mirror.app.ui.screen.home.HomeScreen
import com.mirror.app.ui.screen.home.HomeViewModelFactory
import com.mirror.app.ui.screen.onboarding.OnboardingScreen
import com.mirror.app.ui.screen.onboarding.OnboardingViewModelFactory
import com.mirror.app.ui.screen.settings.SettingsScreen
import com.mirror.app.ui.screen.settings.SettingsViewModelFactory
import kotlinx.coroutines.flow.first

@Composable
fun NavGraph(container: AppContainer) {
    val navController = rememberNavController()
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val prefs = container.userPrefsDataStore.userPreferences.first()
        startDestination = if (prefs.onboardingComplete) Screen.Home.route else Screen.Onboarding.route
    }

    val start = startDestination ?: return

    NavHost(navController = navController, startDestination = start) {
        composable(Screen.Onboarding.route) {
            val vm = viewModel(factory = OnboardingViewModelFactory(container))
            OnboardingScreen(vm, onFinished = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Home.route) {
            val vm = viewModel(factory = HomeViewModelFactory(container))
            HomeScreen(
                vm = vm,
                onCheckIn = { date -> navController.navigate(Screen.CheckIn.createRoute(date)) },
                onHistory = { navController.navigate(Screen.History.route) },
                onSettings = { navController.navigate(Screen.Settings.route) }
            )
        }
        composable(
            route = Screen.CheckIn.route,
            arguments = listOf(navArgument("date") { type = NavType.StringType })
        ) { backStack ->
            val date = backStack.arguments?.getString("date") ?: ""
            val vm = viewModel(factory = CheckInViewModelFactory(container, date))
            CheckInScreen(vm, onSaved = { navController.popBackStack() })
        }
        composable(Screen.History.route) {
            val vm = viewModel(factory = HistoryViewModelFactory(container))
            HistoryScreen(
                vm = vm,
                onEdit = { date -> navController.navigate(Screen.CheckIn.createRoute(date)) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Settings.route) {
            val vm = viewModel(factory = SettingsViewModelFactory(container))
            SettingsScreen(vm, onBack = { navController.popBackStack() })
        }
    }
}
