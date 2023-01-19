package xyz.tomashrib.zephyruswallet.ui.wallet

import android.annotation.SuppressLint
import android.content.ClipData
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.graphics.createBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import xyz.tomashrib.zephyruswallet.R
import xyz.tomashrib.zephyruswallet.data.Wallet
import xyz.tomashrib.zephyruswallet.ui.theme.ZephyrusColors
import xyz.tomashrib.zephyruswallet.ui.theme.sourceSans
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import xyz.tomashrib.zephyruswallet.tools.TAG


internal class AddressViewModel : ViewModel() {

    private var _address: MutableLiveData<String> = MutableLiveData("No address yet")
    private var _addressIndex: MutableLiveData<UInt> = MutableLiveData(0u)
    val address: LiveData<String>
        get() = _address
    val addressIndex: LiveData<UInt>
        get() = _addressIndex

    fun updateAddressLastUnused() {
        _address.value = Wallet.getLastUnusedAddress().address
        _addressIndex.value = Wallet.getLastUnusedAddress().index
    }
    fun updateAddressNew() {
        _address.value = Wallet.getNewAddress().address
        _addressIndex.value = Wallet.getNewAddress().index
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
internal fun ReceiveScreen(
    navController: NavController,
    context: Context,
    addressViewModel: AddressViewModel = viewModel()
){
    val address by addressViewModel.address.observeAsState("Generate new address")

    //this generates last unused address upon ReceiveScreen composable launch
    addressViewModel.updateAddressLastUnused()

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(ZephyrusColors.bgColorBlack)
    ) {
        val (title, qrCode, buttons) = createRefs()

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

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .constrainAs(qrCode){
                    top.linkTo(title.bottom)
                    bottom.linkTo(buttons.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    height = Dimension.fillToConstraints
                }
        ){
            val qrcode: ImageBitmap? = addressToQR(address)
            Log.i("ReceiveScreen", "New receive address is $address")
            if (address != "No address yet" && qrcode != null) {
                Image(
                    bitmap = qrcode,
                    contentDescription = "Bitcoindevkit website QR code",
                    Modifier.size(250.dp)
                )
                Spacer(modifier = Modifier.padding(vertical = 20.dp))
                SelectionContainer {
                    Text(
                        text = address,
                        fontFamily = sourceSans,
                        color = ZephyrusColors.fontColorWhite,
                        fontSize = 15.sp,
                    )
                }
            }

            Spacer(Modifier.padding(10.dp))

            var addressCopied = mutableStateOf(false)
            Button(
                onClick = {
                    copyToClipboard(context, address)
                    addressCopied.value = true
                },
                colors = ButtonDefaults.buttonColors(ZephyrusColors.lightPurplePrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .size(width = 120.dp, height = 60.dp)
                    .padding(vertical = 10.dp, horizontal = 10.dp)
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(10.dp)),
            ){
                //text which is displayed on the button
                Text(
                    stringResource(if (addressCopied.value) R.string.copied else R.string.copy),
                    fontSize = 18.sp,
                    fontFamily = sourceSans,
                    textAlign = TextAlign.Center,
                    lineHeight = 30.sp,
                    color = ZephyrusColors.darkerPurpleOnPrimary,
                )
            }
        }

        Column(
            Modifier
                .constrainAs(buttons) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .padding(bottom = 25.dp)
        ){

            //this button generates new address every click
            Button(
                onClick = { addressViewModel.updateAddressNew() },
                colors = ButtonDefaults.buttonColors(ZephyrusColors.lightPurplePrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .height(90.dp)
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 10.dp, horizontal = 10.dp)
                    .shadow(elevation = 5.dp, shape = RoundedCornerShape(10.dp))
            ){

                Text(
                    text = stringResource(R.string.generate_new_address),
                    fontSize = 18.sp,
                    fontFamily = sourceSans,
                    textAlign = TextAlign.Center,
                    color = ZephyrusColors.darkerPurpleOnPrimary,
                    lineHeight = 30.sp,
                )
            }
        }
    }
}

private fun addressToQR(address: String): ImageBitmap? {
    Log.i("ReceiveScreen", "We are generating the QR code for address $address")
    try {
        val qrCodeWriter: QRCodeWriter = QRCodeWriter()
        val bitMatrix: BitMatrix = qrCodeWriter.encode(address, BarcodeFormat.QR_CODE, 1000, 1000)
        val bitMap = createBitmap(1000, 1000)
        for (x in 0 until 1000) {
            for (y in 0 until 1000) {
                // uses night1 and snow1 for colors
                bitMap.setPixel(x, y, if (bitMatrix[x, y]) 0xFF2e3440.toInt() else 0xFFd8dee9.toInt())
            }
        }
        // Log.i("ReceiveScreen", "QR is ${bitMap.asImageBitmap()}")
        return bitMap.asImageBitmap()
    } catch (e: Throwable) {
        Log.i("ReceiveScreen", "Error with QRCode generation, $e")
    }
    return null
}

private fun copyToClipboard(context: Context, address: String){
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("address", address)
    clipboardManager.setPrimaryClip(clip)

    Log.i(TAG, "Address was copied to clipboard!: $address")
}

//@Preview(device = Devices.PIXEL_4, showBackground = true)
//@Composable
//internal fun PreviewReceiveScreen() {
//    ReceiveScreen(rememberNavController())
//}