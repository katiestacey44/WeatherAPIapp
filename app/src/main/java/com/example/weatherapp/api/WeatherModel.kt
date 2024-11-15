package com.example.weatherapp.api

data class WeatherModel(
    val current: Current,
    val forecast: Forecast,
    val location: Location
)
