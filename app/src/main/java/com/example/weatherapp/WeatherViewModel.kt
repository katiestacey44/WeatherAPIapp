package com.example.weatherapp

import android.app.Application
import android.app.Notification
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.weatherapp.api.Constant
import com.example.weatherapp.api.NetworkResponse
import com.example.weatherapp.api.RetrofitInstance
import com.example.weatherapp.api.WeatherModel
import com.example.weatherapp.workers.NotificationWorker
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalTime
import java.util.concurrent.TimeUnit

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext = application.applicationContext
    private val db = Firebase.firestore
    private val weatherApi = RetrofitInstance.weatherApi
    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherModel>>()
    val weatherResult: LiveData<NetworkResponse<WeatherModel>> = _weatherResult
    private val _favoriteLocations = MutableLiveData<List<String>>(emptyList())
    val favoriteLocations: LiveData<List<String>> = _favoriteLocations
    private val locationHelper = LocationHelper(appContext)

    fun getData(city: String) {
        _weatherResult.value = NetworkResponse.Loading

        viewModelScope.launch {
            try {
                val response = weatherApi.getWeather(Constant.apiKey, city, Constant.days)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _weatherResult.value = NetworkResponse.Success(it)
                    }
                } else {
                    _weatherResult.value =
                        NetworkResponse.Error("Failed to load data, please try again")
                }
            } catch (e: Exception) {
                _weatherResult.value =
                    NetworkResponse.Error("Failed to load data: ${e.message}, please try again")
            }
        }
    }

    fun getDataByCoordinates(latitude: Double, longitude: Double) {
        _weatherResult.value = NetworkResponse.Loading

        viewModelScope.launch {
            try {
                val response = weatherApi.getWeatherByCoordinates(Constant.apiKey, "$latitude,$longitude", Constant.days)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _weatherResult.value = NetworkResponse.Success(it)
                    }
                } else {
                    _weatherResult.value =
                        NetworkResponse.Error("Failed to load data for coordinates, please try again")
                }
            } catch (e: Exception) {
                _weatherResult.value =
                    NetworkResponse.Error("Failed to load data for coordinates: ${e.message}, please try again")
            }
        }
    }

    // Add location to favorites
    fun addFavorite(city: String, uid: String) {
        val currentFavorites = _favoriteLocations.value.orEmpty().toMutableList()
        currentFavorites.add(city)
        _favoriteLocations.value = currentFavorites

        // add favorite to firebase
        data class Favorite (
            val city: String
        )
        val favorite = Favorite(city)

        val userGamesCollection = db.collection("users").document(uid).collection("favorites")

        userGamesCollection.add(favorite).addOnSuccessListener { documentReference ->
            println("Game added with ID: ${documentReference.id}")
        }.addOnFailureListener { e ->
            println("Error adding game, $e")
        }
    }

    // Remove Location from favorites
    fun removeFavorite(city: String, uid: String) {
        val currentFavorites = _favoriteLocations.value.orEmpty().toMutableList()
        currentFavorites.remove(city)
        _favoriteLocations.value = currentFavorites

        val favoritesCollection = db.collection("users").document(uid).collection("favorites")

        favoritesCollection.whereEqualTo("city", city).get()
            .addOnSuccessListener { querySnapshot ->
                val document = querySnapshot.documents.firstOrNull()
                document?.reference?.delete()
                    ?.addOnSuccessListener {
                        Log.d("WeatherViewModel", "Location removed from favorites")
                    }
                    ?.addOnFailureListener { e ->
                        Log.e("WeatherViewModel", "Error removing city from favorites: $e")
                    }
            }
    }

    fun getFavorites(uid: String) {
        val favoritesCollection = db.collection("users").document(uid).collection("favorites")
        favoritesCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val favoriteCities = querySnapshot.documents.mapNotNull { document ->
                    document.getString("city")
                }
                _favoriteLocations.value = favoriteCities
            }
            .addOnFailureListener { exception ->
                Log.e("WeatherViewModel", "Error getting favorite cities: $exception")
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleNotification(notificationTime: LocalTime, city: String, context: Context) {
        val inputData = workDataOf(
            "apiKey" to Constant.apiKey,
            "city" to city,
            "days" to Constant.days
        )

        val notificationWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInputData(inputData)
            .setInitialDelay(calculateDelay(notificationTime), TimeUnit.MILLISECONDS)
            .addTag("daily_notification")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "daily_notification",
            ExistingWorkPolicy.REPLACE,
            notificationWorkRequest
        )
    }

    fun cancelNotification() {
        WorkManager.getInstance().cancelAllWorkByTag("daily_notification")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateDelay(notificationTime: LocalTime): Long {
        val now = LocalTime.now()
        var delay = Duration.between(now, notificationTime).toMillis()
        if (delay < 0) {
            delay += TimeUnit.DAYS.toMillis(1)
        }
        return delay
    }

    fun saveNotificationEnabled(context: Context, isEnabled: Boolean) {
        val sharedPreferences = context.getSharedPreferences("notification_settings", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("notifications_enabled", isEnabled)
        editor.apply()
    }

    fun isNotificationEnabled(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences("notification_settings", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("notifications_enabled", false)
    }


    fun saveCity(city: String) {
        val sharedPreferences = appContext.getSharedPreferences("WeatherAppPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("city", city).apply()
    }

}