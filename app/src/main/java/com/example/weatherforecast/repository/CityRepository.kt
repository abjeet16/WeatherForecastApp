package com.example.weatherforecast.repository

import com.example.weatherforecast.Server.ApiServices

class CityRepository(val api:ApiServices) {
    fun getCities(q:String,limit:Int)=
        api.getCitiesList(q,limit,"3b2e7aeed0c12e98816ca5968088e475")
}