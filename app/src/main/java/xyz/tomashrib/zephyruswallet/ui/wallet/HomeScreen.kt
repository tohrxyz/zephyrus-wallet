package xyz.tomashrib.zephyruswallet.ui.wallet

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.*
import org.bitcoindevkit.TransactionDetails
import xyz.tomashrib.zephyruswallet.R
import xyz.tomashrib.zephyruswallet.data.Wallet
import xyz.tomashrib.zephyruswallet.tools.TAG
import xyz.tomashrib.zephyruswallet.ui.Screen
import xyz.tomashrib.zephyruswallet.ui.theme.ZephyrusColors
import xyz.tomashrib.zephyruswallet.ui.theme.sourceSans
import xyz.tomashrib.zephyruswallet.ui.theme.sourceSansSemiBold
import xyz.tomashrib.zephyruswallet.tools.formatSats
import xyz.tomashrib.zephyruswallet.tools.timestampToString

internal class WalletViewModel() : ViewModel() {

    private var _balance: MutableLiveData<ULong> = MutableLiveData(0u)
    val balance: LiveData<ULong>
        get() = _balance

    private var _balanceUnconfirmed: MutableLiveData<ULong> = MutableLiveData(0u)
    val balanceUnconfirmed: LiveData<ULong>
        get() = _balanceUnconfirmed

    fun updateBalance() {
        //does async call to Wallet.sync(), to not block Main UI Thread
        viewModelScope.launch(Dispatchers.IO){
            //syncs Wallet from the electrum server
            Wallet.sync()
            //when sync is done, UI is updated in this
            withContext(Dispatchers.Main){
                //spendable balance
                _balance.value = Wallet.getBalance()
                //unconfirmed balance - receiving from someone
                _balanceUnconfirmed.value = Wallet.getBalanceUnconfirmed()
            }
        }
        //updates balance
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
internal fun HomeScreen(
    navController: NavController,
    context: Context,
    walletViewModel: WalletViewModel = viewModel()
) {
    //complete list of all transaction associated with current wallet
    val allTransactions: List<TransactionDetails> = Wallet.getTransactions()

    //checks whether the network is online
    val networkAvailable: Boolean = isOnline(LocalContext.current)
    val balance by walletViewModel.balance.observeAsState()
    val balanceUnconfirmed by walletViewModel.balanceUnconfirmed.observeAsState()

    //when network is online and blockchain isnt created yet, the new blockchain is created
    if (networkAvailable && !Wallet.isBlockChainCreated()) {
        Log.i(TAG, "Creating new blockchain")
        Wallet.createBlockchain()
    }

//    walletViewModel.updateBalance()
//    Toast.makeText(context, "Wallet is syncing...", Toast.LENGTH_SHORT).show()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ZephyrusColors.bgColorBlack),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        Spacer(Modifier.padding(30.dp))

        //bar for bitcoin balance
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
                .height(40.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {

            //displays balance number
            Text(
                text = formatSats(balance.toString()),
                fontFamily = sourceSansSemiBold,
                fontSize = 40.sp,
                color = ZephyrusColors.lightPurplePrimary,
            )

            Spacer(Modifier.padding(5.dp))

            //displays the bitcoin unit -> Sats
            Text(
                text = "Sats",
                fontFamily = sourceSansSemiBold,
                fontSize = 40.sp,
                color = ZephyrusColors.lightPurplePrimary,
            )
        }

        //unconfirmed balance
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
                .height(30.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {

            //displays balance number
            Text(
                text = formatSats(balanceUnconfirmed.toString()),
                fontFamily = sourceSansSemiBold,
                fontSize = 20.sp,
                color = ZephyrusColors.lightGrey,
            )

            Spacer(Modifier.padding(5.dp))

            //displays the bitcoin unit -> Sats
            Text(
                text = "Sats (unconfirmed)",
                fontFamily = sourceSansSemiBold,
                fontSize = 20.sp,
                color = ZephyrusColors.lightGrey,
            )
        }

        //when network is offline, the "Network unavailable" is displayed
        if (!networkAvailable) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(color = ZephyrusColors.lightBlue)
                    .height(50.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Network unavailable",
                    fontFamily = sourceSans,
                    fontSize = 18.sp,
                    color = ZephyrusColors.fontColorWhite
                )
            }
        }

        Spacer(Modifier.padding(30.dp))

        //transaction history
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        ){

            //unconfirmed transactions box
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(ZephyrusColors.fontColorWhite)
                    .padding(horizontal = 15.dp, vertical = 8.dp),
            ) {
                Text(
                    text = stringResource(R.string.pending),
                    fontFamily = sourceSans,
                    fontSize = 20.sp,
                    color = ZephyrusColors.bgColorBlack,
                )
            }
            //box where actual unconfirmed transactions are displayed
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(150.dp)
                    .border(2.dp, ZephyrusColors.fontColorWhite)
                    .padding(horizontal = 15.dp, vertical = 8.dp)
            ){
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(state = scrollState)
                ){

                    //make this selectable
                    SelectionContainer() {
                        Text(
                            text = getTransactionList(allTransactions, false),
                            fontFamily = sourceSans,
                            fontSize = 15.sp,
                            color = ZephyrusColors.fontColorWhite,
                        )
                    }
                }
            }

            //confirmed transactions box
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(ZephyrusColors.fontColorWhite)
                    .padding(horizontal = 15.dp, vertical = 8.dp),
            ) {
                Text(
                    text = stringResource(R.string.confirmed),
                    fontFamily = sourceSans,
                    fontSize = 20.sp,
                    color = ZephyrusColors.bgColorBlack,
                )
            }
            //box where actual confirmed transactions are displayed
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(150.dp)
                    .border(2.dp, ZephyrusColors.fontColorWhite)
                    .padding(horizontal = 15.dp, vertical = 8.dp)
            ){
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(state = scrollState)
                ){

                    //make this selectable
                    SelectionContainer() {
                        Text(
                            text = getTransactionList(allTransactions, true),
                            fontFamily = sourceSans,
                            fontSize = 15.sp,
                            color = ZephyrusColors.fontColorWhite,
                        )
                    }
                }
            }
        }

        Spacer(Modifier.padding(50.dp))

        //bottom bar for buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .height(120.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ){

            //receive button
            Button(
                onClick = {
                            navController.navigate(Screen.ReceiveScreen.route)
                            Toast.makeText(context, "Generating new address for you...", Toast.LENGTH_SHORT).show()
                          },
                colors = ButtonDefaults.buttonColors(ZephyrusColors.lightPurplePrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .height(80.dp)
                    .weight(1f)
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(10.dp))
            ){
                Text(
                    text = stringResource(R.string.receive),
                    fontSize = 20.sp,
                    fontFamily = sourceSans,
                    lineHeight = 30.sp,
                    color = ZephyrusColors.darkerPurpleOnPrimary,
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                )
            }

            Spacer(Modifier.padding(horizontal = 5.dp))

            //sync image button
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.ic_round_sync_black),
                contentDescription = "sync",
                modifier = Modifier
                    .background(
                        ZephyrusColors.fontColorWhite,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .weight(0.5f)
                    .clickable {
                        //updates balance with fun from viewModel
                        walletViewModel.updateBalance()
                        //shows a Toast message
                        Toast
                            .makeText(context, "Wallet is syncing...", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .clip(RoundedCornerShape(10.dp))
                    .padding(horizontal = 5.dp)
            )
            Spacer(Modifier.padding(horizontal = 5.dp))

            //send button
            Button(
                onClick = { navController.navigate(Screen.SendScreen.route) },
                colors = ButtonDefaults.buttonColors(ZephyrusColors.lightPurplePrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .height(80.dp)
                    .weight(1f)
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(10.dp))
            ){
                Text(
                    text = stringResource(R.string.send),
                    fontSize = 20.sp,
                    fontFamily = sourceSans,
                    lineHeight = 30.sp,
                    color = ZephyrusColors.darkerPurpleOnPrimary,
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .padding(start = 10.dp)

                )
            }

        }
    }
}

