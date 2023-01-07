package xyz.tomashrib.zephyruswallet.ui.start

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import xyz.tomashrib.zephyruswallet.ui.theme.ZephyrusColors
import xyz.tomashrib.zephyruswallet.R
import xyz.tomashrib.zephyruswallet.WalletCreateType
import xyz.tomashrib.zephyruswallet.ui.Screen
import xyz.tomashrib.zephyruswallet.ui.theme.sourceSans

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WalletChoiceScreen(
    navController: NavController,
    onBuildWalletButtonClicked: (WalletCreateType) -> Unit
){
    Scaffold() {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(ZephyrusColors.bgColorBlack)
        ) {

            //references for items in constraint layout
            val (image, creation, recovery) = createRefs()

            //column layout which contains Image, Spacer and Text under each other
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 100.dp)
                    .constrainAs(image) {
                        top.linkTo(parent.top)
                    },
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
                    text = stringResource(R.string.app_name),
                    color = ZephyrusColors.fontColorWhite,
                    fontSize = 30.sp,
                    fontFamily = sourceSans,
                )
            }

            //button that generates a new wallet
            Button(
                onClick = { onBuildWalletButtonClicked(WalletCreateType.FROMSCRATCH()) },
                colors = ButtonDefaults.buttonColors(ZephyrusColors.lightPurplePrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .size(width = 320.dp, height = 150.dp)
                    .padding(vertical = 10.dp, horizontal = 10.dp)
                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(10.dp))
                    .constrainAs(creation) {
                        bottom.linkTo(recovery.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ){
                //text which is displayed on the button
                Text(
                    stringResource(R.string.generate_new_wallet),
                    fontSize = 25.sp,
                    fontFamily = sourceSans,
                    textAlign = TextAlign.Center,
                    lineHeight = 30.sp,
                    color = ZephyrusColors.darkerPurpleOnPrimary,
                )
            }

            //button that recovers an existing wallet
            Button(
                onClick = { navController.navigate(Screen.WalletRecoveryScreen.route) },
                colors = ButtonDefaults.buttonColors(ZephyrusColors.lightPurplePrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .size(width = 320.dp, height = 150.dp)
                    .padding(vertical = 10.dp, horizontal = 10.dp)
                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(10.dp))
                    .constrainAs(recovery) {
                        bottom.linkTo(parent.bottom, margin = 110.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ){
                //text which is displayed on the button
                Text(
                    stringResource(R.string.recover_existing_wallet),
                    fontSize = 25.sp,
                    fontFamily = sourceSans,
                    textAlign = TextAlign.Center,
                    lineHeight = 30.sp,
                    color = ZephyrusColors.darkerPurpleOnPrimary,
                )
            }
        }
    }
}