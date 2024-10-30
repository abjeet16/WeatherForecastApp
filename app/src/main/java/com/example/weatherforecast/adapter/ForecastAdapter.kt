package com.example.weatherforecast.adapter

import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherforecast.databinding.ForecastViewholderBinding
import com.example.weatherforecast.model.ForecastResponseApi
import java.text.SimpleDateFormat
import java.util.*

class ForecastAdapter : RecyclerView.Adapter<ForecastAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ForecastViewholderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ForecastResponseApi.WeatherData) {
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(data.dtTxt)
            val calendar = Calendar.getInstance()
            calendar.time = date

            val dayOfWeekName = when (calendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SUNDAY -> "Sun"
                Calendar.MONDAY -> "Mon"
                Calendar.TUESDAY -> "Tue"
                Calendar.WEDNESDAY -> "Wed"
                Calendar.THURSDAY -> "Thu"
                Calendar.FRIDAY -> "Fri"
                Calendar.SATURDAY -> "Sat"
                else -> "-"
            }
            binding.nameDayTxt.text = dayOfWeekName

            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val amPM = if (hour < 12) "AM" else "PM"
            val hour12 = if (calendar.get(Calendar.HOUR) == 0) 12 else calendar.get(Calendar.HOUR)
            binding.hourTxt.text = "$hour12 $amPM"

            val tempText = data.main?.temp?.let { Math.round(it).toString() + "°" } ?: "-"
            binding.TempTxt.text = tempText

            val icon = when (data.weather?.get(0)?.icon) {
                "01d", "01n" -> "sunny"
                "02d", "02n" -> "cloudy_sunny"
                "03d", "03n" -> "cloudy_sunny"
                "04d", "04n" -> "cloudy"
                "09d", "09n" -> "rainy"
                "10d", "10n" -> "rainy"
                "11d", "11n" -> "storm"
                "13d", "13n" -> "snowy"
                "50d", "50n" -> "windy"
                else -> "sunny"
            }
            val drawableResource = binding.root.resources.getIdentifier(
                icon, "drawable", binding.root.context.packageName
            )

            Glide.with(binding.root.context)
                .load(drawableResource)
                .into(binding.pic)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ForecastViewholderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = differ.currentList.size

    private val differCallback = object : DiffUtil.ItemCallback<ForecastResponseApi.WeatherData>() {
        override fun areItemsTheSame(oldItem: ForecastResponseApi.WeatherData, newItem: ForecastResponseApi.WeatherData): Boolean {
            return oldItem.dtTxt == newItem.dtTxt
        }

        override fun areContentsTheSame(oldItem: ForecastResponseApi.WeatherData, newItem: ForecastResponseApi.WeatherData): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}
