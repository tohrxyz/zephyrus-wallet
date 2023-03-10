package xyz.tomashrib.zephyruswallet.ui.wallet

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import org.bitcoindevkit.PartiallySignedTransaction
import org.bitcoindevkit.TransactionDetails
import xyz.tomashrib.zephyruswallet.R
import xyz.tomashrib.zephyruswallet.data.Wallet
import xyz.tomashrib.zephyruswallet.tools.TAG
import xyz.tomashrib.zephyruswallet.tools.formatSats
import xyz.tomashrib.zephyruswallet.ui.Screen
import xyz.tomashrib.zephyruswallet.ui.theme.ZephyrusColors
import xyz.tomashrib.zephyruswallet.ui.theme.sourceSans
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

@Composable
internal fun SendScreen(navController: NavController, context: Context){

    val (showDialog, setShowDialog) =  remember { mutableStateOf(false) }

    val recipientAddress: MutableState<String> = remember { mutableStateOf("") }
    val amount: MutableState<String> = remember { mutableStateOf("") }
    val feeRate: MutableState<String> = remember { mutableStateOf("") }

    val qrCodeScanner =
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>("BTC_Address")
        ?.observeAsState()
    qrCodeScanner?.value.let {
        if (it != null){
            if(it.substring(0,8) == "bitcoin:"){
                recipientAddress.value = it.substring(8)
//                Log.i("qrcode", "${it.substring(0,8)} -> ${it.substring(8)}")
            } else {
                recipientAddress.value = it
            }
        }
//            Log.i("qrcode", "naskenovana: ${recipientAddress.value}")

        navController.currentBackStackEntry?.savedStateHandle?.remove<String>("BTC_Address")
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(ZephyrusColors.bgColorBlack)
    ){
        val (title, transactionInputFields, broadcastButton) = createRefs()

        Text(
            text = stringResource(R.string.send_sats),
            color = ZephyrusColors.fontColorWhite,
            fontSize = 25.sp,
            fontFamily = sourceSans,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .constrainAs(title) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .padding(top = 80.dp)
        )

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .constrainAs(transactionInputFields){
                    top.linkTo(title.bottom)
                    bottom.linkTo(broadcastButton.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
//                    height = Dimension.fillToConstraints
                }
        ){

            // displays text, upon click goes to scan qr code
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "Scan QR code",
                    fontSize = 15.sp,
                    fontFamily = sourceSans,
                    color = ZephyrusColors.lightPurplePrimary,
                    modifier = Modifier

                        //upon click, it goes to qr code scan screen
                        .clickable {

                            navController.navigate(Screen.QRScanScreen.route) {
                                launchSingleTop = true
                            }
                        }
                )

                //paste address button
                Text(
                    text = stringResource(R.string.paste_address),
                    fontSize = 15.sp,
                    fontFamily = sourceSans,
                    color = ZephyrusColors.lightPurplePrimary,
                    modifier = Modifier

                        //upon click, the address from clipboard is inserted into recipientAddress input field
                        .clickable {
                            try {

                                //checks if input from clipboard passed safety checks from function
                                if (pasteFromClipboard(context) != "Wrong format") {
                                    recipientAddress.value = pasteFromClipboard(context)
                                } else {

                                    //notifies user that address from clipboard is not of correct format
                                    Toast
                                        .makeText(context, "Wrong address format", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            } catch (e: Exception) {

                                //notifies user that they have empty clipboard
                                Toast
                                    .makeText(context, "Empty clipboard", Toast.LENGTH_SHORT)
                                    .show()
                                Log.i(TAG, "Error while pasting from clipboard: $e")
                            }
                        }
                )
            }

            // input field for address entry
            TransactionAddressInput(recipientAddress)

            // input field for amount entry
            TransactionAmountInput(amount)

            // row layout for two buttons: Balance and Send All
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                //wallet balance is displayed here under amount input field
                Text(
                    //formats the wallet balance like #,###,###
                    text = "Balance: ${formatSats(Wallet.getBalance().toString())} Sats",
                    fontSize = 15.sp,
                    fontFamily = sourceSans,
                    color = ZephyrusColors.lightPurplePrimary,
                )

                // send all button is displayed here under amount input field
                Text(
                    text = "Send All",
                    fontSize = 15.sp,
                    fontFamily = sourceSans,
                    color = ZephyrusColors.lightPurplePrimary,
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .clickable {
                            try{
                                // builds a transaction
                                val (psbt: PartiallySignedTransaction, txDetails: TransactionDetails) = Wallet.createSendAllTransaction(recipientAddress.value, feeRate.value.toFloat())
                                // puts balance - tx fee into amount field
                                amount.value = (txDetails.sent - txDetails.fee!!.toULong()).toString()
                            } catch (e: Exception){
                                // some instructions for user
                                if(recipientAddress.value.isEmpty()){
                                    Toast.makeText(context, "Enter valid address!", Toast.LENGTH_SHORT).show()
                                }
                                else if(feeRate.value.isEmpty()){
                                    Toast.makeText(context, "Enter fee rate!", Toast.LENGTH_SHORT).show()
                                } else{
                                    Toast.makeText(context, "$e", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                )
            }

            // input field for fee rate entry
            TransactionFeeInput(feeRate)

            //clears all input fields
            Text(
                text = stringResource(R.string.clear_all),
                fontSize = 15.sp,
                fontFamily = sourceSans,
                color = ZephyrusColors.lightPurplePrimary,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 20.dp)

                    //when clicked it clears all input fields
                    .clickable {
                        recipientAddress.value = ""
                        amount.value = ""
                        feeRate.value = ""
                    }
            )

            //transaction confirmation dialog
            Dialog(
                recipientAddress = recipientAddress.value,
                amount = amount.value,
                feeRate = feeRate.value,
                showDialog = showDialog,
                setShowDialog = setShowDialog,
                context,
                navController
            )
        }

        // layout for send button
        Column(
            Modifier
                .constrainAs(broadcastButton) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .padding(bottom = 40.dp)
        ){

            // button that envokes transaction confirmation Dialog
            Button(
                onClick = {

                    //prevent app crashing when the input fields are empty
                    if(recipientAddress.value != "" && amount.value != "" && feeRate.value != ""){
                        setShowDialog(true)
                    } else{
                        Log.i(TAG, "The input fields are empty.")

                        //notifies user that they didnt enter any inputs
                        Toast.makeText(context, "Empty input fields", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(ZephyrusColors.lightPurplePrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .size(width = 300.dp, height = 80.dp)
                    .padding(vertical = 10.dp, horizontal = 30.dp)
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(10.dp))
            ){

                Text(
                    text = stringResource(R.string.send_broadcast),
                    color = ZephyrusColors.darkerPurpleOnPrimary,
                    fontSize = 25.sp,
                    fontFamily = sourceSans,
                    lineHeight = 30.sp,
                )
            }
        }
    }
}

// styling for address  input field
@Composable
private fun TransactionAddressInput(recipientAddress: MutableState<String>){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ){

        OutlinedTextField(
            modifier = Modifier
                .padding(bottom = 10.dp)
                .fillMaxWidth(0.9f),
            value = recipientAddress.value,
            onValueChange = {
                recipientAddress.value = it
            },
            label = {
                Text(
                    text = stringResource(R.string.send_address),
                    color = ZephyrusColors.fontColorWhite,
                )
            },
            singleLine = true,
            textStyle = TextStyle(fontFamily = sourceSans, color = ZephyrusColors.fontColorWhite),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = ZephyrusColors.lightPurplePrimary,
                unfocusedBorderColor = ZephyrusColors.fontColorWhite,
                cursorColor = ZephyrusColors.lightPurplePrimary,
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next // displays 'go next' button on keyboard
            )
        )
    }
}

// sending amount input field
@Composable
private fun TransactionAmountInput(amount: MutableState<String>){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ){

        OutlinedTextField(
            modifier = Modifier
                .padding(vertical = 5.dp)
                .fillMaxWidth(0.9f),
            value = amount.value,
            onValueChange = {

                //does checking if user entered only Integers
                //amount to send cannot be text nor decimal number
                try {

                    //if user input can be parsed as Integer
                    if(it.toIntOrNull() != null){
                        val num = it.toIntOrNull()
                        amount.value = num.toString()
                    }

                    //if not Integer, clear input
                    //this basically prevents user from ever entering anything, but integer - its kinda workaround
                    else {
                        amount.value = ""
                    }
                }catch (e: Exception){
                    Log.i(TAG, "Wrong input amount: $e")
                }
            },
            label = {
                Text(
                    text = stringResource(R.string.send_amount),
                    color = ZephyrusColors.fontColorWhite,
                )
            },
            singleLine = true,
            textStyle = TextStyle(fontFamily = sourceSans, color = ZephyrusColors.fontColorWhite),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = ZephyrusColors.lightPurplePrimary,
                unfocusedBorderColor = ZephyrusColors.fontColorWhite,
                cursorColor = ZephyrusColors.lightPurplePrimary,
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number, // limits keyboard type for number entry
                imeAction = ImeAction.Next // displays 'go next' button on keyboard
            )
        )
    }
}

// fee rate input field
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun TransactionFeeInput(feeRate: MutableState<String>){
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ){

        OutlinedTextField(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth(0.9f),
            value = feeRate.value,
            onValueChange = {

                //checks if user entered only Integers
                try{

                    //does checking if input is Integer
                    //fee rate cannot be text
                    //can be float, but for now it Integer is sufficient
                    if(it.toIntOrNull() != null){
                        val num = it.toIntOrNull()
                        feeRate.value = num.toString()
                    }

                    //if not integer, clear input
                    //this basically prevents user from ever entering anything, but integer - its kinda workaround
                    else {
                        feeRate.value = ""
                    }
                }catch (e: Exception){
                    Log.i(TAG, "Wrong fee input: $e")
                }
            },
            label = {
                Text(
                    text = stringResource(R.string.send_fee_rate),
                    color = ZephyrusColors.fontColorWhite,
                )
            },
            singleLine = true,
            textStyle = TextStyle(fontFamily = sourceSans, color = ZephyrusColors.fontColorWhite),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = ZephyrusColors.lightPurplePrimary,
                unfocusedBorderColor = ZephyrusColors.fontColorWhite,
                cursorColor = ZephyrusColors.lightPurplePrimary,
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number, // limits keyboard type for number entry
                imeAction = ImeAction.Done, // displays 'done' on keyboard
            ),
            keyboardActions = KeyboardActions(
                onDone = { keyboardController!!.hide() } // hides keyboard when 'gone' is clicked
            )
        )
    }
}

