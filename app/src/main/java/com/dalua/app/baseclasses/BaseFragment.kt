package com.dalua.app.baseclasses

import android.app.ActivityManager
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dalua.app.R
import com.dalua.app.databinding.AlertDialogLayoutBinding
import com.dalua.app.interfaces.AlertDialogInterface
import com.dalua.app.models.EasySlots
import com.dalua.app.models.schedulemodel.Slot
import com.dalua.app.utils.ColorOpacities
import com.dalua.app.utils.ProjectUtil
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.coroutines.launch
import java.util.ArrayList

open class BaseFragment : Fragment() {

    private val showToast = true

    fun setLedGradientColor(value: Int, startColor: String): GradientDrawable {
        val color1 = try {
            Color.parseColor(startColor)
        } catch (e: Exception) {
            e.printStackTrace()
            Color.parseColor("#1441ff")
        }
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(color1, Color.parseColor("#00FFFFFF"))
        )
        gradientDrawable.alpha = 255 - 255 * ColorOpacities.revertOpacity(value) / 100
        gradientDrawable.gradientType = GradientDrawable.RADIAL_GRADIENT
        gradientDrawable.gradientRadius = 25f
        gradientDrawable.shape = GradientDrawable.RECTANGLE
        return gradientDrawable
    }

    fun setSmallLedColor(color: String): GradientDrawable {
        val color1 = try {
            Color.parseColor(color)
        } catch (e: Exception) {
            e.printStackTrace()
            Color.parseColor("#1441ff")
        }
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(color1, Color.parseColor("#FFFFFF"))
        )
        gradientDrawable.alpha = 255
        gradientDrawable.gradientType = GradientDrawable.RADIAL_GRADIENT
        gradientDrawable.gradientRadius = 25f
        gradientDrawable.shape = GradientDrawable.OVAL
        return gradientDrawable
    }

    private fun setGradient1(value: Int, startColor: String, endColor: String): GradientDrawable {
        val rgbArray1 = startColor.replace("rgba(", "").replace(")", "").replace(" ", "").split(",")
        val rgbArray2 = endColor.replace("rgba(", "").replace(")", "").replace(" ", "").split(",")
        val colorStart = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Color.argb(
                rgbArray1[3].toFloat(),
                rgbArray1[0].toFloat(),
                rgbArray1[1].toFloat(),
                rgbArray1[2].toFloat()
            )
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        //rgba
        //argb
        val colorEnd = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Color.argb(
                rgbArray2[3].toFloat(),
                rgbArray2[0].toFloat(),
                rgbArray2[1].toFloat(),
                rgbArray2[2].toFloat()
            )
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(colorEnd, colorStart)
        )
        gradientDrawable.alpha = value
        gradientDrawable.gradientType = GradientDrawable.RADIAL_GRADIENT
        gradientDrawable.gradientRadius = 0f
        gradientDrawable.shape = GradientDrawable.RECTANGLE
