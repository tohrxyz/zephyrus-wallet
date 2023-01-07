package xyz.tomashrib.zephyruswallet

import android.app.Application
import android.content.Context
import xyz.tomashrib.zephyruswallet.data.Repository
import xyz.tomashrib.zephyruswallet.data.Wallet
import xyz.tomashrib.zephyruswallet.tools.SharedPreferencesManager

class ZephyrusWalletApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        // initialize Wallet object (singleton) with path variable
        Wallet.setPath(applicationContext.filesDir.toString())

        // initialize shared preferences manager object (singleton)
        val sharedPreferencesManager = SharedPreferencesManager(
            sharedPreferences = applicationContext.getSharedPreferences("current_wallet", Context.MODE_PRIVATE)
        )

        // initialize Repository object with shared preferences
        Repository.setSharedPreferences(sharedPreferencesManager)
    }
}