package com.example.weathertest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.example.weathertest.models.WeatherHistoryModel
import com.example.weathertest.services.WeatherService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var minTV: TextView
    lateinit var maxTV: TextView
    lateinit var avgTV: TextView
    lateinit var fromToTV: TextView
    var minT = 0.0
    var maxT = 0.0
    var avgT = 0.0
    var numberOfDays = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        minTV = findViewById(R.id.min)
        maxTV = findViewById(R.id.max)
        avgTV = findViewById(R.id.avg)
        fromToTV = findViewById(R.id.fromTo)

        fromToTV.text = applicationContext.getString(R.string.from_to_text, getDaysAgo(4), getDaysAgo(0))

        updateValues()

    }

    private fun updateValues() {
        val retrofit = Retrofit.Builder()
                .baseUrl("http://api.weatherapi.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        getTempDataFromAPIAndUpdateViews(retrofit, getString(R.string.defaultCity), getDaysAgo(0))
        getTempDataFromAPIAndUpdateViews(retrofit, getString(R.string.defaultCity), getDaysAgo(1))
        getTempDataFromAPIAndUpdateViews(retrofit, getString(R.string.defaultCity), getDaysAgo(2))
        getTempDataFromAPIAndUpdateViews(retrofit, getString(R.string.defaultCity), getDaysAgo(3))
        getTempDataFromAPIAndUpdateViews(retrofit, getString(R.string.defaultCity), getDaysAgo(4))

    }

    private fun getTempDataFromAPIAndUpdateViews(retrofit: Retrofit, city: String , date: String) {
        val call = retrofit.create(WeatherService::class.java).getHistoryWeatherData(getString(R.string.APIKey), city, date)
        call.enqueue(object : Callback<WeatherHistoryModel>{
            override fun onResponse(call: Call<WeatherHistoryModel>, response: Response<WeatherHistoryModel>) {
                if (response.body() != null) {
                    val historyModel = response.body()!!
                    val day = historyModel.forecast.forecastday[0].day
                    Log.i("weatherapi", "code: " + response.code() + ", avg: " + day.avgtemp_c + ", mac: " + day.maxtemp_c + ", min: " + day.mintemp_c)

                    ++numberOfDays
                    minT += day.mintemp_c
                    maxT += day.maxtemp_c
                    avgT += day.avgtemp_c

                    updateViews()
                }
            }

            override fun onFailure(call: Call<WeatherHistoryModel>, t: Throwable) {
                Log.i("weatherapi", "ERROR: " + t.message )
            }

        })
    }

    private fun updateViews() {
        minTV.text = String.format("%.2f", (minT / numberOfDays))
        maxTV.text = String.format("%.2f", (maxT / numberOfDays))
        avgTV.text = String.format("%.2f", (avgT / numberOfDays))
    }

    private fun getDaysAgo(daysAgo: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        return sdf.format(calendar.time)
    }
}