package xyz.tomashrib.zephyruswallet

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import xyz.tomashrib.zephyruswallet.ui.start.CreateWalletNavigation

class ZephyrusWalletActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val onBuildWalletButtonClicked: () -> Unit = {
            setContent {
//                HomeNavigation()
            }
        }

        setContent {
            CreateWalletNavigation(onBuildWalletButtonClicked)
        }

    }
}