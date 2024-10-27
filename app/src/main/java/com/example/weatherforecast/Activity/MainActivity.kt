package com.example.weatherforecast.Activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherforecast.R
import com.example.weatherforecast.ViewModel.WeatherViewModel
import com.example.weatherforecast.databinding.ActivityMainBinding
import com.example.weatherforecast.model.CurrentResponseApi
import com.github.matteobattilana.weather.PrecipType
import retrofit2.Call
import retrofit2.Response
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    val binding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val calendar by lazy {
        Calendar.getInstance()
    }
    private val weatherViewModel:WeatherViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }
        binding.apply {
            var lat = 51.50
            var lon = 0.12
            var name = "London"

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
                                val currentWindSpeed= it.wind?.speed.let {
                                    //check if the wind speed from API is not null
                                    if (it != null) {
                                        // round off the wind speed
                                        Math.round(it)
                                    }
                                }.toString() + "Km"
                                val currentTemp = it.main?.temp.let {
                                    if (it != null) {
                                            Math.round(it)
                                    }
                                }.toString() +"°"
                                val maxTemp = it.main?.tempMax.let{
                                    if (it != null) {
                                            Math.round(it)
                                    }
                                }.toString()+"°"
                                val minTemp = it.main?.tempMin.let {
                                    if (it != null) {
                                        Math.round(it)
                                    }
                                }.toString()+"°"

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
            emissionRate
        }
    }
}