package com.dalua.app.utils

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.dalua.app.R
import com.dalua.app.models.GeoLocationResponse
import com.dalua.app.models.schedulemodel.SingleSchedule
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.skyfishjy.library.RippleBackground
import com.squareup.picasso.Picasso
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


@BindingAdapter("setDateOfTextView")
fun dateTextView(view: TextView, date: String?) {

    if (date != null) {
        val givenDateFormat: DateFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        val myDate: Date = givenDateFormat.parse(date)!!

        val dateFormat = SimpleDateFormat("dd/mm/yy", Locale.US)
        val timeFormat: DateFormat = SimpleDateFormat("hh:mm a", Locale.US)
        view.text = buildString {
            append(dateFormat.format(myDate))
            append(" at ")
            append(timeFormat.format(myDate))
        }
    } else
        view.text = buildString {
            append("21/12/2021")
        }

}

@BindingAdapter("deviceCompletedText", "deviceStatusText", "deviceIpAddress", "deviceVersion")
fun setDeviceStatusText(
    textView: TextView,
    completed: Int?,
    status: Int?,
    ipAddress: String?,
    version: Int?
) {
    if (completed == 0) {
        textView.text = buildString {
            append("Tap to complete")
        }
    } else {
        if (status == 0 || status == 2) {
            textView.text = buildString {
                append("Not-Connected")
            }
        } else {
            if (version == null) {
                textView.text = buildString {
                    append("Tap to update")
                }
            } else {
                textView.text = buildString {
                    append("Connected")
                }
            }
        }
    }
}

@BindingAdapter("deviceCompletedLED", "deviceStatusLED", "deviceIpAddress", "deviceVersion")
fun setDeviceStatusLED(
    imageView: ImageView,
    completed: Int?,
    status: Int?,
    ipAddress: String?,
    version: Int?
) {
    if (completed == 0) {
        imageView.setImageResource(R.drawable.ic_yellow_led)
    } else {
        if (status == 0 || status == 2) {
            imageView.setImageResource(R.drawable.ic_red_led)
        } else {
            if (version == null) {
                imageView.setImageResource(R.drawable.ic_yellow_led)
            } else {
                imageView.setImageResource(R.drawable.ic_green_led)
            }
        }
    }
}

@BindingAdapter("deviceAquariumType", "deviceIpAddress")
fun setDeviceMenuIcon(imageView: ImageView, aquariumType: Int?, ipAddress: String?) {
    if (aquariumType == 2) {
        imageView.visibility = View.GONE
    } else {
        imageView.visibility = View.VISIBLE
    }
}

@BindingAdapter("startRippleAnimation")
fun startRippleAnimation(rippleBackground: RippleBackground, isStart: Boolean) {
    if (isStart) {
        rippleBackground.startRippleAnimation()
    } else {
        rippleBackground.stopRippleAnimation()
    }
}


@BindingAdapter("setWeatherTemperature")
fun setTemperature(view: TextView, date: Double?) {

    if (date != null) {
        val x = date - 273.15
        view.text = String.format(Locale.US, "%.2f", x) + "\u2103"
    } else {
        view.text = "0" + "\u2103"
    }

}

@BindingAdapter("setWeatherImage")
fun setWeatherImage(view: ImageView, id: Int?) {

    val requestBuilder: RequestBuilder<Drawable> =
        Glide.with(view.context).asDrawable().sizeMultiplier(0.05f)
    Log.d("TAG", "setWeatherImage: $id")
    var icon = 0
    when (id) {
        in 200..299 -> {
            icon = R.drawable.weather_thunder_storm
        }
        in 500..599 -> {
            icon = R.drawable.weather_rain
        }
        800 -> {
            icon = R.drawable.weather_sunny
        }
        801, 802 -> {
            icon = R.drawable.weather_partly_cloudy
        }
        803, 804 -> {
            icon = R.drawable.weather_cloudy
        }
        else -> {
            icon = R.drawable.app_place_holder
        }
    }
    Glide
        .with(view.context)
        .load(icon)
        .centerCrop()
        .thumbnail(requestBuilder)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(view)

}

@BindingAdapter("setDeviceImage")
fun setDeviceImage(view: ImageView, image: String?) {
    Picasso.get()
        .load(image)
        .placeholder(R.drawable.app_place_holder)
        .error(R.drawable.app_place_holder)
        .into(view)
}

@BindingAdapter("setUserImage")
fun setUserImage(view: ImageView, image: String?) {
    Log.d("TAG", "setUserImage: $image")
//    val requestBuilder: RequestBuilder<Drawable> =
//        Glide.with(view.context).asDrawable().sizeMultiplier(0.05f)
//    if (image != null) {
//        Glide
//            .with(view.context)
//            .load(image)
//            .thumbnail(requestBuilder)
//            .transition(DrawableTransitionOptions.withCrossFade())
//            .placeholder(R.drawable.ic_user)
//            .into(view)
//    } else {
//        Glide
//            .with(view.context)
//            .load("image")
//            .thumbnail(requestBuilder)
//            .transition(DrawableTransitionOptions.withCrossFade())
//            .placeholder(R.drawable.ic_user)
//            .into(view)
//    }
    Picasso.get()
        .load(image)
        .placeholder(R.drawable.ic_user)
        .error(R.drawable.ic_user)
        .into(view)
}

