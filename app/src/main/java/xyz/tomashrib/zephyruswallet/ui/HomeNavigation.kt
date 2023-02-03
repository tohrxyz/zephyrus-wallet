package xyz.tomashrib.zephyruswallet.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

// this is HomeNavigation graph
// responsible for routing across Wallet Creation screens
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeNavigation() {
    val navController: NavHostController = rememberAnimatedNavController()

    // routes across screens
    AnimatedNavHost(
        navController = navController,
        startDestination = Screen.WalletScreen.route,
    ) {

        // gotes to WalletScreen
        composable(
            route = Screen.WalletScreen.route,
        ) { WalletScreen(navController = navController) }

//        composable(
//            route = Screen.AboutScreen.route,
//        ) { AboutScreen(navController = navController) }
//

        // goes to RecoveryPhraseScreen
        composable(
            route = Screen.RecoveryPhraseScreen.route,
        ) { RecoveryPhraseScreen(navController = navController) }
    }
}