//        return gradientDrawable
        return gradientDrawable
    }

    fun showMessage(message: String?, errorOrSuccess: Boolean) {
        if (showToast)
            ProjectUtil.showToastMessage(requireActivity(), errorOrSuccess, message)
    }

    private var progressDialog: Dialog? = null
    private var awsProgress: Dialog? = null

    fun showWorking() {
        if (progressDialog?.isShowing != true)
            progressDialog?.show()
    }

    fun showWorking(tag: String) {
        Log.d("TAG", "showWorking: TAG: $tag")
        if (progressDialog != null) {
            if (progressDialog?.isShowing!!) {
                progressDialog?.cancel()
            }
            progressDialog?.show()
        }
    }

    fun hideWorking() {
        progressDialog?.dismiss()
    }

    fun awsProgressDialog() {
        awsProgress = Dialog(requireContext())
        awsProgress?.apply {
            setContentView(R.layout.item_aws_progress_dialog)
            window!!.setBackgroundDrawableResource(R.color.transparent)
            setCancelable(false)
            window!!.setWindowAnimations(R.style.DialogAnimation)
        }


    }

    fun showAWSProgress() {

        if (awsProgress?.isShowing != true)
            awsProgress?.show()
    }

    fun hideAWSProgress() {
        awsProgress?.dismiss()
    }

    fun getDrawable(name: String): Drawable? {

        val resourceId: Int =
            resources.getIdentifier(name, "drawable", requireActivity().packageName)
        return resources.getDrawable(resourceId, requireActivity().theme)

    }

    fun myProgressDialog() {
        progressDialog = Dialog(requireContext())
        progressDialog?.apply {
            setContentView(R.layout.item_progress_dialog)
            window!!.setBackgroundDrawableResource(R.color.transparent)
            setCancelable(false)
//            window!!.setWindowAnimations(R.style.DialogAnimation)
        }
    }

    fun initializeProgressDialog() {
        progressDialog = Dialog(requireContext())
        progressDialog?.apply {
            setContentView(R.layout.item_progress_dialog)
            window!!.setBackgroundDrawableResource(R.color.transparent)
            setCancelable(false)
//            window!!.setWindowAnimations(R.style.DialogAnimation)
        }
    }

    open fun connectNotificationService(aquariumId: Int) {
        Log.d(BASETAG, "connectNotificationService: ")
        try {
            if (isMyServiceRunning()) {
                if (NotificationsService.socket != null && !NotificationsService.socket!!.connected()) {
                    NotificationsService.socket!!.connected()
                    Log.d(BASETAG, "connectNotificationService: connected: ")
                }
            } else {
                Log.d(BASETAG, "connectNotificationService: start: ")
                requireActivity().startService(
                    Intent(
                        requireContext(),
                        NotificationsService::class.java
                    ).putExtra("aquariumID", aquariumId)
                )
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    open fun stopServices() {
        Log.d(BASETAG, "stopServices: ")
        if (isMyServiceRunning()) {
            val intent = Intent(
                requireContext(),
                NotificationsService::class.java
            )
            requireActivity().stopService(intent)
        }
    }

    open fun isMyServiceRunning(): Boolean {
        try {
            val manager: ActivityManager =
                requireActivity().getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                if (NotificationsService::class.java.name == service.service.getClassName()) {
                    return true
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
        return false
    }

    fun showChart(
        context: Context,
        chart: LineChart,
        easySlots: EasySlots?,
        slots: MutableList<Slot>
    ) {

        // // Chart Style // //

        // background color
        chart.setBackgroundColor(Color.WHITE)

        // disable description text
        chart.description.isEnabled = false

        // enable touch gestures
        chart.setTouchEnabled(false)

        // set listeners
        chart.setDrawGridBackground(false)

        // enable scaling and dragging
        chart.isDragEnabled = false
        chart.setScaleEnabled(false)

        // force pinch zoom along both axis
        chart.setPinchZoom(false)

        // // X-Axis Style // //
        val xAxis: XAxis = chart.xAxis
        xAxis.axisLineColor = Color.rgb(0, 0, 255);

        xAxis.setDrawGridLines(true)
        xAxis.gridLineWidth = 0.5f
        xAxis.gridColor = context.getColor(R.color.bar_white_color)
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

        setData(180f, chart, easySlots, slots)

        // draw points over time
        chart.animateX(1000, Easing.EaseInBack);
//        chart.animateX(1500, Easing.EaseInSine)

        // get the legend (only possible after setting data)
        chart.legend.isEnabled = false

    }

    private fun setData(
        range: Float,
        chart: LineChart,
        easySlots: EasySlots?,
        slots: MutableList<Slot>
    ) {
        var count = 6
        val values = ArrayList<Entry>()
        val values2 = ArrayList<Entry>()
        val values3 = ArrayList<Entry>()

        if (easySlots != null) {

            for (i in 0 until count) {
                when (i) {
                    0, 5 -> values.add(Entry(i.toFloat(), 0f))
                    else -> values.add(Entry(i.toFloat(), easySlots.getValueA()!!.toFloat()))
                }
            }
            for (i in 0 until count) {
                when (i) {
                    0, 5 -> values2.add(Entry(i.toFloat(), 0f))
                    else -> values2.add(Entry(i.toFloat(), easySlots.getValueB()!!.toFloat()))
                }
            }
            for (i in 0 until count) {
                when (i) {
                    0, 5 -> values3.add(Entry(i.toFloat(), 0f))
                    else -> values3.add(Entry(i.toFloat(), easySlots.getValueC()!!.toFloat()))
                }
            }
        } else {
            count = slots.size
            for (i in 0 until count) {
                when (i) {
                    0, 5 -> values.add(Entry(i.toFloat(), 0f))
                    else -> values.add(Entry(i.toFloat(), slots.get(i).getValueA()!!.toFloat()))
                }
            }
            for (i in 0 until count) {
                when (i) {
                    0, 5 -> values2.add(Entry(i.toFloat(), 0f))
                    else -> values2.add(Entry(i.toFloat(), slots.get(i).getValueB()!!.toFloat()))
                }

            }
            for (i in 0 until count) {

                when (i) {
                    0, 5 -> values3.add(Entry(i.toFloat(), 0f))
                    else -> values3.add(Entry(i.toFloat(), slots.get(i).getValueC()!!.toFloat()))
                }

            }
        }


        var set2: LineDataSet? = null
        var set3: LineDataSet? = null
        var set1: LineDataSet? = null

        if (chart.data != null &&
            chart.data.dataSetCount > 0
        ) {

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
            set1.color = chart.context.getColor(R.color.purple_led_color)
            set2.color = chart.context.getColor(R.color.blue_selected_led_color)
            set3.color = chart.context.getColor(R.color.bar_white_color)

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
            set1.fillFormatter = IFillFormatter { dataSet, dataProvider ->
                chart.axisLeft.axisMinimum
            }

            // set the filled area
            set2.setDrawFilled(false)
            set2.fillFormatter = IFillFormatter { dataSet, dataProvider ->
                chart.axisLeft.axisMinimum
            }

            // set the filled area
            set3.setDrawFilled(false)
            set3.fillFormatter = IFillFormatter { dataSet, dataProvider ->
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
            chart.invalidate();
        }


    }

    fun showAlertDialogWithListener(
        context: Context,
        title: String,
        message: String,
        positiveButton: String,
        negativeButton: String,
        isCancelAble: Boolean,
        listener: AlertDialogInterface
    ) = try {

        lifecycleScope.launch {

            val dialog = Dialog(context)
            if (dialog.isShowing) dialog.cancel()
            val binding = AlertDialogLayoutBinding.inflate(LayoutInflater.from(context))
            dialog.setContentView(binding.root)
            dialog.setCanceledOnTouchOutside(isCancelAble)
            dialog.setCancelable(isCancelAble)
            dialog.window?.setBackgroundDrawableResource(R.color.transparent)
//            dialog.window?.setWindowAnimations(R.style.DialogAnimation)
            binding.textTitle.text = title
            binding.textMessage.text = message
            if (positiveButton != "") {
                binding.positiveButton.visibility = View.VISIBLE
                binding.positiveButton.text = positiveButton
                binding.positiveButton.setOnClickListener {
                    listener.onPositiveClickListener(dialog)
                }
            } else {
                binding.positiveButton.visibility = View.GONE
            }
            if (negativeButton != "") {
                binding.negativeButton.visibility = View.VISIBLE
                binding.negativeButton.text = negativeButton
                binding.negativeButton.setOnClickListener {
                    listener.onNegativeClickListener(dialog)
                }
            } else {
                binding.negativeButton.visibility = View.GONE
            }
            dialog.show()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    companion object {
        const val BASETAG = "BaseFragment"
    }

}