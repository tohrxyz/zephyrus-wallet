package xyz.tomashrib.zephyruswallet.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import xyz.tomashrib.zephyruswallet.ui.theme.ZephyrusColors
import xyz.tomashrib.zephyruswallet.R
import xyz.tomashrib.zephyruswallet.ui.wallet.WalletNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WalletScreen(navController: NavController){
    val scope = rememberCoroutineScope()

    //closed drawer by default
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val items = listOf(Icons.Default.Favorite, Icons.Default.Face, Icons.Default.Email, Icons.Default.Face)
    val selectedItem = remember { mutableStateOf(items[0]) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContainerColor = ZephyrusColors.bgColorBlack,
        drawerContent = {

            //column layout, displaying image and text under it
            Column(
                Modifier
                    .background(ZephyrusColors.lightPurplePrimary)
                    .height(250.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ){

                //displays image
                Image(
                    painter = painterResource(R.drawable.zephyrus_wallet_logo),
                    contentDescription = "Zephyrus Wallet Logo",
                    Modifier
                        .size(120.dp)
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(30.dp))
                )

                //displays text
                Text(
                    text = stringResource(R.string.app_name),
                    color = ZephyrusColors.surfaceBlack,
                    fontSize = 15.sp,
                )

                //displays space to bottom
                Spacer(Modifier.padding(bottom = 20.dp))

            }

            NavigationDrawerItem(
                label = { Text(stringResource(R.string.recovery_phrase)) },
                selected = items[0] == selectedItem.value,
                onClick = { navController.navigate(Screen.RecoveryPhraseScreen.route) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = ZephyrusColors.bgColorBlack,
                    unselectedContainerColor = ZephyrusColors.bgColorBlack,
                    selectedTextColor = ZephyrusColors.lightPurplePrimary,
                    unselectedTextColor = ZephyrusColors.lightPurplePrimary,
                )
            )
        },
        content = {
          Scaffold(){
              WalletNavigation()
          }
        }
    )
}