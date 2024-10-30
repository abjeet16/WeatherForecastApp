package com.example.weatherforecast.Activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherforecast.ViewModel.CityViewModel
import com.example.weatherforecast.adapter.CityAdapter
import com.example.weatherforecast.databinding.ActivityCityListBinding
import com.example.weatherforecast.model.CityResponseApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CityListActivity : AppCompatActivity() {
    val binding : ActivityCityListBinding by lazy {
        ActivityCityListBinding.inflate(layoutInflater)
    }
    private val cityAdapter by lazy { CityAdapter() }
    private val cityViewModel : CityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            cityEdit.addTextChangedListener(object :TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    progressBar4.visibility = View.VISIBLE
                    cityViewModel.loadCity(s.toString(),10).enqueue(object : Callback<CityResponseApi> {
                        override fun onResponse(
                            call: Call<CityResponseApi>,
                            response: Response<CityResponseApi>
                        ) {
                            if (response.isSuccessful){
                                val data = response.body()
                                data.let {
                                    progressBar4.visibility = View.GONE
                                    cityAdapter.differ.submitList(it)
                                    cityView.apply {
                                        layoutManager = LinearLayoutManager(this@CityListActivity,LinearLayoutManager.HORIZONTAL,false)
                                        adapter = cityAdapter
                                    }
                                }
                            }
                        }

                        override fun onFailure(call: Call<CityResponseApi>, t: Throwable) {

                        }

                    })
                }
            })
        }
    }
}