package com.example.mlkitqrandbarcodescanner

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class BarCodeAndQRCodeAnalyser(private val barcodeListener: BarcodeAnalyzerListener) :
    ImageAnalysis.Analyzer {

    /**
     * if  you want to  scan QR codes as well you need to uncomment  the  FORMAT_QR_CODE option and vice  versa
     * i am leaving it commented for now
     */
    // Get an instance of BarcodeScanner
    private val options = BarcodeScannerOptions.Builder().setBarcodeFormats(
        Barcode.FORMAT_CODE_39,
        Barcode.FORMAT_CODE_93,
        Barcode.FORMAT_CODE_128,
        Barcode.FORMAT_CODABAR,
        Barcode.FORMAT_EAN_13,
        Barcode.FORMAT_EAN_8,
        Barcode.FORMAT_ITF,
        Barcode.FORMAT_UPC_A,
        Barcode.FORMAT_UPC_E/*,
        FORMAT_QR_CODE*/
    ).build()

    private val scanner by lazy {
        BarcodeScanning. getClient(options)
    }

    @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            // Pass image to the scanner and have it do its thing
            scanner.process(image)
                .addOnSuccessListener { barcodes ->

                    // Task completed successfully
                    if (barcodes.isEmpty().not()) {
                        barcodeListener(barcodes)
                    }


                }
                .addOnFailureListener {
                    it.printStackTrace()
                }
                .addOnCompleteListener {
                    // It's important to close the imageProxy
                    imageProxy.close()
                }
        }
    }
}