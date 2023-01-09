package xyz.tomashrib.zephyruswallet.ui.start

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import xyz.tomashrib.zephyruswallet.R
import xyz.tomashrib.zephyruswallet.WalletCreateType
import xyz.tomashrib.zephyruswallet.ui.theme.ZephyrusColors
import xyz.tomashrib.zephyruswallet.ui.theme.sourceSans

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WalletRecoveryScreen(
    onBuildWalletButtonClicked: (WalletCreateType) -> Unit
){
    Scaffold(
    ){

        ConstraintLayout(
            modifier = Modifier
                .fillMaxHeight(1f)
        ) {

            val (title, main, recover) = createRefs()

            val emptyRecoveryPhrase: Map<Int, String> = mapOf(
                1 to "", 2 to "", 3 to "", 4 to "", 5 to "", 6 to "",
                7 to "", 8 to "", 9 to "", 10 to "", 11 to "", 12 to ""
            )
            val (recoveryPhraseWordMap, setRecoveryPhraseWordMap) = remember { mutableStateOf(emptyRecoveryPhrase) }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ZephyrusColors.bgColorBlack)
                    .constrainAs(title) {
                        top.linkTo(parent.top)
                    }
            ){
                Column{
                    Text(
                        text = stringResource(R.string.recover_wallet),
                        color = ZephyrusColors.fontColorWhite,
                        fontSize = 25.sp,
                        fontFamily = sourceSans,
                        modifier = Modifier
                            .padding(top = 80.dp, bottom = 8.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }

            MyList(
                recoveryPhraseWordMap,
                setRecoveryPhraseWordMap,
                modifier = Modifier
                    .constrainAs(main){
                        top.linkTo(title.bottom)
                        bottom.linkTo(recover.top)
                        height = Dimension.fillToConstraints
                    }
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ZephyrusColors.bgColorBlack)
                    .constrainAs(recover){
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ){
                Column{
                    Button(
                        onClick = {
                            onBuildWalletButtonClicked(
                                WalletCreateType.RECOVER(
                                    buildRecoveryPhrase(recoveryPhraseWordMap)
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(ZephyrusColors.lightPurplePrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .size(width = 320.dp, height = 120.dp)
                            .padding(vertical = 10.dp, horizontal = 10.dp)
                            .shadow(elevation = 4.dp, shape = RoundedCornerShape(10.dp))
                    ){
                        Text(
                            text = stringResource(R.string.recover_wallet),
                            fontSize = 20.sp,
                            fontFamily = sourceSans,
                            textAlign = TextAlign.Center,
                            lineHeight = 30.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MyList(
    recoveryPhraseWordMap: Map<Int, String>,
    setRecoveryPhraseWordMap: (Map<Int, String>) -> Unit,
    modifier: Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        modifier
            .fillMaxWidth(1f)
            .background(ZephyrusColors.bgColorBlack)
            .verticalScroll(state = scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val focusManager = LocalFocusManager.current
        for (i in 1..12) {
            WordField(wordNumber = i, recoveryPhraseWordMap, setRecoveryPhraseWordMap, focusManager)
        }
    }
}

@Composable
fun WordField(
    wordNumber: Int,
    recoveryWordMap: Map<Int, String>,
    setRecoveryPhraseWordMap: (Map<Int, String>) -> Unit,
    focusManager: FocusManager
) {
    OutlinedTextField(
        value = recoveryWordMap[wordNumber] ?: "elvis is here",
        onValueChange = { newText ->
            val newMap: MutableMap<Int, String> = recoveryWordMap.toMutableMap()
            newMap[wordNumber] = newText

            val updatedMap = newMap.toMap()
            setRecoveryPhraseWordMap(updatedMap)
        },
        label = {
            Text(
                text = "Word $wordNumber",
                color = ZephyrusColors.fontColorWhite,
            )
        },
        textStyle = TextStyle(
            fontSize = 18.sp,
            color = ZephyrusColors.fontColorWhite
        ),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = ZephyrusColors.lightPurplePrimary,
            unfocusedBorderColor = ZephyrusColors.fontColorWhite,
            cursorColor = ZephyrusColors.lightPurplePrimary,
        ),
        modifier = Modifier
            .padding(8.dp),
        keyboardOptions = when (wordNumber) {
            12 -> KeyboardOptions(imeAction = ImeAction.Done)
            else -> KeyboardOptions(imeAction = ImeAction.Next)
        },
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) },
            onDone = { focusManager.clearFocus() }
        ),
        singleLine = true,
    )
}

// input words can have capital letters, space around them, space inside of them
private fun buildRecoveryPhrase(recoveryPhraseWordMap: Map<Int, String>): String {
    var recoveryPhrase = ""
    recoveryPhraseWordMap.values.forEach() {
        recoveryPhrase = recoveryPhrase.plus(it.trim().replace(" ", "").lowercase().plus(" "))
    }
    return recoveryPhrase.trim()
}