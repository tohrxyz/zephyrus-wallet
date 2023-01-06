package xyz.tomashrib.zephyruswallet.ui.start

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.tomashrib.zephyruswallet.R
import xyz.tomashrib.zephyruswallet.ui.theme.ZephyrusColors
import xyz.tomashrib.zephyruswallet.ui.theme.sourceSans

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WalletRecoveryScreen(){
    Scaffold(
    ){
        //column layout which contains Image, Spacer and Text under each other
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ZephyrusColors.bgColorBlack),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ){
            //displays image
            Image(
                painter = painterResource(R.drawable.zephyrus_wallet_logo),
                contentDescription = "Zephyrus Wallet Logo",
                Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(35.dp))
            )

            //displays space between
            Spacer(modifier = Modifier.padding(12.dp))

            //displays text
            Text(
                text = stringResource(R.string.wallet_recovery_screen),
                color = ZephyrusColors.fontColorWhite,
                fontSize = 30.sp,
                fontFamily = sourceSans,
            )
        }
    }
}