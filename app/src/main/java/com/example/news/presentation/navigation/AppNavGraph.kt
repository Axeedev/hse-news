package com.example.news.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.news.presentation.screens.SubscriptionsScreen


@Composable
fun AppNavGraph(){

    val controller = rememberNavController()

    NavHost(
        navController = controller,
        startDestination = Screen.News.route
    ){
        composable(Screen.News.route){
            SubscriptionsScreen()
        }
    }
}


sealed class Screen(val route: String){


    data object News: Screen("news")


}