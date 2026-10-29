package com.example.myandroidproject

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myandroidproject.screens.AddScheduleScreen
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
                        popUpTo<RegisterRoute> { inclusive = true } // Regisztrációs képernyő törlése a backstackből
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable<HomeRoute> {
            HomeScreen(
                onAddScheduleClick = {
                    navController.navigate(AddScheduleRoute)
                },
                onLogout = {
                    session.clear()
                    navController.navigate(LoginRoute) {
                        popUpTo<HomeRoute> { inclusive = true }
                    }
                }
            )
        }

        composable<AddScheduleRoute> {
            AddScheduleScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}