package com.example.weathertest

import android.os.Bundle
import android.util.Log
import android.widget.TextView
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

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showDays(findViewById(R.id.list))
    }

    private fun showDays(daysRecyclerView: RecyclerView) {
        val dates = ArrayList<String>()
        for (i in 0 .. 4) dates.add(getDaysAgo(i))

        val adapter = DaysAdapter(dates)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        daysRecyclerView.setLayoutManager(layoutManager)
        daysRecyclerView.adapter = adapter
    }

    private fun getDaysAgo(daysAgo: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        return sdf.format(calendar.time)
    }
}