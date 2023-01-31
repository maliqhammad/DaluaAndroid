package com.dalua.app.ui.wifiactivity

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.net.wifi.ScanResult
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dalua.app.R
import com.dalua.app.databinding.ItemNetworksAddBinding
import com.dalua.app.databinding.ItemNetworksBinding
import com.dalua.app.utils.AppConstants
import com.dalua.app.utils.ProjectUtil

class WifiAdapterRV(private val results: List<ScanResult>, val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class MyViewHolder(itemView: ItemNetworksBinding) : RecyclerView.ViewHolder(itemView.root) {
        val itemBinding: ItemNetworksBinding = itemView
    }

    class MyViewHolder1(itemView: ItemNetworksAddBinding) : RecyclerView.ViewHolder(itemView.root) {
        val itemBinding: ItemNetworksAddBinding = itemView
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

        if (viewType == results.size) {

            val itemBinding: ItemNetworksAddBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_networks_add,
                parent,
                false
            )
            return MyViewHolder1(itemBinding)

        } else {

            val itemBinding: ItemNetworksBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_networks,
                parent,
                false
            )
            return MyViewHolder(itemBinding)

        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d(ContentValues.TAG, "onBindViewHolder: Position: $position")
        if (position == results.size) {

            (holder as MyViewHolder1).itemBinding.button1.setOnClickListener {
                showWifiCredentialsDialog("")
            }

        } else {

            (holder as MyViewHolder).itemBinding.textView2.text = results[position].SSID
            holder.itemBinding.textView5.text =
                results[position].capabilities.replace("[", "").split("]")[0]

            holder.itemBinding.button1.setOnClickListener {
                showWifiCredentialsDialog(results[position].SSID)
            }

        }


    }

    private fun showWifiCredentialsDialog(ssid: String?) {

        val dialog = Dialog(context)
        if (dialog.isShowing) dialog.cancel()
        dialog.setContentView(R.layout.item_add_wifi_cred)
        dialog.window?.setBackgroundDrawableResource(R.color.transparent)
        dialog.setCancelable(true)
//        dialog.window?.setWindowAnimations(R.style.DialogAnimation)
        dialog.findViewById<EditText>(R.id.edittext).setText(ssid)
        dialog.findViewById<Button>(R.id.button1).setOnClickListener {
            val ssid1 = dialog.findViewById<EditText>(R.id.edittext).text.toString().trim()
            val password = dialog.findViewById<EditText>(R.id.etPassword).text.toString().trim()

            if (ssid1.isNotEmpty() && password.isNotEmpty()) {
                dialog.dismiss()
                AppConstants.SSID = ssid1
                AppConstants.PASSWORD = password
                AppConstants.StartConnectionNow = true
                (context as Activity).apply {
                    setResult(RESULT_OK)
                    finish()
                }
//                    connectDeviceDialog()

            } else {
                ProjectUtil.showToastMessage(
                    context as ShowWifiConnectionActivity,
                    true,
                    "Please fill the form correctly."
                )
            }

        }
        dialog.findViewById<Button>(R.id.button).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }

    override fun getItemCount(): Int {
        return results.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}
