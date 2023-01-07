package xyz.tomashrib.zephyruswallet.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeNavigation() {
    val navController: NavHostController = rememberAnimatedNavController()

    AnimatedNavHost(
        navController = navController,
        startDestination = Screen.WalletScreen.route,
    ) {

        composable(
            route = Screen.WalletScreen.route,
        ) { WalletScreen(navController = navController) }

//        composable(
//            route = Screen.AboutScreen.route,
//        ) { AboutScreen(navController = navController) }
//
        composable(
            route = Screen.RecoveryPhraseScreen.route,
        ) { RecoveryPhraseScreen(navController = navController) }
    }
}