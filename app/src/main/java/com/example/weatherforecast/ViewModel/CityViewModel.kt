package com.example.weatherforecast.ViewModel

import androidx.lifecycle.ViewModel
import com.example.weatherforecast.Server.ApiClient
import com.example.weatherforecast.Server.ApiServices
import com.example.weatherforecast.repository.CityRepository

class CityViewModel(val repository: CityRepository):ViewModel() {
    constructor():this(CityRepository(ApiClient().getClient().create(ApiServices::class.java)))

    fun loadCity(q:String,limit:Int)=
        repository.getCities(q,limit)
}