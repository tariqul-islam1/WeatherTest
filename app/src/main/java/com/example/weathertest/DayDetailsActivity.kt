package com.example.weathertest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.example.weathertest.models.WeatherHistoryModel
import com.example.weathertest.services.WeatherService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DayDetailsActivity : AppCompatActivity() {

    lateinit var headingTV: TextView
    lateinit var minTV: TextView
    lateinit var maxTV: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_details)
        headingTV = findViewById(R.id.headingTV)
        minTV = findViewById(R.id.min)
        maxTV = findViewById(R.id.max)

        val day = intent.getStringExtra("date")
        if (day != null) {
            updateValues(day)
            headingTV.text = applicationContext.getString(R.string.date, day)
        }
    }

    private fun updateValues(day: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://api.weatherapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val call = retrofit.create(WeatherService::class.java).getHistoryWeatherData(getString(R.string.APIKey), getString(R.string.defaultCity), day)
        call.enqueue(object : Callback<WeatherHistoryModel> {
            override fun onResponse(call: Call<WeatherHistoryModel>, response: Response<WeatherHistoryModel>) {
                if (response.body() != null) {
                    val historyModel = response.body()!!
                    val day = historyModel.forecast.forecastday[0].day
                    Log.i("weatherapi", "code: " + response.code() + ", avg: " + day.avgtemp_c + ", max: " + day.maxtemp_c + ", min: " + day.mintemp_c)

                    minTV.text = day.mintemp_c.toString()
                    maxTV.text = day.maxtemp_c.toString()
                }
            }

            override fun onFailure(call: Call<WeatherHistoryModel>, t: Throwable) {
                Log.i("weatherapi", "ERROR: " + t.message)
                Toast.makeText(applicationContext, "Error " + t.message, Toast.LENGTH_SHORT).show()
            }
        })

    }
}