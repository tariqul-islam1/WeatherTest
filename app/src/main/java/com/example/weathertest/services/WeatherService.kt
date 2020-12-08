package com.example.weathertest.services

import com.example.weathertest.models.WeatherHistoryModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("history.json?")
    fun getHistoryWeatherData(@Query("key") key: String, @Query("q") q: String, @Query("dt") dt: String): Call<WeatherHistoryModel>
}