package xyz.tomashrib.zephyruswallet.ui.wallet

import android.view.RoundedCorner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import xyz.tomashrib.zephyruswallet.R
import xyz.tomashrib.zephyruswallet.ui.Screen
import xyz.tomashrib.zephyruswallet.ui.theme.ZephyrusColors
import xyz.tomashrib.zephyruswallet.ui.theme.sourceSans
import xyz.tomashrib.zephyruswallet.ui.theme.sourceSansSemiBold


@Composable
internal fun HomeScreen(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ZephyrusColors.bgColorBlack),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        Spacer(Modifier.padding(30.dp))

        Row(
            Modifier
                .fillMaxWidth()
//                .background(ZephyrusColors.lightPurplePrimary)
                .padding(horizontal = 15.dp)
                .height(120.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "123,434",
                fontFamily = sourceSansSemiBold,
                fontSize = 40.sp,
                color = ZephyrusColors.lightPurplePrimary,
            )
            Spacer(Modifier.padding(5.dp))
            Text(
                text = "Sats",
                fontFamily = sourceSansSemiBold,
                fontSize = 40.sp,
                color = ZephyrusColors.lightPurplePrimary,
            )
        }
        Spacer(Modifier.padding(50.dp))

//        //button that goes to transaction history screen
//        Button(
//            onClick = {  },
//            colors = ButtonDefaults.buttonColors(ZephyrusColors.lightPurplePrimary),
//            shape = RoundedCornerShape(10.dp),
//            modifier = Modifier
//                .size(width = 320.dp, height = 100.dp)
//                .padding(vertical = 10.dp, horizontal = 10.dp)
//                .shadow(elevation = 4.dp, shape = RoundedCornerShape(10.dp)),
//        ){
//            //text which is displayed on the button
//            Text(
//                stringResource(R.string.transaction_history),
//                fontSize = 25.sp,
//                fontFamily = sourceSans,
//                textAlign = TextAlign.Center,
//                lineHeight = 30.sp,
//                color = ZephyrusColors.darkerPurpleOnPrimary,
//            )
//        }

        //transaction history box

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
//                .background(ZephyrusColors.darkerPurpleOnPrimary, shape = RoundedCornerShape(10.dp))
        ){
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(ZephyrusColors.fontColorWhite)
//                    .border(2.dp, ZephyrusColors.lightPurplePrimary)
                    .padding(horizontal = 15.dp, vertical = 8.dp),
            ) {
                Text(
                    text = stringResource(R.string.pending),
                    fontFamily = sourceSans,
                    fontSize = 20.sp,
                    color = ZephyrusColors.bgColorBlack,
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(80.dp)
//                    .background(ZephyrusColors.lightPurplePrimary)
                    .border(2.dp, ZephyrusColors.fontColorWhite)
                    .padding(horizontal = 15.dp, vertical = 8.dp)
            ){
                Text(
                    text = stringResource(R.string.transaction_history),
                    fontFamily = sourceSans,
                    fontSize = 15.sp,
                    color = ZephyrusColors.fontColorWhite,
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(ZephyrusColors.fontColorWhite)
//                    .border(2.dp, ZephyrusColors.lightPurplePrimary)
                    .padding(horizontal = 15.dp, vertical = 8.dp),
            ) {
                Text(
                    text = stringResource(R.string.confirmed),
                    fontFamily = sourceSans,
                    fontSize = 20.sp,
                    color = ZephyrusColors.bgColorBlack,
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(80.dp)
//                    .background(ZephyrusColors.lightPurplePrimary)
                    .border(2.dp, ZephyrusColors.fontColorWhite)
                    .padding(horizontal = 15.dp, vertical = 8.dp)
            ){
                Text(
                    text = stringResource(R.string.transaction_history),
                    fontFamily = sourceSans,
                    fontSize = 15.sp,
                    color = ZephyrusColors.fontColorWhite,
                )
            }
        }



        Spacer(Modifier.padding(70.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .height(120.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ){

            Button(
                onClick = { navController.navigate(Screen.ReceiveScreen.route) },
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
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.ic_round_sync_black),
                contentDescription = "sync",
                modifier = Modifier
                    .background(
                        ZephyrusColors.fontColorWhite,
                        shape = RoundedCornerShape(10.dp)
                    )
//                    .height(80.dp)
                    .weight(0.5f)
//                    .shadow(elevation = 5.dp, shape = RoundedCornerShape(10.dp))
                    .clickable { }
                    .clip(RoundedCornerShape(10.dp))
                    .padding(horizontal = 5.dp)
            )
            Spacer(Modifier.padding(horizontal = 5.dp))

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