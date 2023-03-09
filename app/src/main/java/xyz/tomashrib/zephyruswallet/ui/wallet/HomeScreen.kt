package xyz.tomashrib.zephyruswallet.ui.wallet

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.widget.Space
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.*
import org.bitcoindevkit.TransactionDetails
import org.json.JSONObject
import xyz.tomashrib.zephyruswallet.R
import xyz.tomashrib.zephyruswallet.data.Wallet
import xyz.tomashrib.zephyruswallet.tools.TAG
import xyz.tomashrib.zephyruswallet.ui.Screen
import xyz.tomashrib.zephyruswallet.ui.theme.ZephyrusColors
import xyz.tomashrib.zephyruswallet.ui.theme.sourceSans
import xyz.tomashrib.zephyruswallet.ui.theme.sourceSansSemiBold
import xyz.tomashrib.zephyruswallet.tools.formatSats
import xyz.tomashrib.zephyruswallet.tools.timestampToString

// viewmodel handles the data across screen refreshes
internal class WalletViewModel() : ViewModel() {

    // handles rewrite of spendable balance
    private var _balance: MutableLiveData<ULong> = MutableLiveData(0u)
    val balance: LiveData<ULong>
        get() = _balance

    // handles rewrite of incoming unconfirmed balance
    private var _balanceUnconfirmed: MutableLiveData<ULong> = MutableLiveData(0u)
    val balanceUnconfirmed: LiveData<ULong>
        get() = _balanceUnconfirmed

    // handles rewrite of transaction history
    private var _transactionList: MutableLiveData<List<TransactionDetails>> = MutableLiveData()
    val transactionList: LiveData<List<TransactionDetails>>
        get() = _transactionList

    private var _bitcoinPrice: MutableLiveData<String> = MutableLiveData()
    val bitcoinPrice: LiveData<String>
        get() = _bitcoinPrice

    // updates balance + transaction history
    fun updateBalance() {
        //does async call to Wallet.sync(), not to block Main UI Thread
        viewModelScope.launch(Dispatchers.IO){
            //syncs Wallet from the electrum server
            Wallet.sync()
            //when sync is done, UI is updated in this
            withContext(Dispatchers.Main){
                //spendable balance
                _balance.value = Wallet.getBalance()
                //unconfirmed balance - receiving from someone
                _balanceUnconfirmed.value = Wallet.getBalanceUnconfirmed()
                // transaction history
                _transactionList.value = Wallet.getTransactions()

            }
        }
    }

