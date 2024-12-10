package com.example.weatherapp.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.weatherapp.R
import com.example.weatherapp.api.WeatherModel
import com.example.weatherapp.api.Constant
import com.example.weatherapp.api.NetworkResponse
import com.example.weatherapp.api.RetrofitInstance
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

class NotificationWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        val apiKey = inputData.getString("apiKey") ?: return Result.failure()
        val city = inputData.getString("city") ?: return Result.failure()

        // Fetch weather data
        val weatherData = getWeatherData(city)
        if (weatherData != null) {
            sendNotification(weatherData)
            Log.d("NotificationWorker", "Notification sent successfully")
            return Result.success()
        } else {
            Log.e("NotificationWorker", "Failed to fetch weather data")
            return Result.failure()
        }
    }

    private suspend fun getWeatherData(city: String): WeatherModel? {
        return try {
            val response = withContext(Dispatchers.IO) {
                RetrofitInstance.weatherApi.getWeather(apiKey = "1fc460243bd649d898c163818242810", city = city, days = "1")
            }
            if (response.isSuccessful) {
                response.body()?.also {
                    Log.d("NotificationWorker", "Weather API response successful")
                }
            } else {
                Log.e("NotificationWorker", "Weather API response failed. Code: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("NotificationWorker", "Error fetching weather data: ${e.localizedMessage}")
            null
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendNotification(weatherData: WeatherModel) {
        val currentTemp = weatherData.current.temp_f
        val currentCondition = weatherData.current.condition.text
        Log.d("NotificationWorker", "Current Temp: $currentTemp, Condition: $currentCondition")

        // Proceed with creating and sending the notification
        val context = applicationContext
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannelId = "weather_notifications"
        val channel = NotificationChannel(
            notificationChannelId,
            "Weather Notifications",
            NotificationManager.IMPORTANCE_HIGH
        )

        val existingChannel = notificationManager.getNotificationChannel(notificationChannelId)
        if (existingChannel != null) {
            Log.d("NotificationWorker", "Notification channel already exists: $notificationChannelId")
        } else {
            Log.d("NotificationWorker", "Creating notification channel: $notificationChannelId")
        }
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, notificationChannelId)
            .setSmallIcon(R.drawable.ic_weather_icon)
            .setContentTitle("Weather Update")
            .setContentText("Current Temp: $currentTemp°F, Condition: $currentCondition")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(0, notification)
    }
}
