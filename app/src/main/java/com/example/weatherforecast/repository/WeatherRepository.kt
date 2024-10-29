package com.example.weatherforecast.repository

import com.example.weatherforecast.Server.ApiServices

class WeatherRepository(val api:ApiServices){

    fun getCurrentWeather(lat:Double,lng:Double,unit:String)=
        api.getCurrentWeather(lat,lng,unit,"3b2e7aeed0c12e98816ca5968088e475")

    fun getForecastWeather(lat:Double,lng:Double,unit:String)=
        api.getForeCastWeather(lat,lng,unit,"3b2e7aeed0c12e98816ca5968088e475")
}