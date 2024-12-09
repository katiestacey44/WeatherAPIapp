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
        val days = inputData.getInt("days", 1)

        // Fetch weather data
        val weatherData = getWeatherData(apiKey, city, days) ?: return Result.failure()

        // Send the notification with the fetched weather data
        sendNotification(weatherData)

        Log.d("NotificationWorker", "Notification sent successfully")
        return Result.success()
    }

    private suspend fun getWeatherData(apiKey: String, city: String, days: Int): WeatherModel? {
        if (city.isEmpty()) {
            return null
        }

        return try {
            val response = withContext(Dispatchers.IO) {
                RetrofitInstance.weatherApi.getWeather(apiKey, city, days.toString())
            }
            when (response) {
                is NetworkResponse.Success<*> -> {
                    Log.d("NotificationWorker", "Weather API response successful")
                    response.body()
                }
                else -> {
                    Log.e("NotificationWorker", "Weather API response failed: $response")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("NotificationWorker", "Error fetching weather data: ${e.message}")
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
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, notificationChannelId)
            .setContentTitle("Weather Update")
            .setContentText("Current Temp: $currentTemp°F, Condition: $currentCondition")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(0, notification)
    }
}
