package xyz.tomashrib.zephyruswallet.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bitcoindevkit.*
import xyz.tomashrib.zephyruswallet.tools.TAG
import org.bitcoindevkit.Wallet as BdkWallet

object Wallet {

    // declaring necessary vars
    private lateinit var wallet: BdkWallet
    private lateinit var path: String
    private const val electrumURL: String = "ssl://electrum.blockstream.info:60002"
    private lateinit var blockchainConfig: BlockchainConfig
    private lateinit var blockchain: Blockchain

    // object for status of network synchronization
    object LogProgress: Progress {
        override fun update(progress: Float, message: String?) {
            Log.i(TAG, "Sync wallet")
        }
    }

    // setting Wallet path
    // important for construction
    fun setPath(path: String) {
        Wallet.path = path
    }

    // initializes necessary prerequisites for wallet construction
    private fun initialize(
        externalDescriptor: String,
        internalDescriptor: String,
    ) {
        val database = DatabaseConfig.Sqlite(SqliteDbConfiguration("$path/bdk-sqlite"))
        wallet = BdkWallet(
            externalDescriptor,
            internalDescriptor,
            // Network.REGTEST,
            Network.TESTNET,
            database,
        )
    }

    // creates Blockchain instance
    // for network operations
    fun createBlockchain() {
        blockchainConfig = BlockchainConfig.Electrum(ElectrumConfig(electrumURL, null, 10u, 20u, 10u))
        // blockchainConfig = BlockchainConfig.Esplora(EsploraConfig(esploraUrl, null, 5u, 20u, 10u))
        blockchain = Blockchain(blockchainConfig)
    }

    // creates new wallet from parameters
    fun createWallet() {
        // seed phrase randomly generated from a wordlist of 2048 english words representing entropy
        val mnemonic: Mnemonic = Mnemonic(WordCount.WORDS12)
        // root key
        // all addresses and important things are computed from this
        val bip32RootKey: DescriptorSecretKey = DescriptorSecretKey(
            network = Network.TESTNET,
            mnemonic = mnemonic,
            // passphrase, like a password on top of seed phrase
            // empty string for simplicity's sake
            // in future could be optional when on wallet creation
            password = ""
        )
        // computes descriptors necessary for different operations from root key
        val externalDescriptor: String = createExternalDescriptor(bip32RootKey)
        val internalDescriptor: String = createInternalDescriptor(bip32RootKey)
        initialize(
            externalDescriptor = externalDescriptor,
            internalDescriptor = internalDescriptor,
        )

        // saves them to sharedPreferences
        Repository.saveWallet(path, externalDescriptor, internalDescriptor)
        Repository.saveMnemonic(mnemonic.asString())
    }

    // only create BIP84 compatible wallets
    // important for creation of receive address
    private fun createExternalDescriptor(rootKey: DescriptorSecretKey): String {
        val externalPath: DerivationPath = DerivationPath("m/84h/1h/0h/0")
        val externalDescriptor = "wpkh(${rootKey.extend(externalPath).asString()})"
        Log.i(TAG, "Descriptor for receive addresses is $externalDescriptor")
        return externalDescriptor
    }

    // important for creation of change address
    // when sending btc, you need to spend the whole utxo and the change goes to this address
    private fun createInternalDescriptor(rootKey: DescriptorSecretKey): String {
        val internalPath: DerivationPath = DerivationPath("m/84h/1h/0h/1")
        val internalDescriptor = "wpkh(${rootKey.extend(internalPath).asString()})"
        Log.i(TAG, "Descriptor for change addresses is $internalDescriptor")
        return internalDescriptor
    }

    // if the wallet already exists, its descriptors are stored in shared preferences
    // and it loads wallet from storage
    fun loadExistingWallet() {
        val initialWalletData: RequiredInitialWalletData = Repository.getInitialWalletData()
        Log.i(TAG, "Loading existing wallet, descriptor is ${initialWalletData.descriptor}")
        Log.i(TAG, "Loading existing wallet, change descriptor is ${initialWalletData.changeDescriptor}")
        initialize(
            externalDescriptor = initialWalletData.descriptor,
            internalDescriptor = initialWalletData.changeDescriptor,
        )
    }

    // recovers existing wallet from mnemonic (seed phrase)
    fun recoverWallet(mnemonic: String) {
        val bip32RootKey: DescriptorSecretKey = DescriptorSecretKey(
            network = Network.TESTNET,
            mnemonic = Mnemonic.fromString(mnemonic),
            password = ""
        )
        val externalDescriptor: String = createExternalDescriptor(bip32RootKey)
        val internalDescriptor: String = createInternalDescriptor(bip32RootKey)
        initialize(
            externalDescriptor = externalDescriptor,
            internalDescriptor = internalDescriptor,
        )
        Repository.saveWallet(path, externalDescriptor, internalDescriptor)
        Repository.saveMnemonic(mnemonic.toString())
    }

    // constructs transaction from different parameters
    fun createTransaction(recipient: String, amount: ULong, fee_rate: Float): TxBuilderResult {
        val scriptPubkey: Script = Address(recipient).scriptPubkey()
        return TxBuilder()
            .addRecipient(scriptPubkey, amount)
            .feeRate(satPerVbyte = fee_rate)
            .finish(wallet)
    }

    // signs a transaction with private key
    fun sign(psbt: PartiallySignedTransaction) {
        wallet.sign(psbt)
    }

    // broadcasts signed bitcoin transactions to network
    fun broadcast(signedPsbt: PartiallySignedTransaction): String {
        blockchain.broadcast(signedPsbt)
        return signedPsbt.txid()
    }

    // retrieves transaction history for current wallet
    fun getTransactions(): List<TransactionDetails> = wallet.listTransactions()

    // syncs wallet from network, retrieves all data important for addresses
    suspend fun sync(){
        Log.i(TAG, "Wallet is syncing")
        wallet.sync(blockchain, LogProgress)
        Log.i(TAG, "Wallet has synced")
    }

    // retrieves spendable balance of a wallet
    // spendable is confirmed and/or change from sent transaction by user
    fun getBalance(): ULong = wallet.getBalance().spendable

    // retrieves unconfirmed balance of a wallet
    // unconfirmed balance is balance to be received from other people
    fun getBalanceUnconfirmed(): ULong = wallet.getBalance().untrustedPending

    // generates new bitcoin receive address
    fun getNewAddress(): AddressInfo {
        val newAddress = wallet.getAddress(AddressIndex.NEW)
        Log.i("Wallet", "New address is $newAddress.address")
        return newAddress
    }

    // retrieves last unused address which doesnt have any transactions on it
    // important, because for increased privacy, its recommended to use new address for every transaction
    fun getLastUnusedAddress(): AddressInfo = wallet.getAddress(AddressIndex.LAST_UNUSED)

    // checks if blockchain instance is created or not
    fun isBlockChainCreated() = ::blockchain.isInitialized

    // creates a transaction that drains the whole balance of a wallet
    fun createSendAllTransaction(recipient: String, feeRate: Float): TxBuilderResult {
        return TxBuilder()
            .drainWallet()
            .drainTo(address = recipient)
            .feeRate(satPerVbyte = feeRate)
            .finish(wallet)
    }
}