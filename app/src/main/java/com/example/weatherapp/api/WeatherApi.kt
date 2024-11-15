package com.example.weatherapp.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("/v1/forecast.json")
    suspend fun getWeather( //async call
        @Query("key") apiKey: String,
        @Query("q") city: String,
        @Query("days") days: String
    ): Response<WeatherModel>

    @GET("/v1/forecast.json")
    suspend fun getWeatherByCoordinates(
        @Query("key") apiKey: String,
        @Query("q") coordinates: String,
        @Query("days") days: String
    ): Response<WeatherModel>
}