package com.example.weatherapp.api

data class WeatherModel(
    val location: Location,
    val current: Current,
    val forecast: Forecast
)


