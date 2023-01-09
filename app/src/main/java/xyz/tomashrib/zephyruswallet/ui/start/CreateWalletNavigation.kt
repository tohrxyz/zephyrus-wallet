package xyz.tomashrib.zephyruswallet.ui.start

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import xyz.tomashrib.zephyruswallet.WalletCreateType
import xyz.tomashrib.zephyruswallet.ui.Screen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CreateWalletNavigation(onBuildWalletButtonClicked: (WalletCreateType) -> Unit) {
    val navController: NavHostController = rememberAnimatedNavController()

    //this routes across different screen depending on the context
    AnimatedNavHost(
        navController = navController,
        startDestination = Screen.WalletChoiceScreen.route,
    ) {

        //this can go to WalletChoiceScreen
        composable(
            route = Screen.WalletChoiceScreen.route,
        ) { WalletChoiceScreen(navController = navController, onBuildWalletButtonClicked) }

        //this can go to WalletRecoveryScreen
        composable(
            route = Screen.WalletRecoveryScreen.route,
        ) { WalletRecoveryScreen(onBuildWalletButtonClicked) }
    }
}