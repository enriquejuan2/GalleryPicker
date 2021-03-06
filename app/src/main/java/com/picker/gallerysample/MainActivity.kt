package com.picker.gallerysample

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import com.picker.gallery.model.GalleryData
import com.picker.gallery.model.interactor.GalleryPicker
import com.picker.gallery.utils.MLog
import kotlinx.android.synthetic.main.activity_main.*
import com.picker.gallery.view.PickerActivity

class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_READ_WRITE = 123

    val REQUEST_RESULT_CODE = 101

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (isReadWritePermitted()) getGalleryResults() else checkReadWritePermission()
        val i = Intent(this@MainActivity, PickerActivity::class.java)
        i.putExtra("IMAGES_LIMIT", 4)
        i.putExtra("VIDEOS_LIMIT", 1)
        i.putExtra("REQUEST_RESULT_CODE", REQUEST_RESULT_CODE)
        startActivityForResult(i, 101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == REQUEST_RESULT_CODE && data != null) {
            val mediaList = data.getParcelableArrayListExtra<GalleryData>("MEDIA")
            MLog.e("SELECTED MEDIA", mediaList.size.toString())
        }
    }

    fun getGalleryResults() {
        val images = GalleryPicker(this).getImages()
        val videos = GalleryPicker(this).getVideos()
        text.text = "IMAGES COUNT: ${images.size}\nVIDEOS COUNT: ${videos.size}"
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun checkReadWritePermission(): Boolean {
        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_READ_WRITE)
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_READ_WRITE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) getGalleryResults()
        }
    }

    private fun isReadWritePermitted(): Boolean = (checkCallingOrSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkCallingOrSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
}
