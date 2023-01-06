package xyz.tomashrib.zephyruswallet.ui.start

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import xyz.tomashrib.zephyruswallet.ui.Screen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CreateWalletNavigation(onBuildWalletButtonClicked: () -> Unit) {
    val navController: NavHostController = rememberAnimatedNavController()

    AnimatedNavHost(
        navController = navController,
        startDestination = Screen.WalletChoiceScreen.route,
    ) {

        composable(
            route = Screen.WalletChoiceScreen.route,
        ) { WalletChoiceScreen(navController = navController, onBuildWalletButtonClicked) }

        composable(
            route = Screen.WalletRecoveryScreen.route,
        ) { WalletRecoveryScreen() }
    }
}