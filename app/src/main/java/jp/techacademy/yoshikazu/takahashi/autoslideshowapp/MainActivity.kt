package jp.techacademy.yoshikazu.takahashi.autoslideshowapp

import android.content.ContentUris
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val PRC = 100
    private var imageUriList = ArrayList<Uri>()
    private var imageUriIndex = 0
    private var mTimer: Timer? = null
    private var autoScrollFlag = false
    private var mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getContentsInfo()
            } else {
                requestPermissions(
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    PRC
                )
            }
        }else {
            getContentsInfo()
        }

        previous_button.setOnClickListener {
            if (imageUriIndex == 0) {
                imageUriIndex = imageUriList.size - 1
            }else {
                imageUriIndex -= 1
            }
            imageView.setImageURI(imageUriList[imageUriIndex])
        }

        next_button.setOnClickListener {
            slideShowNext()
        }

        auto_button.setOnClickListener {
            if (mTimer == null) {
                if (!autoScrollFlag) {
                    autoScrollFlag = true
                    previous_button.isClickable = false
                    next_button.isClickable = false
                    mTimer = Timer()

                    mTimer!!.schedule(object : TimerTask() {
                        override fun run() {
                            mHandler.post {
                                slideShowNext()
                            }
                        }
                    }, 2000, 2000)
                } else {
                    autoScrollFlag = false
                    previous_button.isClickable = true
                    next_button.isClickable = true
                    mTimer!!.cancel()
                    mTimer = null
                }
            }
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            PRC ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                }
        }
    }

    private fun getContentsInfo() {
        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null
        )

        if (cursor!!.moveToFirst()) {
            do {
                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                Log.d("ANDROID", "URI:$imageUri")
                imageUriList.add(imageUri)
            }while(cursor.moveToNext())
        }
        cursor.close()
        imageView.setImageURI(imageUriList[imageUriIndex])
    }

    private fun slideShowNext() {
        if (imageUriIndex == imageUriList.size - 1) {
            imageUriIndex = 0
        }else {
            imageUriIndex += 1
        }
        Log.d("TEST", "index:${imageUriIndex}")
        Log.d("TEST", "URI:${imageUriList[imageUriIndex]}")
        imageView.setImageURI(imageUriList[imageUriIndex])
    }
}