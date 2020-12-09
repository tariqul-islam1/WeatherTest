package com.example.weathertest

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weathertest.models.WeatherHistoryModel
import com.example.weathertest.services.WeatherService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.ceil
import kotlin.math.floor

class MainActivity : AppCompatActivity() {

    lateinit var minTV: TextView
    lateinit var maxTV: TextView
    private lateinit var avgTV: TextView
    lateinit var medTV: TextView
    lateinit var fromToTV: TextView
    var minT = 99.0
    var maxT = -99.0
    var avgT = 0.0
    var numberOfDays = 0.0
    var dailyTemps = DoubleArray(5)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        minTV = findViewById(R.id.min)
        maxTV = findViewById(R.id.max)
        avgTV = findViewById(R.id.avg)
        medTV = findViewById(R.id.med)
        fromToTV = findViewById(R.id.fromTo)

        fromToTV.text = applicationContext.getString(R.string.from_to_text, getDaysAgo(4), getDaysAgo(0))

        updateValues()
        showDays(findViewById(R.id.list))
    }

    /**
     * Manually call API for each individual days because the API doesn't provide range of history data
     */
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

    /**
     * Here we call the API to get weather data
     * Example GET call: http://api.weatherapi.com/v1/history.json?key=558be982c9fc437aa84154822200412&q=Gothenburg&dt=2020-12-08
     * After getting the data as JSON object it calls updateViews() to update the TextViews
     * @param retrofit Retrofit A retrofit object
     * @param city String Name of a city, e.g. Gothenburg
     * @param date String Specific day of format yyyy-MM-dd
     */
    private fun getTempDataFromAPIAndUpdateViews(retrofit: Retrofit, city: String , date: String) {
        val call = retrofit.create(WeatherService::class.java).getHistoryWeatherData(getString(R.string.APIKey), city, date)
        call.enqueue(object : Callback<WeatherHistoryModel>{
            override fun onResponse(call: Call<WeatherHistoryModel>, response: Response<WeatherHistoryModel>) {
                if (response.body() != null) {
                    val historyModel = response.body()!!
                    val day = historyModel.forecast.forecastday[0].day
                    //Log.i("weatherapi", "code: " + response.code() + ", avg: " + day.avgtemp_c + ", max: " + day.maxtemp_c + ", min: " + day.mintemp_c)
                    ++numberOfDays
                    if (minT > day.mintemp_c) minT = day.mintemp_c
                    if (maxT < day.maxtemp_c) maxT = day.maxtemp_c
                    avgT += day.avgtemp_c
                    dailyTemps[numberOfDays.toInt() - 1] = day.avgtemp_c
                    updateViews()
                }
            }

            override fun onFailure(call: Call<WeatherHistoryModel>, t: Throwable) {
                Log.i("weatherapi", "ERROR: " + t.message )
                Toast.makeText(applicationContext, "Error " + t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun showDays(daysRecyclerView: RecyclerView) {
        val dates = ArrayList<String>()
        for (i in 0 .. 4) dates.add(getDaysAgo(i))
        val adapter = DaysAdapter(dates)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        daysRecyclerView.setLayoutManager(layoutManager)
        daysRecyclerView.adapter = adapter
    }

    private fun updateViews() {
        minTV.text = String.format("%.2f", minT)
        maxTV.text = String.format("%.2f", maxT)
        avgTV.text = String.format("%.2f", (avgT / numberOfDays))
        Arrays.sort(dailyTemps)
        medTV.text = String.format("%.2f", (dailyTemps[ceil(dailyTemps.size.toDouble() / 2).toInt()]))
    }

    private fun getDaysAgo(daysAgo: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        return sdf.format(calendar.time)
    }
}