package xyz.tomashrib.zephyruswallet.ui.wallet

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import xyz.tomashrib.zephyruswallet.ui.Screen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WalletNavigation() {
    val navController: NavHostController = rememberAnimatedNavController()

    AnimatedNavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route,
    ) {

        composable(
            route = Screen.HomeScreen.route,
        ) { HomeScreen(navController, LocalContext.current) }

        composable(
            route = Screen.ReceiveScreen.route,
        ) { ReceiveScreen(navController, LocalContext.current) }

        composable(
            route = Screen.SendScreen.route,
        ) { SendScreen(navController, LocalContext.current) }
//
//        composable(
//            route = Screen.TransactionsScreen.route,
//        ) { TransactionsScreen(navController) }
    }
}