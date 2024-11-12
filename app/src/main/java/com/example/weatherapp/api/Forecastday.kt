package com.example.weatherapp.api

data class Forecastday(
    val astro: Astro,
    val date: String,
    val date_epoch: String,
    val day: String,
    val hour: List<Hour>
)