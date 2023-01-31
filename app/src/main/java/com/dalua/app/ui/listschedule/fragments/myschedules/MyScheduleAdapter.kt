package com.dalua.app.ui.listschedule.fragments.myschedules

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dalua.app.R
import com.dalua.app.databinding.ItemMySchedulesBinding
import com.dalua.app.databinding.ItemProgressBinding
import com.dalua.app.models.EasySlots
import com.dalua.app.models.schedulemodel.SingleSchedule
import com.dalua.app.models.schedulemodel.Slot
import com.dalua.app.utils.AppConstants
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import java.util.*

class MyScheduleAdapter(
    val viewModel: MyScheduleVM,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var isLoadingAdded = false
    private var daluaList: MutableList<SingleSchedule> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        when (viewType) {
            ITEM -> {
                val binding: ItemMySchedulesBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_my_schedules,
                    parent,
                    false
                )
                viewHolder = MyViewHolder(binding)
            }
            LOADING -> {
                val binding: ItemProgressBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_progress,
                    parent,
                    false
                )
                viewHolder = LoadingViewHolder(binding)
            }
        }
        return viewHolder!!

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (getItemViewType(position)) {
            ITEM -> {
                itemBinding(holder as MyViewHolder, position)
            }
            LOADING -> {
                val loadingViewHolder = holder as LoadingViewHolder
                loadingViewHolder.binding.loadMoreProgress.visibility = View.VISIBLE
            }
        }
    }

    private fun itemBinding(holder: MyViewHolder, position: Int) {

        holder.binding.item = daluaList[position]
        holder.binding.viewModel = viewModel

        if (AppConstants.ISGroupOrDevice.contentEquals("D")) {
            holder.binding.isEnabled =
                AppConstants.DeviceOrGroupID == daluaList[position].deviceId && daluaList[position].enabled!!.contentEquals(
                    "1"
                )
            if (holder.binding.isEnabled == true)
                AppConstants.ScheDuleID = daluaList[position].id
        } else {
            holder.binding.isEnabled =
                AppConstants.DeviceOrGroupID == daluaList[position].groupId && daluaList[position].enabled!!.contentEquals(
                    "1"
                )
            if (holder.binding.isEnabled == true)
                AppConstants.ScheDuleID = daluaList[position].id
        }

        if (daluaList[position].mode!!.contentEquals("1")) {
            holder.binding.textviewAdvance.apply {
                setBackgroundResource(R.drawable.btn_gradient_background)
                setTextColor(
                    holder.itemView.context.resources.getColor(
                        R.color.blue_color_adv,
                        holder.itemView.context.theme
                    )
                )
                text = holder.itemView.context.getString(R.string.easy)
            }
        } else {
            holder.binding.textviewAdvance.apply {
                setBackgroundResource(R.drawable.btn_gradient_background)
                setTextColor(
                    holder.itemView.context.resources.getColor(
                        R.color.blue_color_adv,
                        holder.itemView.context.theme
                    )
                )
                text = holder.itemView.context.getString(R.string.advance)
            }
        }

        initChart(
            holder.itemView.context,
            holder.binding.chart1,
            daluaList[position].easySlots,
            daluaList[position].slots,
            daluaList[position].waterType
        )
    }

    override fun getItemCount(): Int {
        return daluaList.size
    }

    private fun initChart(
        context: Context,
        chart: LineChart,
        easySlots: EasySlots?,
        slots: MutableList<Slot>,
        waterType: String
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
        xAxis.axisLineColor = Color.rgb(0, 0, 255)

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

        setData(180f, chart, easySlots, slots, waterType)

        // draw points over time
        chart.animateX(500, Easing.EaseInBounce)
//        chart.animateX(1500, Easing.EaseInSine)

        // get the legend (only possible after setting data)
        chart.legend.isEnabled = false

    }

    private fun setData(
        range: Float,
        chart: LineChart,
        easySlots: EasySlots?,
        slots: MutableList<Slot>,
        waterType: String
    ) {
        var count = 6
        val values = ArrayList<Entry>()
        val values2 = ArrayList<Entry>()
        val values3 = ArrayList<Entry>()

        if (easySlots != null) {

            for (i in 0 until count) {
                when (i) {
                    0, 5 -> values.add(Entry(i.toFloat(), 0f))
                    else -> values.add(Entry(i.toFloat(), easySlots.valueA!!.toFloat()))
                }
            }
            for (i in 0 until count) {
                when (i) {
                    0, 5 -> values2.add(Entry(i.toFloat(), 0f))
                    else -> values2.add(Entry(i.toFloat(), easySlots.valueB!!.toFloat()))
                }
            }
            for (i in 0 until count) {
                when (i) {
                    0, 5 -> values3.add(Entry(i.toFloat(), 0f))
                    else -> values3.add(Entry(i.toFloat(), easySlots.valueC!!.toFloat()))
                }
            }
        } else {
            count = slots.size
            for (i in 0 until count) {
                when (i) {
                    0, 5 -> values.add(Entry(i.toFloat(), 0f))
                    else -> values.add(Entry(i.toFloat(), slots[i].valueA!!.toFloat()))
                }
            }
            for (i in 0 until count) {
                when (i) {
                    0, 5 -> values2.add(Entry(i.toFloat(), 0f))
                    else -> values2.add(Entry(i.toFloat(), slots[i].valueB!!.toFloat()))
                }

            }
            for (i in 0 until count) {

                when (i) {
                    0, 5 -> values3.add(Entry(i.toFloat(), 0f))
                    else -> values3.add(Entry(i.toFloat(), slots[i].valueC!!.toFloat()))
                }

            }
        }


        val set2: LineDataSet?
        val set3: LineDataSet?
        val set1: LineDataSet?

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
            setGraphLineColors(chart, set1, set2, set3, waterType)

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


    }

    private fun setGraphLineColors(
        chart: LineChart,
        set1: LineDataSet,
        set2: LineDataSet,
        set3: LineDataSet,
        waterType: String
    ) {
        if (waterType.lowercase(Locale.getDefault()) == "marine") {
            set1.color = chart.context.getColor(R.color.purple_led_color)
            set2.color = chart.context.getColor(R.color.blue_selected_led_color)
            set3.color = chart.context.getColor(R.color.bar_white_color)
        } else {
            set1.color = chart.context.getColor(R.color.red_led_color)
            set2.color = chart.context.getColor(R.color.green_led_color)
            set3.color = chart.context.getColor(R.color.yellow_led_color)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == daluaList.size - 1 && isLoadingAdded) {
            LOADING
        } else {
            ITEM
        }
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        add(SingleSchedule())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
        val position = daluaList.size - 1
        if (position != -1) {
            daluaList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun add(group: SingleSchedule) {
        daluaList.add(group)
        notifyItemInserted(daluaList.size - 1)
    }

    fun addAll(moveResults: List<SingleSchedule>) {
        for (result in moveResults) {
            add(result)
        }
    }

    fun clearList(){
        daluaList.clear()
    }

    fun getItem(position: Int): SingleSchedule {
        return daluaList[position]
    }

    inner class MyViewHolder(val binding: ItemMySchedulesBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class LoadingViewHolder(val binding: ItemProgressBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        private const val LOADING = 0
        private const val ITEM = 1
    }
}
