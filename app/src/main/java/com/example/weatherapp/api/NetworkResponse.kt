package com.example.weatherapp.api

sealed class NetworkResponse<out T> {
    data class Success<out T>(val data: T) : NetworkResponse<T>()  // Correct constructor syntax
    data class Error(val message: String) : NetworkResponse<Nothing>() // For error handling
    object Loading : NetworkResponse<Nothing>() // To represent loading state
}
