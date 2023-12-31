package com.panisset.notificator

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MainActivity : ComponentActivity() {
  private val imageArray = arrayOf(R.drawable.eduzz, R.drawable.hotmart, R.drawable.monettize, R.drawable.braip, R.drawable.kiwify, R.drawable.nubank, R.drawable.perfectpay)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.layout)
    val imageButton: ImageView = findViewById(R.id.imageButton)
    createNotificationChannel()
    val notifyButton: Button = findViewById(R.id.Notify)


    setImageFromDrawableArray(imageButton)

    val spinnerOptions =
      arrayOf("Nubak", "PerfectPay", "Monettize", "Kiwify", "Hotmart", "Eduzz", "Braip")

    // Set up the spinner
    val spinner: Spinner = findViewById(R.id.spinner)
    val spinnerArrayAdapter =
      ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerOptions)
    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    spinner.adapter = spinnerArrayAdapter

    // Handling spinner item selections
    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val selectedItem = parent.getItemAtPosition(position).toString()
        updateImage(imageButton, selectedItem)

        // Assuming getImageResId returns the appropriate drawable resource ID
        val imageResId = getImageResId(selectedItem)
        showNotification("Selected Item", "You selected: $selectedItem", imageResId)
      }

      override fun onNothingSelected(parent: AdapterView<*>) {
        // Optional: Handle the case where no item was selected
      }
    }
    notifyButton.setOnClickListener {
      val selectedPosition = spinner.selectedItemPosition
      val selectedItem = spinnerOptions[selectedPosition]
      val imageResId = getImageResId(selectedItem)
      showNotification("Selected Item", "You selected: $selectedItem", imageResId)
    }

  }
    private fun updateImage(imageView: ImageView, selectedItem: String) {
        val imageResId = when (selectedItem) {
          "Nubak" -> R.drawable.nubank // Replace with your actual resource IDs
          "PerfectPay" -> R.drawable.perfectpay
          "Monettize" -> R.drawable.monettize
          "Kiwify" -> R.drawable.kiwify
          "Hotmart" -> R.drawable.hotmart
          "Eduzz" -> R.drawable.eduzz
          "Braip" -> R.drawable.braip
          else -> R.drawable.nubank // Default or fallback image
        }
        imageView.setImageResource(imageResId)
      }

      fun onNothingSelected(parent: AdapterView<*>) {
        // Optional: Handle the case where no item was selected
      }

  private fun setImageFromDrawableArray(imageButton: ImageView) {
    try {
      val randomIndex = (0 until imageArray.size).random()
      imageButton.setImageResource(imageArray[randomIndex])
    } catch (e: Exception) {
      Log.e("ImageError", "Error setting image: ${e.message}")
    }
  }


  private fun createNotificationChannel() {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val name = "notify" // Directly using a string literal
      val descriptionText = "notify_description"
      val importance = NotificationManager.IMPORTANCE_DEFAULT
      val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
        description = descriptionText
      }
      // Register the channel with the system
      val notificationManager: NotificationManager =
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.createNotificationChannel(channel)
    }
  }
  private fun showNotification(title: String, content: String, iconResId: Int) {
    val intent = Intent(this, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    val pendingIntentFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    } else {
      PendingIntent.FLAG_UPDATE_CURRENT
    }

    val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, pendingIntentFlag)

    val notification = NotificationCompat.Builder(this, CHANNEL_ID)
      .setSmallIcon(iconResId) // Use the selected image as small icon
      .setContentTitle(title)
      .setContentText(content)
      .setContentIntent(pendingIntent)
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .build()

    val notificationId = System.currentTimeMillis().toInt()

    // Post the notification
    val notificationManager = NotificationManagerCompat.from(this)
    if (ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.POST_NOTIFICATIONS
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      // TODO: Consider calling
      //    ActivityCompat#requestPermissions
      // here to request the missing permissions, and then overriding
      //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
      //                                          int[] grantResults)
      // to handle the case where the user grants the permission. See the documentation
      // for ActivityCompat#requestPermissions for more details.
      return
    }
    notificationManager.notify(notificationId, notification)

  }

  companion object {

    const val CHANNEL_ID = "first"
    const val NOTIFICATION_ID = 101
  }


private fun getImageResId(selectedItem: String): Int {
  return when (selectedItem) {
    "Nubak" -> R.drawable.nu
    "PerfectPay" -> R.drawable.perfectpay

    else -> R.drawable.nubank // Default or fallback image
  }
}


}
