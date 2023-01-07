package xyz.tomashrib.zephyruswallet.ui.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import xyz.tomashrib.zephyruswallet.R
import xyz.tomashrib.zephyruswallet.ui.theme.ZephyrusColors
import xyz.tomashrib.zephyruswallet.ui.theme.sourceSans

@Composable
internal fun SendScreen(navController: NavController){
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
            TransactionAddressInput()
            TransactionAmountInput()
            TransactionFeeInput()
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
                onClick = {  },
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
private fun TransactionAddressInput(){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        var inputText by remember { mutableStateOf("") }

        OutlinedTextField(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth(0.9f),
            value = inputText,
            onValueChange = { inputText = it },
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
private fun TransactionAmountInput(){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        var inputText by remember { mutableStateOf("") }

        OutlinedTextField(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth(0.9f),
            value = inputText,
            onValueChange = { inputText = it },
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
private fun TransactionFeeInput(){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        var inputText by remember { mutableStateOf("") }

        OutlinedTextField(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth(0.9f),
            value = inputText,
            onValueChange = { inputText = it },
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