@BindingAdapter("setImage")
fun setImage(view: ImageView, image: String?) {
    Log.d("TAG", "setUserImage: $image")
//    val requestBuilder: RequestBuilder<Drawable> =
//        Glide.with(view.context).asDrawable().sizeMultiplier(0.05f)
//    Glide
//        .with(view.context)
//        .load(image)
//        .thumbnail(requestBuilder)
//        .transition(DrawableTransitionOptions.withCrossFade())
//        .placeholder(R.drawable.ic_profile_user)
//        .into(view)
//
    Picasso.get()
        .load(image)
        .placeholder(R.drawable.ic_user)
        .error(R.drawable.ic_user)
        .into(view)
}

@BindingAdapter("app:setGlideImage", "app:setGlideError", "app:setGlidePlaceHolder")
fun setGlideImage(
    view: ImageView,
    setGlideImage: String?,
    setGlideError: Drawable,
    setGlidePlaceHolder: Drawable
) {
//    val requestBuilder: RequestBuilder<Drawable> =
//        Glide.with(view.context).asDrawable().sizeMultiplier(0.05f)
//    if (setGlideImage != null) {
//        Glide
//            .with(view.context)
//            .load(setGlideImage)
//            .thumbnail(requestBuilder)
//            .transition(DrawableTransitionOptions.withCrossFade())
//            .centerCrop()
//            .placeholder(setGlidePlaceHolder)
//            .error(setGlideError)
//            .into(view)
//    } else {
//        Glide
//            .with(view.context)
//            .load("image")
//            .thumbnail(requestBuilder)
//            .transition(DrawableTransitionOptions.withCrossFade())
//            .centerCrop()
//            .placeholder(setGlidePlaceHolder)
//            .error(setGlideError)
//            .into(view)
//    }

    Picasso.get()
        .load(setGlideImage)
        .placeholder(setGlidePlaceHolder)
        .error(setGlideError)
        .into(view)
}

@BindingAdapter("setResourceImage")
fun setResourceImage(view: ImageView, image: Int?) {
    Log.d("TAG", "setResourceImage: $image")
    view.setImageResource(image!!)
}

@BindingAdapter("setTextViewWithInteger")
fun setTextViewWithInteger(textView: TextView, int: Int) {
    textView.text = int.toString()
}

@BindingAdapter(
    value = ["setLocationTextView", "setLocationIDTextView", "setLocationEnabledTextView"],
    requireAll = false
)
fun setLocationTextview(
    view: TextView,
    location: GeoLocationResponse?,
    location_id: String?,
    location_Enabled: String?
) {
    var isSet = false
    if (location_id != null) {
        for (datum in location!!.data) {
            if (datum.id.toString().contentEquals(location_id)) {
                view.text = datum.name
                view.visibility = View.VISIBLE
                isSet = true
            }
        }
    }
    if (!isSet || location_Enabled.contentEquals("0"))
        view.visibility = View.GONE


}

@BindingAdapter("setLocationName")
fun setLocationName(view: TextView, schedule: SingleSchedule?) {
    Log.d("TAG", "setLocationName: " + schedule!!.geoLocation)
    if (schedule!!.location != null && schedule.geoLocation == 1) {
        view.visibility = View.VISIBLE
        view.text = schedule.location.name
        Log.d("TAG", "setLocationName: " + schedule.location.name)
    } else {
        view.visibility = View.INVISIBLE
    }
}