    fun updatePrice(context: Context){
        viewModelScope.launch(Dispatchers.IO){
            val price = getBitcoinPrice(context, balance.toString())

            withContext(Dispatchers.Main){
                _bitcoinPrice.value = price
            }
        }
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
//    val allTransactions: List<TransactionDetails> = Wallet.getTransactions()
    // updates transaction list every time viewmodel updates it
    val allTransactions by walletViewModel.transactionList.observeAsState()

    //checks whether the network is online
    val networkAvailable: Boolean = isOnline(LocalContext.current)
    // updates spendable balance every time viewmodel updates it
    val balance by walletViewModel.balance.observeAsState()
    // updates incoming unconfirmed balance every time viewmodel updates it
    val balanceUnconfirmed by walletViewModel.balanceUnconfirmed.observeAsState()

    val bitcoinPrice by walletViewModel.bitcoinPrice.observeAsState()

    //when network is online and blockchain isnt created yet, the new blockchain is created
    if (networkAvailable && !Wallet.isBlockChainCreated()) {
        Log.i(TAG, "Creating new blockchain")
        Wallet.createBlockchain()
    }

//    walletViewModel.updateBalance()
//    Toast.makeText(context, "Wallet is syncing...", Toast.LENGTH_SHORT).show()

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(ZephyrusColors.bgColorBlack)
    ) {
        val (balanceBar, txHistoryBox, buttonsBar) = createRefs()

        //bar for bitcoin balance
        Column(
            modifier = Modifier
                .constrainAs(balanceBar) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .padding(top = 60.dp, bottom = 90.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp)
                    .height(40.dp)
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

            // if there is any incoming balance it displays it
            if(balanceUnconfirmed!!.toInt() != 0){

                //unconfirmed balance
                Row(
                    Modifier
                        .fillMaxWidth()
//                        .padding(horizontal = 15.dp)
                        .height(30.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {

                    //displays balance number
                    Text(
                        text = "+${formatSats(balanceUnconfirmed.toString())}",
                        fontFamily = sourceSansSemiBold,
                        fontSize = 20.sp,
                        color = ZephyrusColors.lightGrey,
                    )

//                    Spacer(Modifier.padding(5.dp))

                    //displays the bitcoin unit -> Sats
                    Text(
                        text = "  Sats on the way!",
                        fontFamily = sourceSansSemiBold,
                        fontSize = 20.sp,
                        color = ZephyrusColors.lightGrey,
                    )
                }
            }

            // bitcoin price goes here
            BitcoinPrice(bitcoinPrice.toString())
//            BitcoinPrice(btcBalance = "25000", context = context)
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
        }

        // tx history container
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .constrainAs(txHistoryBox) {
                    top.linkTo(balanceBar.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(buttonsBar.top)
                }
        ){

            //this displays all transactions from history
            allTransactions?.let { TransactionHistoryList(transactions = it) }
        }

        //bottom bar for buttons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp, top = 60.dp, start = 10.dp, end = 10.dp)
                .height(120.dp)
                .constrainAs(buttonsBar) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
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
                        //only sync when network is online
                        if (isOnline(context)) {
                            //updates balance with fun from viewModel
                            walletViewModel.updateBalance()
                            walletViewModel.updatePrice(context)

                            //shows a Toast message
                            Toast
                                .makeText(context, "Wallet is syncing...", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast
                                .makeText(context, "Network unavailable!", Toast.LENGTH_SHORT)
                                .show()
                        }
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
    } //constraint end


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

@Composable
fun TransactionHistoryList(transactions: List<TransactionDetails>){

    // sorts transactions by confirmation time, if unconfirmed it goes to the top
    val sortedTxList = transactions.sortedWith(compareByDescending(nullsLast()) { it.confirmationTime?.height })
    // for scrollable tx history
    val scrollState = rememberScrollState()

    // container for all txs
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .padding(vertical = 10.dp, horizontal = 10.dp)
            .verticalScroll(scrollState)
    ) {

        // for every transaction in list
        for(item in sortedTxList){

            //if unconfirmed
            if (item.confirmationTime == null){
                TransactionHistoryTile(
                    isPayment = (checkIsPayment(item.received.toString(), item.sent.toString())),
                    isConfirmed = false,
                    received = item.received.toString(),
                    sent = item.sent.toString(),
                    txId = item.txid,
                    timestamp = "pending",
                )
                // space between
                Spacer(Modifier.padding(vertical = 10.dp))
            } else{ // if confirmed
                TransactionHistoryTile(
                    isPayment = (checkIsPayment(item.received.toString(), item.sent.toString())),
                    isConfirmed = (checkIsConfirmed(item.confirmationTime.toString())),
                    received = item.received.toString(),
                    sent = item.sent.toString(),
                    txId = item.txid,
                    timestamp = item.confirmationTime!!.timestamp.timestampToString()
                )
                // space between
                Spacer(Modifier.padding(vertical = 10.dp))
            }
        }
    }
}

//tile for display of a single transaction record (like a row)
@Composable
fun TransactionHistoryTile(
    isPayment: Boolean,
    isConfirmed: Boolean,
    received: String,
    sent: String,
    txId: String,
    timestamp: String
){
    val urlHandler = LocalUriHandler.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            // when user clicks on specific transaction tile
            // they are redirected to a blockchain explorer for that transaction
            .clickable {
                urlHandler.openUri("https://mempool.space/testnet/tx/${txId}")
            }
    ) {

        // for confirmed transactions
        if(isConfirmed){
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .height(50.dp)
                    .border(
                        2.dp, if (isPayment) {
                            ZephyrusColors.lightPurplePrimary
                        } else {
                            ZephyrusColors.lightBlue
                        }, RoundedCornerShape(8.dp)
                    )
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ){
                // displays how much was sent/received
                Text(
                    text = if(isPayment){ "- ${formatSats(sent)} Sats"} else { "+ ${formatSats(received)} Sats"},
                    fontSize = 18.sp,
                    fontFamily = sourceSans,
                    color = if(isPayment){ZephyrusColors.lightPurplePrimary} else {ZephyrusColors.lightBlue},
                )
                Spacer(Modifier.padding(10.dp))
                // displays when (time)
                Text(
                    text = timestamp,
                    fontSize = 15.sp,
                    fontFamily = sourceSans,
                    color = if(isPayment){ZephyrusColors.lightPurplePrimary} else{ZephyrusColors.lightBlue},
                )
            }
        } else { // for unconfirmed/pending transactions

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .height(50.dp)
                    .border(2.dp, ZephyrusColors.lightGrey, RoundedCornerShape(8.dp))
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ){
                // displays how much was sent/received
                Text(
                    text = if(isPayment){ "- ${formatSats(sent)}"} else { "+ ${formatSats(received)}"},
                    fontSize = 18.sp,
                    fontFamily = sourceSans,
                    color = ZephyrusColors.lightGrey,
                )
                // displays when (time)
                Text(
                    text = "Pending",
                    fontSize = 18.sp,
                    fontFamily = sourceSans,
                    color = ZephyrusColors.lightGrey,
                )
            }
        }
    }
}

@Composable
fun BitcoinPrice(
    btcPrice: String
){
    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(vertical = 10.dp)
    ) {
        Text(
            text = "$btcPrice USD",
            fontSize = 18.sp,
            fontFamily = sourceSans,
            color = ZephyrusColors.fontColorWhite,
        )
    }


}

// check if its payment to someone or you receive
fun checkIsPayment(received: String, sent: String): Boolean{
    val receivedSats = received.toInt()
    val sentSats = sent.toInt()

    // returns true if received less than sent
    return (receivedSats - sentSats) < 0
}

// check if tx is confirmed or pending
fun checkIsConfirmed(confirmationTime: String): Boolean{

    // returns true if it has confirmation time
    return (confirmationTime != null)
}

fun getBitcoinPrice(
    context: Context,
    btcBalance: String
): String{
    val url = "https://api.coindesk.com/v1/bpi/currentprice.json"
    var priceUSD = "0"
    val queue = Volley.newRequestQueue(context)

    val stringRequest = StringRequest(
        Request.Method.GET, url,
        { response ->
            var jsonData = JSONObject(response)
            var price = jsonData.getJSONObject("bpi")
                        .getJSONObject("USD")
                        .getString("rate")
            priceUSD = price
        },
        { error ->
            Log.d(TAG, error.toString()) }
    )
    queue.add(stringRequest)

    val balancePrice = (btcBalance.toDouble() / 100000000) * priceUSD.toDouble()
    return balancePrice.toString()
}

//@Preview(device = Devices.PIXEL_4, showBackground = true)
//@Composable
//fun PreviewTransactionHistoryTile(){
//    TransactionHistoryTile(isPayment = true, isConfirmed = true, received = "23,000", sent = "13,324", timestamp = "jan 3 2023")
//}