package xyz.tomashrib.zephyruswallet

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import xyz.tomashrib.zephyruswallet.data.Repository
import xyz.tomashrib.zephyruswallet.data.Wallet
import xyz.tomashrib.zephyruswallet.tools.TAG
import xyz.tomashrib.zephyruswallet.ui.HomeNavigation
import xyz.tomashrib.zephyruswallet.ui.start.CreateWalletNavigation

class ZephyrusWalletActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val onBuildWalletButtonClicked: (WalletCreateType) -> Unit = { walletCreateType ->
            try {
                // load up a wallet either from scratch or using a BIP39 recovery phrase
                when (walletCreateType) {
                    // if we create a wallet from scratch we don't need a recovery phrase
                    is WalletCreateType.FROMSCRATCH -> Wallet.createWallet()

                    is WalletCreateType.RECOVER -> Wallet.recoverWallet(walletCreateType.recoveryPhrase)
                }
                setContent {
                    HomeNavigation()
                }
            } catch(e: Throwable) {
                Log.i(TAG, "Could not build wallet: $e")
            }
        }

        if (Repository.doesWalletExist()) {
            Wallet.loadExistingWallet()
            setContent {
                HomeNavigation()
            }
        } else {
            setContent {
                CreateWalletNavigation(onBuildWalletButtonClicked)
            }
        }

    }
}

sealed class WalletCreateType() {
    class FROMSCRATCH : WalletCreateType()
    class RECOVER(val recoveryPhrase: String) : WalletCreateType()
}