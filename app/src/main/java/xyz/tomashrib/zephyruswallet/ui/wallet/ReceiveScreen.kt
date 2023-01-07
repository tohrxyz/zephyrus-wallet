package xyz.tomashrib.zephyruswallet.ui.wallet

import android.view.RoundedCorner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
internal fun ReceiveScreen(navController: NavController){
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(ZephyrusColors.bgColorBlack)
    ) {
        val (title, qrCode, address, copy, regenerate) = createRefs()

        //displays title text
        Text(
            text = stringResource(R.string.receive_address),
            color = ZephyrusColors.fontColorWhite,
            fontSize =  25.sp,
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

        //displays qr code with btc address
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .constrainAs(qrCode) {
                    top.linkTo(title.bottom)
                    bottom.linkTo(copy.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    height = Dimension.fillToConstraints
                }
                .padding(top = 50.dp)
        ){
            Image(
                painter = painterResource(R.drawable.qr_code_btc),
                contentDescription = "QR code btc address",
                modifier = Modifier
                    .size(200.dp)
                    .padding(10.dp)
                    .background(ZephyrusColors.lightPurplePrimary)
            )

            Spacer(Modifier.padding(30.dp))

            Text(
                text = "bc1q 7cyr fmck 2ffu 2ud3 rn5l 5a8y v6f0 chkp 0zpemf",
                color = ZephyrusColors.lightPurplePrimary,
                fontSize = 18.sp,
                fontFamily = sourceSans,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .padding(start = 7.dp)
            )

            Spacer(Modifier.padding(5.dp))

            Button(
                onClick = {  },
                colors = ButtonDefaults.buttonColors(ZephyrusColors.lightPurplePrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .size(width = 120.dp, height = 60.dp)
                    .padding(vertical = 10.dp, horizontal = 10.dp)
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(10.dp)),
            ){
                //text which is displayed on the button
                Text(
                    stringResource(R.string.copy),
                    fontSize = 18.sp,
                    fontFamily = sourceSans,
                    textAlign = TextAlign.Center,
                    lineHeight = 30.sp,
                    color = ZephyrusColors.darkerPurpleOnPrimary,
                )
            }
        }
    }
}