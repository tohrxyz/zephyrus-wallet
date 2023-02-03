package xyz.tomashrib.zephyruswallet.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import xyz.tomashrib.zephyruswallet.R
import xyz.tomashrib.zephyruswallet.data.Repository
import xyz.tomashrib.zephyruswallet.ui.theme.ZephyrusColors
import xyz.tomashrib.zephyruswallet.ui.theme.sourceSans

// screen for display of seed words
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RecoveryPhraseScreen(navController: NavController){

    val seedPhrase: String = Repository.getMnemonic()
    val wordList: List<String> = seedPhrase.split(" ")

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

            //displays text
            Text(
                text = stringResource(R.string.recovery_phrase_screen),
                color = ZephyrusColors.fontColorWhite,
                fontSize = 30.sp,
                fontFamily = sourceSans,
                modifier = Modifier
                    .padding(top = 50.dp)
            )

            Spacer(Modifier.padding(vertical = 20.dp))

            // cycle to display each of 12 words
            wordList.forEachIndexed { index, item ->
                Text(
                    text = "${index + 1}. $item",
                    modifier = Modifier.weight(weight = 1F),
                    color = ZephyrusColors.fontColorWhite,
                    fontFamily = sourceSans
                )
            }
        }
    }
}