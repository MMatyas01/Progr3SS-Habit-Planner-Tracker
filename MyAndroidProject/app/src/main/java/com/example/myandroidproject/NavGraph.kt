package com.example.myandroidproject

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myandroidproject.screens.AddHabitScreen
import com.example.myandroidproject.screens.CreateScheduleScreen
import com.example.myandroidproject.screens.HomeScreen
import com.example.myandroidproject.screens.LoginScreen
import com.example.myandroidproject.screens.RegisterScreen
import com.example.myandroidproject.utils.SessionManager

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val session = remember { SessionManager.getInstance(context) }
    val startDestination: Any = if (session.isLoggedIn) HomeRoute else LoginRoute

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<LoginRoute> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(HomeRoute) {
                        popUpTo<LoginRoute> { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(RegisterRoute)
                }
            )
        }

        composable<RegisterRoute> {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(LoginRoute) {
                        popUpTo<RegisterRoute> { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable<HomeRoute> {
            HomeScreen(
                onAddHabit = {
                    navController.navigate(AddHabitRoute)
                },
                onCreateSchedule = {
                    navController.navigate(CreateScheduleRoute)
                },
                onLogout = {
                    session.clear()
                    navController.navigate(LoginRoute) {
                        popUpTo<HomeRoute> { inclusive = true }
                    }
                }
            )
        }

        // Ezeket a képernyőket a következő lépésben fogjuk megírni!
        composable<AddHabitRoute> {
            AddHabitScreen(
                onBack = { navController.popBackStack() },
                onSaved = {
                    navController.navigate(HomeRoute) {
                        popUpTo<HomeRoute> { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToCreateSchedule = { navController.navigate(CreateScheduleRoute) }
            )
        }

        composable<CreateScheduleRoute> {
            CreateScheduleScreen(
                onBack = { navController.popBackStack() },
                onSaved = {
                    navController.navigate(HomeRoute) {
                        popUpTo<HomeRoute> { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToAddHabit = { navController.navigate(AddHabitRoute) }
            )
        }
    }
}