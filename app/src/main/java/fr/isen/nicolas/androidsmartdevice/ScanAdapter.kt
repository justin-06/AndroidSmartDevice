package fr.isen.nicolas.androidsmartdevice

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView

class ScanAdapter(val context : Context,val BLE : ArrayList<BluetoothDevice>) : RecyclerView.Adapter<ScanAdapter.ScanViewHolder>(){

    fun addDevice(device: BluetoothDevice) {
        if (!BLE.contains(device)) {
            BLE.add(device)
        }
    }
    class ScanViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        var cellText : TextView = view.findViewById<TextView>(R.id.text_BLE_name)
        var address : TextView = view.findViewById(R.id.text_BLE_address)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanViewHolder {
        Log.w("Adapter", "create : $BLE")
        return ScanViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_scan, parent, false))
    }
    override fun onBindViewHolder(holder: ScanViewHolder, position: Int) {
        val device = BLE[position]
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        holder.cellText.text = device.name
        holder.address.text = device.address
        Log.w("Adapter", "position : $device")
    }
    override fun getItemCount(): Int = BLE.size
}