//function that checks if the internet connectivity is available
fun isOnline(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities =
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    if (capabilities != null) {
        when {
            //mobile data
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            }
            //wifi
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            }
            //ethernet
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
    }
    return false
}

//function that returns a string containing the transaction history depending on whether the parameter
//isConfirmed = true -> for confirmed (mined) transactions
//isConfirmed = false -> for unconfirmed (not mined) transactions
//this will be displayed on the HomeScreen to be shown as transaction history for the current wallet
private fun getTransactionList(transactions: List<TransactionDetails>, isConfirmed: Boolean): String {

    if(isConfirmed){

        //filter those transactions out that have confirmation time
        val confirmedTransactions = transactions.filter {

            //when transaction has valid confirmation time, it was confirmed already
            it.confirmationTime != null
        }

        //check if the transaction list is empty
        if (confirmedTransactions.isEmpty()) {
            Log.i(TAG, "Confirmed transaction list is empty")
            return "No confirmed transactions"
        } else { //when transaction list contains some transactions

            //sort transactions from most recent, by blockheight (when it was confirmed/mined)
            //higher blockheight == more recent
            val sortedTransactions = confirmedTransactions.sortedByDescending { it.confirmationTime!!.height }

            //builds string containing all transactions
            return buildString {

                //for every transaction that exists
                for (item in sortedTransactions) {
                    Log.i(TAG, "Transaction list item: $item")
                    appendLine("Timestamp: ${item.confirmationTime!!.timestamp.timestampToString()}")
                    appendLine("Received: ${item.received}")
                    appendLine("Sent: ${item.sent}")
                    appendLine("Fees: ${item.fee}")
                    appendLine("Block: ${item.confirmationTime!!.height}")
                    appendLine("Txid: ${item.txid}")
                    appendLine()
                }
            }
        }
    } else{ //when isConfirmed = false (unconfirmed transactions)

        //filter out transactions from the list by confirmation time
        val unconfirmedTransactions = transactions.filter {

            //when transactions doesnt have confirmation time, it was not confirmed yet
            it.confirmationTime == null
        }

        //checks if the list of transactions is empty
        if (unconfirmedTransactions.isEmpty()) {
            Log.i(TAG, "Pending transaction list is empty")
            return "No pending transactions"
        } else { //when transaction list exists

            //builds string containing all transactions
            return buildString {

                //for every transaction
                for (item in unconfirmedTransactions) {
                    Log.i(TAG, "Pending transaction list item: $item")
                    appendLine("Timestamp: Pending")
                    appendLine("Received: ${item.received}")
                    appendLine("Sent: ${item.sent}")
                    appendLine("Fees: ${item.fee}")
                    appendLine("Txid: ${item.txid}")
                    appendLine()
                }
            }
        }
    }
}