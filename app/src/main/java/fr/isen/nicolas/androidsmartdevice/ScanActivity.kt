package fr.isen.nicolas.androidsmartdevice

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.companion.AssociationInfo
import android.companion.AssociationRequest
import android.companion.BluetoothDeviceFilter
import android.companion.CompanionDeviceManager
import android.content.*
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.os.*
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import fr.isen.nicolas.androidsmartdevice.databinding.ActivityScanBinding
import java.util.*
import kotlin.collections.ArrayList
import kotlin.LazyThreadSafetyMode

@RequiresApi(Build.VERSION_CODES.O)
class ScanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanBinding
    private val bluetoothAdapter: BluetoothAdapter by
    lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager =
            getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }
    private var scanning = false
    private val handler = Handler()
    private lateinit var bluetoothLeScanner: BluetoothLeScanner
    // Stops scanning after 10 seconds.
    private val SCAN_PERIOD: Long = 10000
    private val DeviceList = ArrayList<BLE>()

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            //permissions.containsValue(false)
            if (permissions.all { it.value }) {
                togglePlayPause()
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = supportActionBar
        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        actionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.blue)))
        binding.RecyclerViewBLE.layoutManager = LinearLayoutManager(this)
        binding.progressBarScan.isIndeterminate = false
        binding.buttonScan.text = "Lancer le Scan BLE"
        binding.imageView2.visibility = View.VISIBLE

        askPermissionBeforeScan()
    }
    private fun togglePlayPause(){
        binding.buttonScan.setOnClickListener {
            if (!scanning){
                Log.w(" SCAN ", "scanLeDevice")
                scanLeDevice()
                Log.w(" SCAN ", "ScanAdapter")
            }
        }
    }
    private fun displayButton(){
        if (scanning){
            binding.progressBarScan.isVisible = true
            binding.progressBarScan.isIndeterminate = true
            binding.buttonScan.text = "Scan BLE en cours ..."
            binding.imageView2.visibility = View.INVISIBLE
        }
        if (!scanning) {
            binding.progressBarScan.isIndeterminate = false
            binding.buttonScan.text = "Lancer le Scan BLE"
            binding.imageView2.visibility = View.VISIBLE
        }
    }
    @SuppressLint("MissingPermission")
    private fun scanLeDevice() {
        if (!scanning) { // Stops scanning after a pre-defined scan period.
            handler.postDelayed({
                scanning = false
                displayButton()
                bluetoothLeScanner.stopScan(leScanCallback)
            }, SCAN_PERIOD)
            scanning = true
            displayButton()
            bluetoothLeScanner.startScan(leScanCallback)
        } else {
            scanning = false
            displayButton()
            bluetoothLeScanner.stopScan(leScanCallback)
        }
        Log.w(" SCAN ", "scanning end = $scanning")
    }
    private val leScanCallback: ScanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            Log.w(" SCAN ", "scan callback")
            var multi = false
            val newDevice = BLE()
            if(result.device.name != null){
                if(DeviceList.isNotEmpty()){
                    for(i in 0 until DeviceList.size){
                        if(DeviceList[i].name == result.device.name){
                            multi = true
                        }
                    }
                    if(multi == false){
                        Log.w(" SCAN ", "Device Name = ${result.device.name}")
                        newDevice.addDevice(result.device.name, result.device.address)
                        DeviceList.add(newDevice)
                    }
                } else {
                    Log.w(" SCAN ", "Device Name = ${result.device.name}")
                    newDevice.addDevice(result.device.name, result.device.address)
                    DeviceList.add(newDevice)
                }
                binding.RecyclerViewBLE.adapter = ScanAdapter(DeviceList)
            }
        }
    }

    private fun askPermissionBeforeScan() {
        if (allPermissionGranted()) {
            togglePlayPause()
        } else {
            requestPermissionLauncher.launch(getAllPermissions())
        }
    }
    private fun getAllPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        }
    }
    private fun allPermissionGranted(): Boolean {
        val allPermissions = getAllPermissions()
        return allPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }
    class BLE{
        var name : String = ""
        var address : String = ""

        fun addDevice(Name: String, Address : String) {
            name = Name
            address = Address
        }
    }
}