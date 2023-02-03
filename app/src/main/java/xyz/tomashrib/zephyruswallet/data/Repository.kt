package xyz.tomashrib.zephyruswallet.data

import android.util.Log
import xyz.tomashrib.zephyruswallet.tools.SharedPreferencesManager
import xyz.tomashrib.zephyruswallet.tools.TAG

object Repository {

    // shared preferences are a way to save/retrieve small pieces of data without building a database
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    //initialize sharedPreferences
    fun setSharedPreferences(sharedPrefManager: SharedPreferencesManager) {
        sharedPreferencesManager = sharedPrefManager
    }

    // see if user has already created wallet
    fun doesWalletExist(): Boolean {
        val walletInitialized: Boolean = sharedPreferencesManager.walletInitialised
        Log.i(TAG, "Value of walletInitialized at launch: $walletInitialized")
        return walletInitialized
    }

    // save the necessary data for wallet reconstruction in shared preferences
    // upon application launch, the wallet can initialize itself using that data
    fun saveWallet(path: String, descriptor: String, changeDescriptor: String) {
        Log.i(
            TAG,
            "Saved wallet:\npath -> $path \ndescriptor -> $descriptor \nchange descriptor -> $changeDescriptor"
        )
        sharedPreferencesManager.walletInitialised = true
        sharedPreferencesManager.path = path
        sharedPreferencesManager.descriptor = descriptor
        sharedPreferencesManager.changeDescriptor = changeDescriptor
    }

    // save mnemonic to sharedPreferences
    fun saveMnemonic(mnemonic: String) {
        sharedPreferencesManager.mnemonic = mnemonic
    }

    // retrieve mnemonic from sharedPreferences
    fun getMnemonic(): String {
        return sharedPreferencesManager.mnemonic
    }

    // retrieve wallet data necessary for wallet construction
    fun getInitialWalletData(): RequiredInitialWalletData {
        val descriptor: String = sharedPreferencesManager.descriptor
        val changeDescriptor: String = sharedPreferencesManager.changeDescriptor
        return RequiredInitialWalletData(descriptor, changeDescriptor)
    }
}

// custom data class for descriptor and change descriptor
// gives these two a structure
data class RequiredInitialWalletData(
    val descriptor: String,
    val changeDescriptor: String
)