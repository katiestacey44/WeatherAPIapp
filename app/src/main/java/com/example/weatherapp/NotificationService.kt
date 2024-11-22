package com.example.weatherapp

import android.content.Context
import androidx.core.app.NotificationCompat

class NotificationService(
    private val context: Context
) {

    fun showNotification()｛
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)

    ｝

    }

    companion object{
        const val CHANNEL_ID = "channel_id"
    }

}