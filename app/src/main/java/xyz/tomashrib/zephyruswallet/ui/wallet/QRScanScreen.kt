package xyz.tomashrib.zephyruswallet.ui.wallet

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import xyz.tomashrib.zephyruswallet.tools.QRCodeAnalyzer
import xyz.tomashrib.zephyruswallet.ui.Screen
import xyz.tomashrib.zephyruswallet.ui.theme.ZephyrusColors
import xyz.tomashrib.zephyruswallet.ui.theme.sourceSans

// screen where btc address is scanned from a QR code using camera
@Composable
internal fun QRScanScreen(
    navController: NavHostController,
    sendScreenViewModel: SendScreenViewModel = viewModel()
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val qrScanned = rememberSaveable { mutableStateOf(false) }

    val cameraProviderState = remember { mutableStateOf<ProcessCameraProvider?>(null) }

    cameraProviderFuture.addListener(
        {
            val cameraProvider = cameraProviderFuture.get()
            cameraProviderState.value = cameraProvider
        },
        ContextCompat.getMainExecutor(context)
    )


    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // asks to give a permission to use Camera
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    LaunchedEffect(key1 = true) {
        launcher.launch(Manifest.permission.CAMERA)
    }

    // whole screen with camera vision + cancel button
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = ZephyrusColors.bgColorBlack,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        NavigateToSendScreen(navController, qrScanned)
        ConstraintLayout(
            modifier = Modifier.fillMaxSize(),
        ) {
            val (camera, cancelButton) = createRefs()

            Box(
                modifier = Modifier
                    .background(ZephyrusColors.bgColorBlack)
                    .constrainAs(camera) {
                        top.linkTo(parent.top)
                        absoluteLeft.linkTo(parent.absoluteLeft)
                        absoluteRight.linkTo(parent.absoluteRight)
                        bottom.linkTo(parent.bottom)
                    }
            ) {
                Column {
                    if (hasCameraPermission) { // if it has camera permission, it starts to scan
                        AndroidView(
                            factory = { context ->
                                val previewView = PreviewView(context)
                                val preview = Preview.Builder().build()
                                val selector = CameraSelector.Builder()
                                    .requireLensFacing(CameraSelector.LENS_FACING_BACK) // scan with rear camera
                                    .build()
                                preview.setSurfaceProvider(previewView.surfaceProvider)
                                val imageAnalysis = ImageAnalysis.Builder()
                                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                    .build()
                                imageAnalysis.setAnalyzer(
                                    ContextCompat.getMainExecutor(context),
                                    QRCodeAnalyzer { result ->
                                        result?.let {
                                            if (!qrScanned.value) {
                                                Log.d("QRScanScreen", "Scanned QR code successfully: $it")
                                                sendScreenViewModel.scannedAddress.value = it
                                                qrScanned.value = true
                                                cameraProviderState.value?.unbindAll()
                                            }
                                        } ?: run {
                                            Log.d("QRScanScreen", "Failed to scan QR code")
                                        }
                                    }
                                )

                                try {
                                    cameraProviderFuture.get().bindToLifecycle(
                                        lifecycleOwner,
                                        selector,
                                        preview,
                                        imageAnalysis
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                                return@AndroidView previewView
                            },
                            modifier = Modifier.weight(weight = 1f)
                        )
                    }
                }
            }

            // button that cancels qr code scanning and goes back to send screen
            Button(
                onClick = {
                    navController.popBackStack()
                },
                colors = ButtonDefaults.buttonColors(ZephyrusColors.lightPurplePrimary),
                shape = RoundedCornerShape(20.dp),
//                border = BorderStroke(3.dp, ZephyrusColors.fontColorWhite),
                modifier = Modifier
                    .constrainAs(cancelButton) {
                        start.linkTo(parent.start, margin = 16.dp)
                        end.linkTo(parent.end, margin = 16.dp)
                        bottom.linkTo(parent.bottom, margin = 16.dp)
                    }
                    .padding(top = 4.dp, start = 4.dp, end = 4.dp, bottom = 4.dp)
                    .height(70.dp)
                    .width(200.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "Cancel",
                        color = ZephyrusColors.darkerPurpleOnPrimary,
                        fontSize = 18.sp,
                        fontFamily = sourceSans
                    )
                }
            }
        }
    }
}

@Composable
fun NavigateToSendScreen(navController: NavController, qrScanned: MutableState<Boolean>) {
    LaunchedEffect(qrScanned.value) {
        if (qrScanned.value) {
            navController.navigate(Screen.SendScreen.route) {
                popUpTo(Screen.SendScreen.route) { inclusive = true; saveState = false }
            }
            qrScanned.value = false
        }
    }
}