// transcation confirmation dialog
@SuppressLint("UnrememberedMutableState")
@Composable
fun Dialog(
    recipientAddress: String,
    amount: String,
    feeRate: String,
    showDialog: Boolean,
    setShowDialog: (Boolean) -> Unit,
    context: Context,
    navController: NavController
) {
    var isEntered = mutableStateOf(false) // mutable variable for state of isEntered value
    var totalFee: Int // variable for total fee to be used when sending btc tx
    try{
        //this tries to create a transaction
        //because we want to get amount of total transaction fees
        //also needs to include psbt, because Wallet.createTransaction() returns TXBuilder
        //no further use of psbt
        val (psbt: PartiallySignedTransaction, txDetails: TransactionDetails) = Wallet.createTransaction(recipientAddress, amount.toULong(), feeRate.toFloat())
        totalFee = txDetails.fee!!.toInt()
        isEntered.value = true
    }catch (e: Exception){
//        Log.i(TAG, "Started SendScreen just now")
        totalFee = 0
    }

    // when it should be displayed
    if (showDialog) {
        AlertDialog(
            containerColor = ZephyrusColors.bgColorBlack,
            onDismissRequest = {},
            title = {
                Text(
                    text = "Confirm transaction",
                    color = ZephyrusColors.fontColorWhite
                )
            },
            text = {
                //format this so it looks nicer
                //make more Text() composables and name of things should be bold or different color idk
                Text(
                    text = "Send: $amount\nto: $recipientAddress\nFee rate: ${feeRate.toFloat()}\nTotal fee: $totalFee",
                    color = ZephyrusColors.fontColorWhite
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        try{
                            // attempts to broadcast transaction to network
                            broadcastTransaction(recipientAddress, amount.toULong(), feeRate.toFloat(), context, navController)
                            setShowDialog(false) // closes dialog
                        }catch(e: Exception){
                            Log.i(TAG, "Failed broadcast: $e")
                        }
                    },
                ) {
                    Text(
                        text = "Confirm",
                        color = ZephyrusColors.fontColorWhite
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        setShowDialog(false) // closes dialog
                    },
                ) {
                    Text(
                        text = "Cancel",
                        color = ZephyrusColors.fontColorWhite
                    )
                }
            },
        )
    }
}

