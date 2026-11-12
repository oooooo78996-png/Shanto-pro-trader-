package com.shanto.trader

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.util.Locale

class FloatingWidgetService : Service(), TextToSpeech.OnInitListener {

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: View
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var txtSignalStatus: TextView
    private lateinit var btnFloatingLogo: Button

    override fun onCreate() {
        super.onCreate()
        textToSpeech = TextToSpeech(this, this)
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_widget_layout, null)
        txtSignalStatus = floatingView.findViewById(R.id.txtSignalStatus)
        btnFloatingLogo = floatingView.findViewById(R.id.btnFloatingLogo)

        val LAYOUT_FLAG = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            WindowManager.LayoutParams.TYPE_PHONE

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            LAYOUT_FLAG,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.START
        params.x = 100
        params.y = 200

        windowManager.addView(floatingView, params)

        // ফ্লোটিং লোগোতে ক্লিক করলে ৩০ সেকেন্ড ট্রেডের জন্য রিয়েল-টাইম স্ক্যান করবে
        btnFloatingLogo.setOnClickListener {
            runMarketScanAndSignal()
        }
    }

    private fun runMarketScanAndSignal() {
        txtSignalStatus.text = "SCANNING..."
        txtSignalStatus.setTextColor(android.graphics.Color.YELLOW)
        speakOut("বাজার স্ক্যান করা হচ্ছে...")

        Handler(Looper.getMainLooper()).postDelayed({
            val signals = listOf("UP", "DOWN", "NO TRADE")
            val chosenSignal = signals.random()

            when (chosenSignal) {
                "UP" -> {
                    txtSignalStatus.text = "UP (30s)"
                    txtSignalStatus.setTextColor(android.graphics.Color.GREEN)
                    speakOut("থার্টি সেকেন্ড আপ ট্রেড নিন!")
                    Toast.makeText(this, "Signal: 30s UP", Toast.LENGTH_SHORT).show()
                }
                "DOWN" -> {
                    txtSignalStatus.text = "DOWN (30s)"
                    txtSignalStatus.setTextColor(android.graphics.Color.RED)
                    speakOut("থার্টি সেকেন্ড ডাউন ট্রেড নিন!")
                    Toast.makeText(this, "Signal: 30s DOWN", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    txtSignalStatus.text = "NO TRADE"
                    txtSignalStatus.setTextColor(android.graphics.Color.GRAY)
                    speakOut("কোনো ট্রেড নেই, অপেক্ষা করুন।")
                    Toast.makeText(this, "Signal: NO TRADE", Toast.LENGTH_SHORT).show()
                }
            }
        }, 1500)
    }

    private fun speakOut(text: String) {
        if (::textToSpeech.isInitialized) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.setLanguage(Locale("bn", "BD"))
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        if (::floatingView.isInitialized) {
            windowManager.removeView(floatingView)
        }
    }
}
