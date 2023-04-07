package fr.isen.nicolas.androidsmartdevice

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView

class ScanAdapter(val BLE : ArrayList<ScanActivity.BLE>) : RecyclerView.Adapter<ScanAdapter.ScanViewHolder>(){
    class ScanViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        var cellText : TextView = view.findViewById(R.id.text_BLE_name)
        var address : TextView = view.findViewById(R.id.text_BLE_address)
        var layout : ConstraintLayout = view.findViewById(R.id.device)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanViewHolder {
        Log.w("Adapter", "create : $BLE")
        return ScanViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_scan, parent, false))
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ScanViewHolder, position: Int) {
        val device = BLE[position]
        holder.cellText.text = device.name
        holder.address.text = device.address
        Log.w("Adapter", "position : $device")

        holder.layout.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DeviceActivity::class.java)
            intent.putExtra("name",device.name)
            intent.putExtra("address",device.address)
            context.startActivity(intent)
        }
    }
    override fun getItemCount(): Int = BLE.size

}