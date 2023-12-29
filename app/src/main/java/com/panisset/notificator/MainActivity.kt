package com.panisset.notificator

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Button
import android.widget.EditText
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import java.io.IOException

class MainActivity : ComponentActivity() {
    private val PERMISSION_REQUEST_CODE = 1
    private var selectedImageUri: Uri? = null

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
            selectedImageUri = uri
            val imageButton: ImageButton = findViewById(R.id.imageButton)
            imageButton.setImageURI(uri)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout)
        val image: ImageButton = findViewById(R.id.imageButton)
        image.setOnClickListener { imagePicker(it) }
        val notifyButton: Button = findViewById(R.id.Notify)
        notifyButton.setOnClickListener { notifyUser() }
    }
    private fun imagePicker(view: View) {
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
            // Use PickVisualMediaRequest with ImageOnly
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
        }
    }

    private fun notifyUser() {
        val name = findViewById<EditText>(R.id.NameOfInstitution).text.toString()
        val description = findViewById<EditText>(R.id.Description).text.toString()
        val repeatCountText = findViewById<EditText>(R.id.numberOfiteractions).text.toString()
        val repeatCount = repeatCountText.toIntOrNull() ?: 0
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a notification channel (required for API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("your_channel_id", "Your Channel Name", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, "your_channel_id")
            .setContentTitle(name)
            .setContentText(description)

        selectedImageUri?.let { uri ->
            val bitmap = uriToBitmap(uri)
            bitmap?.let {
                builder.setSmallIcon(IconCompat.createWithBitmap(bitmap))
            }
        }

        // Notify
        for (i in 1..repeatCount) {
            notificationManager.notify(i, builder.build()) // Use 'i' as notification ID for uniqueness
        }
    }
    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)

            // Resize bitmap to 24x24 dp (which might be different in pixels depending on the screen density)
            val sizeInDp = 24
            val scale = resources.displayMetrics.density
            val sizeInPx = (sizeInDp * scale + 0.5f).toInt()
            Bitmap.createScaledBitmap(bitmap, sizeInPx, sizeInPx, true)
        } catch (e: IOException) {
            Log.e("MainActivity", "Error converting Uri to Bitmap", e)
            null
        }

    }
}
