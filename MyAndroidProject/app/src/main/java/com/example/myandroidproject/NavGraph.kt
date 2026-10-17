package com.example.myandroidproject

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myandroidproject.screens.HomeScreen
import com.example.myandroidproject.screens.LoginScreen
import com.example.myandroidproject.screens.AddScheduleScreen

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = LoginRoute
    ) {
        composable<LoginRoute> {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(HomeRoute) {
                        popUpTo<LoginRoute> {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable<HomeRoute> {
            HomeScreen(
                onAddScheduleClick = {
                    navController.navigate(AddScheduleRoute)
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
