package com.example.qrscanner

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.SparseArray
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.util.isNotEmpty
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private val requestCodeCameraPermission = 1001
    private lateinit var cameraSource: CameraSource
    private lateinit var detector: BarcodeDetector


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            askForCameraPermission();
        } else {
            setupControls();
        }
    }

    private fun setupControls() {
        detector = BarcodeDetector.Builder(this@MainActivity).build()
        cameraSource = CameraSource.Builder(this@MainActivity, detector).setAutoFocusEnabled(true).build()
        CameraSurfaceView.holder.addCallback(SurfaceCallBack)
        detector.setProcessor(processor)

    }

    private fun askForCameraPermission() {
        ActivityCompat.requestPermissions(
            this@MainActivity, arrayOf(Manifest.permission.CAMERA), requestCodeCameraPermission
        )

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== requestCodeCameraPermission && grantResults.isNotEmpty())
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                setupControls()
            }
        else
            {
                Toast.makeText(applicationContext,"Permission Denied!",Toast.LENGTH_SHORT).show()

            }
    }
    private val SurfaceCallBack=object :SurfaceHolder.Callback{
        override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

        }

        override fun surfaceDestroyed(holder: SurfaceHolder?) {
            cameraSource.stop()
        }

        override fun surfaceCreated(holder: SurfaceHolder?) {
            try {
                cameraSource.start(holder)

            }catch (exception:Exception)
            {
                Toast.makeText(applicationContext,"Something Went Wrong!",Toast.LENGTH_SHORT).show()
            }

        }


    }

    private val processor=object : Detector.Processor<Barcode>
    {

        override fun release() {

        }
        override fun receiveDetections(p0: Detector.Detections<Barcode>?) {
            if(p0!=null && p0.detectedItems.isNotEmpty())
            {
                val qrCodes: SparseArray<Barcode> = p0.detectedItems
                val code = qrCodes.valueAt(0)
                TextScanResult.text=code.displayValue
            }
            else
            {
                TextScanResult.text=""
            }

        }


    }


}

