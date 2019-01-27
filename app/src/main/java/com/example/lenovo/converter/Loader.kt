package com.example.lenovo.converter

import android.app.Activity
import android.app.IntentService
import android.content.Intent
import android.os.Bundle
import android.os.ResultReceiver
import android.util.Log
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL

class Loader : IntentService("Loader") {

    override fun onHandleIntent(intent: Intent) {
        val url = intent.getStringExtra("EXTRA_URL")
        val rec = intent.getParcelableExtra("RECEIVER") as ResultReceiver
        val bundle = Bundle()
        bundle.putByteArray("RESULT_VALUE", loadByteArray(url))
        bundle.putString("REQUEST", intent.getStringExtra("REQUEST"))
        bundle.putString("VALUE", intent.getStringExtra("VALUE"))
        bundle.putString("VIEW_NUMBER", intent.getStringExtra("VIEW_NUMBER"))
        rec.send(Activity.RESULT_OK, bundle)
    }

    private fun loadByteArray(url: String?): ByteArray {
        Log.d(logTag, "Downloading from ${url}")
        return URL(url).openConnection().run {
            Log.d(logTag, "Opened Connection")
            connect()
            Log.d(logTag, "Connected")
            val code = (this as? HttpURLConnection)?.responseCode
            Log.d(logTag, "Response code: $code")
            val buffer = ByteArrayOutputStream()
            getInputStream().copyTo(buffer)
            buffer.toByteArray()
        }
    }

    companion object {
        private const val logTag = "Loader"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(logTag, "onCreate: ")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(logTag, "onStartCommand: ")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(logTag, "onDestroy: ")
    }
}
