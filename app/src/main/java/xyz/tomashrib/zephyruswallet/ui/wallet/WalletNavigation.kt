package xyz.tomashrib.zephyruswallet.ui.wallet

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import xyz.tomashrib.zephyruswallet.ui.Screen

// this is WalletNavigation graph
// responsible for routing between wallet screens
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WalletNavigation() {
    val navController: NavHostController = rememberAnimatedNavController()

    // routes across screens
    AnimatedNavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route,
    ) {

        // goes to HomeScreen
        composable(
            route = Screen.HomeScreen.route,
        ) { HomeScreen(navController, LocalContext.current) }

        // goes to ReceiveScreen
        composable(
            route = Screen.ReceiveScreen.route,
        ) { ReceiveScreen(navController, LocalContext.current) }

        // goes to SendScreen
        composable(
            route = Screen.SendScreen.route,
        ) { SendScreen(navController, LocalContext.current) }

        // goes to QRScanScreen
        composable(
            route = Screen.QRScanScreen.route,
        ) { QRScanScreen(navController = navController)}
    }
}