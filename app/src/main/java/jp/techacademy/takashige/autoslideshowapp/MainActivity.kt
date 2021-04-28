package jp.techacademy.takashige.contentprovider

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.util.Log
import android.provider.MediaStore
import android.content.ContentUris
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import com.google.android.material.snackbar.Snackbar



class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_CODE = 100
    private var PHOTO = listOf<android.net.Uri>()
    private var NUM = 0
    private var Button_NUM = 0
    private var mHandler = Handler()
    private var mTimer: Timer? = null
    private var mTimerSec = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkCallingOrSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getContentsInfo_0()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_CODE
                )
            }
        } else {
            getContentsInfo_0()
        }
        button_0.setOnClickListener {
            getContentsInfo_1()
        }
        button_1.setOnClickListener {
            getContentsInfo_2()
        }
        button_2.setOnClickListener {
            if (PHOTO.size >= 1) {
                if (Button_NUM == 0) {
                    mTimer = Timer()
                    mTimer!!.schedule(object : TimerTask() {
                        override fun run() {
                            mTimerSec += 2.0
                            NUM = (NUM + 1) % PHOTO.size
                            mHandler.post {
                                imageView.setImageURI(PHOTO[NUM])
                            }
                        }
                    }, 2000, 2000)
                    Button_NUM = 1
                    button_0.isEnabled = false
                    button_1.isEnabled = false
                    button_2.text = "停止"
                } else {
                    mTimer!!.cancel()
                    mTimer = null
                    Button_NUM = 0
                    button_0.isEnabled = true
                    button_1.isEnabled = true
                    button_2.text = "再生"
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo_0()
                } else {
                    button_0.isEnabled = false
                    button_1.isEnabled = false
                    button_2.isEnabled = false
                }
        }
    }

    private fun getContentsInfo_0() {

        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null
        )

        if (cursor!!.moveToFirst()){
            do {
                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                val imageUris =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                Log.d("ANDROID", "URI : " + imageUris.toString())
                Log.d("ANDROID", imageUris.javaClass.toString())
                PHOTO += (imageUris)
            } while (cursor.moveToNext())
        }
        cursor.close()
        imageView.setImageURI(PHOTO[NUM])
    }

    private fun getContentsInfo_1() {
        if (PHOTO.size >= 1) {
            NUM = (NUM + 1) % PHOTO.size
            imageView.setImageURI(PHOTO[NUM])
        }
    }

    private fun getContentsInfo_2() {
        if (PHOTO.size >= 1) {
            NUM = (NUM + (PHOTO.size - 1)) % PHOTO.size
            imageView.setImageURI(PHOTO[NUM])
        }
    }
}
