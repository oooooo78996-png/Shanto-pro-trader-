package com.shanto.trader

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnOn = findViewById<Button>(R.id.btnOn)
        val btnOff = findViewById<Button>(R.id.btnOff)

        // TURN ON বাটনে ক্লিক করলে পারমিশন চেক করে ফ্লোটিং সার্ভিস চালু হবে
        btnOn.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.CanDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivityForResult(intent, 2084)
                Toast.makeText(this, "দয়া করে ফ্লোটিং পারমিশন অন করুন", Toast.LENGTH_LONG).show()
            } else {
                val serviceIntent = Intent(this, FloatingWidgetService::class.java)
                startService(serviceIntent)
                Toast.makeText(this, "Floating AI Turned ON", Toast.LENGTH_SHORT).show()
            }
        }

        // TURN OFF বাটনে ক্লিক করলে ফ্লোটিং সার্ভিস বন্ধ হয়ে যাবে
        btnOff.setOnClickListener {
            val serviceIntent = Intent(this, FloatingWidgetService::class.java)
            stopService(serviceIntent)
            Toast.makeText(this, "Floating AI Turned OFF", Toast.LENGTH_SHORT).show()
        }
    }
}