// attempts to broadcast transaction to bitcoin network
private fun broadcastTransaction(recipientAddress: String, amount: ULong, feeRate: Float = 1F, context: Context, navController: NavController) {
    Log.i(TAG, "Attempting to broadcast transaction with inputs: recipient: $recipientAddress, amount: $amount, fee rate: $feeRate")
    Toast.makeText(context, "Attempting to broadcast...", Toast.LENGTH_SHORT).show() // notifies user
    try {
        // create, sign, and broadcast transaction
        val (psbt: PartiallySignedTransaction, txDetails: TransactionDetails)  = Wallet.createTransaction(recipientAddress, amount, feeRate)
        Wallet.sign(psbt)
        val txid: String = Wallet.broadcast(psbt)
        Log.i(TAG, "Transaction was broadcasted! txid: $txid")

        //notifies user about successfully broadcasting their transaction
        Toast.makeText(context, "Transaction was broadcasted!", Toast.LENGTH_SHORT).show()

        //because its successfull, it goes back to HomeScreen
        navController.navigate(Screen.HomeScreen.route)
    } catch (e: Throwable) {
        Log.i(TAG, "Broadcast error: ${e.message}")

        //notifies user that they entered invalid address
        Toast.makeText(context, "Broadcast error: invalid address", Toast.LENGTH_LONG).show()
    }
}

// returns string from clipboard
private fun pasteFromClipboard(context: Context): String{

    //lengths of different bitcoin address formats
    val legacyAddressLenght = 32
    val segwitAddressLenght = 42
    val taprootAddressLenght = 62

    //initialize clipboardManager
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    //get input from clipboard
    val item = clipboardManager.primaryClip!!.getItemAt(0)

    //check if null
    if(item == null || item.text == null || clipboardManager.primaryClip == null){
        return "Wrong format"
        //could return "Null input"
    }

    //converts clipboard input to String
    val pasteData = item.text.toString()

    //check if empty
    if (pasteData.isEmpty()){
        return "Wrong format"
        //could return "Empty string"
    }

    //check if valid bitcoin address format length
    if(pasteData.length != legacyAddressLenght && pasteData.length != segwitAddressLenght && pasteData.length != taprootAddressLenght) {
        return "Wrong format"
    }

    //default return
    return pasteData
}