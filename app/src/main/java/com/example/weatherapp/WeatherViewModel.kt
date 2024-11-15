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
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private val weatherApi = RetrofitInstance.weatherApi
    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherModel>>()
    val weatherResult: LiveData<NetworkResponse<WeatherModel>> = _weatherResult

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
}