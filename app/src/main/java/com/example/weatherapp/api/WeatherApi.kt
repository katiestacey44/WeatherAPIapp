package com.example.weatherapp.api

import com.google.android.gms.common.api.internal.ApiKey
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

   @GET("/v1/current.json")
   suspend fun getWeather(
       @Query("key") apiKey: String,
       @Query("q") city :String,
       @Query("days") days : String
   ): Response<WeatherModel>
}
