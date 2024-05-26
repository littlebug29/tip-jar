package com.example.tipjar

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tipjar.ui.ReceiptScreen
import com.example.tipjar.ui.TipCalculationScreen
import com.example.tipjar.ui.TipHistoryScreen
import com.example.tipjar.ui.TipJarTheme
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContent {
            TipJarTheme {
                TipJarApp()
            }
        }
    }

    @Composable
    fun TipJarApp() {
        val navController = rememberNavController()
        val viewModel = viewModel<MainViewModel>()
        var shouldTakePhoto by remember { mutableStateOf(false) }
        var imageUri by remember { mutableStateOf<Uri?>(null) }
        val context = LocalContext.current

        val cameraLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    viewModel.savePayment(imageUri.toString())
                    navController.navigate(TIP_HISTORY_DESTINATION)
                }
            }
        val cameraPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                val uri = createImageUri(context)
                imageUri = uri
                cameraLauncher.launch(uri)
            } else {
                Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
        NavHost(navController = navController, startDestination = TIP_CALCULATION_DESTINATION) {
            composable(TIP_CALCULATION_DESTINATION) {
                TipCalculationScreen(
                    navController = navController,
                    viewModel = viewModel,
                    shouldTakePhoto = shouldTakePhoto,
                    onCheckChange = { shouldTakePhoto = it },
                    onSavePaymentClick = {
                        if (shouldTakePhoto) {
                            when (PackageManager.PERMISSION_GRANTED) {
                                ContextCompat.checkSelfPermission(
                                    context,
                                    android.Manifest.permission.CAMERA
                                ) -> {
                                    val uri = createImageUri(context)
                                    imageUri = uri
                                    cameraLauncher.launch(uri)
                                }

                                else -> {
                                    cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                                }
                            }
                        } else {
                            viewModel.savePayment(null)
                            navController.navigate("history")
                        }
                    }
                )
            }
            composable(TIP_HISTORY_DESTINATION) {
                TipHistoryScreen(navController = navController, viewModel = viewModel)
            }
            composable(RECEIPT_DESTINATION) { backStackStrategy ->
                Text(text = "Receipt")
            }
        }
    }

    private fun createImageUri(context: Context): Uri {
        val filename = "receipt_${System.currentTimeMillis()}.jpg"
        val file = File(context.externalCacheDir, filename)
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    companion object {
        const val TIP_CALCULATION_DESTINATION = "tipCalculation"
        const val TIP_HISTORY_DESTINATION = "history"
        const val RECEIPT_DESTINATION = "receipt"
    }
}