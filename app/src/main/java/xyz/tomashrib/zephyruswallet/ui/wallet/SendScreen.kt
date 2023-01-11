package xyz.tomashrib.zephyruswallet.ui.wallet

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import org.bitcoindevkit.PartiallySignedTransaction
import org.bitcoindevkit.TransactionDetails
import xyz.tomashrib.zephyruswallet.R
import xyz.tomashrib.zephyruswallet.data.Wallet
import xyz.tomashrib.zephyruswallet.tools.TAG
import xyz.tomashrib.zephyruswallet.tools.formatSats
import xyz.tomashrib.zephyruswallet.ui.theme.ZephyrusColors
import xyz.tomashrib.zephyruswallet.ui.theme.sourceSans

@Composable
internal fun SendScreen(navController: NavController){

    val (showDialog, setShowDialog) =  remember { mutableStateOf(false) }

    val recipientAddress: MutableState<String> = remember { mutableStateOf("") }
    val amount: MutableState<String> = remember { mutableStateOf("") }
    val feeRate: MutableState<String> = remember { mutableStateOf("") }

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
                    height = Dimension.fillToConstraints
                }
        ){
            TransactionAddressInput(recipientAddress)
            TransactionAmountInput(amount)

            //wallet balance is displayed here under amount input field
            Text(

                //formats the wallet balance like #,###,###
                text = "Balance: ${formatSats(Wallet.getBalance().toString())} Sats",
                fontSize = 15.sp,
                fontFamily = sourceSans,
                color = ZephyrusColors.lightPurplePrimary,
                modifier = Modifier
                    .align(Alignment.Start)

                    //when clicked, it puts wallet balance value into amount input field
                    .clickable { amount.value = Wallet.getBalance().toString() }
            )

            TransactionFeeInput(feeRate)


            Dialog(
                recipientAddress = recipientAddress.value,
                amount = amount.value,
                feeRate = feeRate.value,
                showDialog = showDialog,
                setShowDialog = setShowDialog
            )
        }

        Column(
            Modifier
                .constrainAs(broadcastButton) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .padding(bottom = 40.dp)
        ){

            Button(
                onClick = {

                    //prevent app crashing when the input fields are empty
                    if(recipientAddress.value != "" && amount.value != "" && feeRate.value != ""){
                        setShowDialog(true)
                    } else{
                        Log.i(TAG, "The input fields are empty.")
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

@Composable
private fun TransactionAddressInput(recipientAddress: MutableState<String>){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
//        var inputText by remember { mutableStateOf("") }

        OutlinedTextField(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth(0.9f),
            value = recipientAddress.value,
            onValueChange = { recipientAddress.value = it },
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
        )
    }
}

@Composable
private fun TransactionAmountInput(amount: MutableState<String>){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
//        var inputText by remember { mutableStateOf("") }

        OutlinedTextField(
            modifier = Modifier
                .padding(vertical = 5.dp)
                .fillMaxWidth(0.9f),
            value = amount.value,
            onValueChange = { amount.value = it },
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
        )
    }
}

@Composable
private fun TransactionFeeInput(feeRate: MutableState<String>){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
//        var inputText by remember { mutableStateOf("") }

        OutlinedTextField(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth(0.9f),
            value = feeRate.value,
            onValueChange = { feeRate.value = it },
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
        )
    }
}

@Composable
fun Dialog(
    recipientAddress: String,
    amount: String,
    feeRate: String,
    showDialog: Boolean,
    setShowDialog: (Boolean) -> Unit,
) {
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
                Text(
                    text = "Send: $amount\nto: $recipientAddress\nFee rate: ${feeRate.toFloat()}",
                    color = ZephyrusColors.fontColorWhite
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        broadcastTransaction(recipientAddress, amount.toULong(), feeRate.toFloat())
                        setShowDialog(false)
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
                        setShowDialog(false)
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

private fun broadcastTransaction(recipientAddress: String, amount: ULong, feeRate: Float = 1F) {
    Log.i(TAG, "Attempting to broadcast transaction with inputs: recipient: $recipientAddress, amount: $amount, fee rate: $feeRate")
    try {
        // create, sign, and broadcast
        val (psbt: PartiallySignedTransaction, txDetails: TransactionDetails)  = Wallet.createTransaction(recipientAddress, amount, feeRate)
        Wallet.sign(psbt)
        val txid: String = Wallet.broadcast(psbt)
        Log.i(TAG, "Transaction was broadcasted! txid: $txid")
    } catch (e: Throwable) {
        Log.i(TAG, "Broadcast error: ${e.message}")
    }
}

@Preview(device = Devices.PIXEL_4, showBackground = true)
@Composable
internal fun PreviewSendScreen() {
    SendScreen(rememberNavController())
}