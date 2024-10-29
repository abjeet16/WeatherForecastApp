package com.example.weatherforecast.adapter

import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherforecast.databinding.ForecastViewholderBinding
import com.example.weatherforecast.model.ForecastResponseApi
import java.text.SimpleDateFormat

class ForecastAdapter : RecyclerView.Adapter<ForecastAdapter.viewHolder>() {

    private lateinit var binding: ForecastViewholderBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding = ForecastViewholderBinding.inflate(LayoutInflater.from(parent.context))
        return viewHolder()
    }

    inner class viewHolder : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val binding = ForecastViewholderBinding.bind(holder.itemView)
        val date = SimpleDateFormat("yyy-MM-dd HH:mm:dd").parse(differ.currentList[position].dtTxt.toString())
        val calendar = Calendar.getInstance()
        calendar.time = date

        val dayOfWeekName =
            when(calendar.get(Calendar.DAY_OF_WEEK)){
            1->"Sun"
            2->"Mon"
            3->"Tue"
            4->"Wed"
            5->"Thu"
            6->"Fri"
            7->"Sat"
            else->"-"
        }
        binding.nameDayTxt.text = dayOfWeekName
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val amPM = if (hour<12)"am" else "pm"
        val hour12 = calendar.get(Calendar.HOUR)
        binding.hourTxt.text = hour12.toString()+amPM
        binding.TempTxt.text = differ.currentList[position].main?.temp.let {
            if (it != null) {
                Math.round(it)
            }
        }.toString()+"°"

        val icon = when(differ.currentList[position].weather?.get(0)?.icon.toString()){
            "01d","01n"->"sunny"
            "02d","02n"->"cloudy_sunny"
            "03d","03n"->"cloudy_sunny"
            "04d","04n"->"cloudy"
            "09d","09n"->"rainy"
            "10d","10n"->"rainy"
            "11d","11n"->"storm"
            "13d","13n"->"snowy"
            "50d","50n"->"windy"
            else->"sunny"

        }
        val drawableResource : Int = binding.root.resources.getIdentifier(
            icon,
            "drawable",binding.root.context.packageName
        )

        Glide.with(binding.root.context)
            .load(drawableResource)
            .into(binding.pic)
    }

    private val differCallback = object : DiffUtil.ItemCallback<ForecastResponseApi.WeatherData>(){
        override fun areItemsTheSame(
            oldItem: ForecastResponseApi.WeatherData,
            newItem: ForecastResponseApi.WeatherData
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: ForecastResponseApi.WeatherData,
            newItem: ForecastResponseApi.WeatherData
        ): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this,differCallback)
}