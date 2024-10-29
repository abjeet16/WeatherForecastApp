package com.example.weatherforecast.ViewModel

import androidx.lifecycle.ViewModel
import com.example.weatherforecast.Server.ApiClient
import com.example.weatherforecast.Server.ApiServices
import com.example.weatherforecast.repository.WeatherRepository
import java.util.concurrent.TimeUnit

class WeatherViewModel(val repository: WeatherRepository):ViewModel() {

    constructor():this(WeatherRepository(ApiClient().getClient().create(ApiServices::class.java)))

    fun loadCurrentWeather(lat :Double,lng:Double,unit:String) =
        repository.getCurrentWeather(lat, lng, unit)

    fun loadForecastWeather(lat :Double,lng:Double,unit:String) =
        repository.getForecastWeather(lat, lng, unit)
}