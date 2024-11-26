package com.example.weatherapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.api.Constant
import com.example.weatherapp.api.NetworkResponse
import com.example.weatherapp.api.RetrofitInstance
import com.example.weatherapp.api.WeatherModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val weatherApi = RetrofitInstance.weatherApi
    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherModel>>()
    val weatherResult: LiveData<NetworkResponse<WeatherModel>> = _weatherResult
    private val _favoriteLocations = MutableLiveData<List<String>>(emptyList())
    val favoriteLocations: LiveData<List<String>> = _favoriteLocations

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
    fun removeFavorite(city: String) {
        val currentFavorites = _favoriteLocations.value.orEmpty().toMutableList()
        currentFavorites.remove(city)
        _favoriteLocations.value = currentFavorites
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
}