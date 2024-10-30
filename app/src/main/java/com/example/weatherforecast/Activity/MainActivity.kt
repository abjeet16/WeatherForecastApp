package com.example.weatherforecast.Activity

import android.content.Intent
import android.graphics.Color
import android.graphics.RenderNode
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.weatherforecast.R
import com.example.weatherforecast.ViewModel.WeatherViewModel
import com.example.weatherforecast.adapter.ForecastAdapter
import com.example.weatherforecast.databinding.ActivityMainBinding
import com.example.weatherforecast.model.CurrentResponseApi
import com.example.weatherforecast.model.ForecastResponseApi
import com.github.matteobattilana.weather.PrecipType
import eightbitlab.com.blurview.RenderScriptBlur
import retrofit2.Call
import retrofit2.Response
import java.util.Calendar
import kotlin.math.log

class MainActivity : AppCompatActivity() {
    val binding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val calendar by lazy {
        Calendar.getInstance()
    }
    private val forecastAdapter by lazy { ForecastAdapter() }
    private val weatherViewModel:WeatherViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Hide status bar
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.hide()

        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }
        binding.apply {

            addCity.setOnClickListener {
                startActivity(Intent(this@MainActivity,CityListActivity::class.java))
            }
            var lat = intent.getDoubleExtra("lat",0.0)
            var lon = intent.getDoubleExtra("lon",0.0)
            var name = intent.getStringExtra("cityName")

            if (lat == 0.0){
                name = "Null Island"
            }

            //CURRENT TEMP
            cityTxt.text = name
            progressBar.visibility = View.VISIBLE
            weatherViewModel.loadCurrentWeather(lat,lon,"metric")
                .enqueue(object : retrofit2.Callback<CurrentResponseApi> {
                    override fun onResponse(
                        call: Call<CurrentResponseApi>,
                        response: Response<CurrentResponseApi>
                    ) {
                        if (response.isSuccessful) {
                            val data = response.body()
                            progressBar.visibility = View.GONE
                            detailsLayout.visibility = View.VISIBLE
                            data?.let {
                                statusTxt.text = it.weather?.get(0)?.main ?: "-"
                                val humidity = it.main?.humidity.toString()+"%"
                                val currentWindSpeed = it.wind?.speed?.let { speed ->
                                    Math.round(speed).toString() + "Km"
                                } ?: "- Km" // Fallback if speed is null

                                val currentTemp = it.main?.temp?.let { temp ->
                                    Math.round(temp).toString() + "°"
                                } ?: "-°" // Fallback if temp is null

                                val maxTemp = it.main?.tempMax?.let { tempMax ->
                                    Math.round(tempMax).toString() + "°"
                                } ?: "-°" // Fallback if tempMax is null

                                val minTemp = it.main?.tempMin?.let { tempMin ->
                                    Math.round(tempMin).toString() + "°"
                                } ?: "-°" // Fallback if tempMin is null


                                humidityTxt.text = humidity
                                currentTempTxt.text = currentTemp
                                windTxt.text = currentWindSpeed
                                maxTempTxt.text = maxTemp
                                minTempTxt.text = minTemp

                                val drawable = if (isNightTime()) {
                                    R.drawable.night_bg
                                }else{
                                    setDynamicallyWallPaper(it.weather?.get(0)?.icon?:"-")
                                }
                                bgImage.setImageResource(drawable)
                                setEffectRainSnow(it.weather?.get(0)?.icon?:"-")
                            }
                        }
                    }

                    override fun onFailure(call: Call<CurrentResponseApi>, t: Throwable) {
                        Toast.makeText(this@MainActivity, t.toString(), Toast.LENGTH_SHORT).show()
                    }
                })

            //setting Blur View
            val radius = 10f;
            val decorView = window.decorView
            val rootView = (decorView.findViewById(android.R.id.content) as ViewGroup?)
            val windowBackground = decorView.background

            rootView?.let {
                blurView.setupWith(it,RenderScriptBlur(this@MainActivity))
                    .setFrameClearDrawable(windowBackground)
                    .setBlurRadius(radius)
                blurView.outlineProvider = ViewOutlineProvider.BACKGROUND
                blurView.clipToOutline = true
            }

            //foreCast Temp
            weatherViewModel.loadForecastWeather(lat,lon,"metric").enqueue(object : retrofit2.Callback<ForecastResponseApi>{
                override fun onResponse(
                    call: Call<ForecastResponseApi>,
                    response: Response<ForecastResponseApi>
                ) {
                    if (response.isSuccessful){
                        val data = response.body()
                        blurView.visibility = View.VISIBLE
                        data?.let {
                            forecastAdapter.differ.submitList(it.list)
                            forecaseRecyclerView.apply {
                                layoutManager = LinearLayoutManager(this@MainActivity,LinearLayoutManager.HORIZONTAL,false)
                                Log.d("bdhfcbafdcuh", it.list.toString())
                                adapter = forecastAdapter
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ForecastResponseApi>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
        }
    }
    private fun isNightTime():Boolean{
        // returns true if the time is above 6pm
        return calendar.get(Calendar.HOUR_OF_DAY) >= 18
    }
    private fun setDynamicallyWallPaper(icon:String):Int{
        return when(icon.dropLast(1)){
            "01"->{
                initWeatherView(PrecipType.CLEAR)
                R.drawable.snow_bg
            }
            "02","03","04"->{
                initWeatherView(PrecipType.CLEAR)
                R.drawable.cloudy_bg
            }
            "09","10","11"->{
                initWeatherView(PrecipType.RAIN)
                R.drawable.rainy_bg
            }
            "13"->{
                initWeatherView(PrecipType.SNOW)
                R.drawable.snow_bg
            }
            "50"->{
                initWeatherView(PrecipType.CLEAR)
                R.drawable.haze_bg
            }
            else -> 0
        }
    }

    private fun setEffectRainSnow(icon:String){
        when(icon.dropLast(1)){
            "01"->{
                initWeatherView(PrecipType.CLEAR)
            }
            "02","03","04"->{
                initWeatherView(PrecipType.CLEAR)

            }
            "09","10","11"->{
                initWeatherView(PrecipType.RAIN)

            }
            "13"->{
                initWeatherView(PrecipType.SNOW)

            }
            "50"->{
                initWeatherView(PrecipType.CLEAR)

            }
        }
    }
    private fun initWeatherView(type: PrecipType){
        binding.weatherView.apply{
            setWeatherData(type)
            angle =- 20
            emissionRate = 100.0f
        }
    }
}