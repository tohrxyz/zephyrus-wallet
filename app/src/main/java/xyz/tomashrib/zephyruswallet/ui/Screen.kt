package xyz.tomashrib.zephyruswallet.ui


sealed class Screen(val route: String) {
    // wallet creation screens
    object WalletChoiceScreen : Screen("wallet_choice_screen")
    object WalletRecoveryScreen : Screen("wallet_recovery_screen")

    // home screens
    object WalletScreen : Screen("wallet_screen")
    object AboutScreen : Screen("about_screen")
    object RecoveryPhraseScreen : Screen("recovery_phrase_screen")

    // wallet screens
    object HomeScreen : Screen("home_screen")
    object SendScreen : Screen("send_screen")
    object ReceiveScreen : Screen("receive_screen")
    object TransactionsScreen : Screen("transactions_screen")

    // qr code scanning screen
    object QRScanScreen: Screen("qr_scan_screen")
}