@BindingAdapter("setGraph")
fun setGraph(chart: LineChart, singleSchedule: SingleSchedule?) {
    chart.setBackgroundColor(Color.WHITE)
    chart.description.isEnabled = false
    chart.setTouchEnabled(false)
    chart.setDrawGridBackground(false)
    chart.isDragEnabled = false
    chart.setScaleEnabled(false)
    chart.setPinchZoom(false)
    val xAxis: XAxis = chart.xAxis
    xAxis.axisLineColor = Color.rgb(0, 0, 255)
    xAxis.setDrawGridLines(true)
    xAxis.gridLineWidth = 0.5f
    xAxis.gridColor = chart.context.getColor(R.color.bar_white_color)
    xAxis.position = XAxis.XAxisPosition.BOTTOM
    xAxis.setLabelCount(6, true)
    xAxis.setDrawLabels(false)
    xAxis.axisLineWidth = 0.1f

    // // Y-Axis Style // //
    val yAxis: YAxis = chart.axisLeft
    yAxis.gridLineWidth = 0.5f

    // disable dual axis (only use LEFT axis)
    chart.axisRight.isEnabled = false
    chart.axisLeft.isEnabled = false

    // axis range
    yAxis.axisMaximum = 200f
    yAxis.axisMinimum = 0f

    // Create Limit Lines //
    // draw limit lines behind data instead of on top
    yAxis.setDrawLimitLinesBehindData(true)
    xAxis.setDrawLimitLinesBehindData(true)

    var count = 6
    val values = ArrayList<Entry>()
    val values2 = ArrayList<Entry>()
    val values3 = ArrayList<Entry>()

    if (singleSchedule!!.easySlots != null) {

        for (i in 0 until count) {
            when (i) {
                0, 5 -> values.add(Entry(i.toFloat(), 0f))
                else -> values.add(Entry(i.toFloat(), singleSchedule.easySlots.valueA!!.toFloat()))
            }
        }
        for (i in 0 until count) {
            when (i) {
                0, 5 -> values2.add(Entry(i.toFloat(), 0f))
                else -> values2.add(Entry(i.toFloat(), singleSchedule.easySlots.valueB!!.toFloat()))
            }
        }
        for (i in 0 until count) {
            when (i) {
                0, 5 -> values3.add(Entry(i.toFloat(), 0f))
                else -> values3.add(Entry(i.toFloat(), singleSchedule.easySlots.valueC!!.toFloat()))
            }
        }
    } else {
        count = singleSchedule.slots.size
        for (i in 0 until count) {
            when (i) {
                0, 5 -> values.add(Entry(i.toFloat(), 0f))
                else -> values.add(Entry(i.toFloat(), singleSchedule.slots[i].valueA!!.toFloat()))
            }
        }
        for (i in 0 until count) {
            when (i) {
                0, 5 -> values2.add(Entry(i.toFloat(), 0f))
                else -> values2.add(Entry(i.toFloat(), singleSchedule.slots[i].valueB!!.toFloat()))
            }

        }
        for (i in 0 until count) {

            when (i) {
                0, 5 -> values3.add(Entry(i.toFloat(), 0f))
                else -> values3.add(Entry(i.toFloat(), singleSchedule.slots[i].valueC!!.toFloat()))
            }

        }
    }


    val set2: LineDataSet?
    val set3: LineDataSet?
    val set1: LineDataSet?

    if (chart.data != null && chart.data.dataSetCount > 0) {

        set1 = chart.data.getDataSetByIndex(0) as LineDataSet
        set1.values = values
        set1.notifyDataSetChanged()
        set2 = chart.data.getDataSetByIndex(1) as LineDataSet
        set2.values = values2
        set2.notifyDataSetChanged()
        set3 = chart.data.getDataSetByIndex(2) as LineDataSet
        set3.values = values3
        set3.notifyDataSetChanged()
        chart.data.notifyDataChanged()
        chart.notifyDataSetChanged()

    } else {
        // create a dataset and give it a type
        set1 = LineDataSet(values, "")
        set1.setDrawIcons(false)
        set2 = LineDataSet(values2, "")
        set2.setDrawIcons(false)
        set3 = LineDataSet(values3, "")
        set3.setDrawIcons(false)

        // black lines and points

        if (singleSchedule!!.waterType.lowercase(Locale.getDefault()) == "marine") {
            set1.color = chart.context.getColor(R.color.purple_led_color)
            set2.color = chart.context.getColor(R.color.blue_selected_led_color)
            set3.color = chart.context.getColor(R.color.bar_white_color)
        } else {
            set1.color = chart.context.getColor(R.color.red_led_color)
            set2.color = chart.context.getColor(R.color.green_led_color)
            set3.color = chart.context.getColor(R.color.yellow_led_color)
        }

        // line thickness and point size
        set1.lineWidth = 2f
        set2.lineWidth = 2f
        set3.lineWidth = 2f

        // draw points as solid circles
        set1.setDrawCircleHole(false)
        set1.setDrawCircles(false)
        set2.setDrawCircleHole(false)
        set2.setDrawCircles(false)
        set3.setDrawCircleHole(false)
        set3.setDrawCircles(false)

        // text size of values
        set1.valueTextSize = 9f
        set2.valueTextSize = 9f
        set3.valueTextSize = 9f

        // set the filled area
        set1.setDrawFilled(false)
        set1.fillFormatter = IFillFormatter { _, _ ->
            chart.axisLeft.axisMinimum
        }

        // set the filled area
        set2.setDrawFilled(false)
        set2.fillFormatter = IFillFormatter { _, _ ->
            chart.axisLeft.axisMinimum
        }

        // set the filled area
        set3.setDrawFilled(false)
        set3.fillFormatter = IFillFormatter { _, _ ->
            chart.axisLeft.axisMinimum
        }

        val dataSets = ArrayList<ILineDataSet>()
        dataSets.add(set1) // add the data sets
        dataSets.add(set2) // add the data sets
        dataSets.add(set3) // add the data sets

        // create a data object with the data sets
        val data = LineData(dataSets)
        data.setDrawValues(false)
        // set data
        chart.data = data
        chart.invalidate()
    }


    // draw points over time
    chart.animateX(500, Easing.EaseInBounce)
//        chart.animateX(1500, Easing.EaseInSine)

    // get the legend (only possible after setting data)
    chart.legend.isEnabled = false

}

