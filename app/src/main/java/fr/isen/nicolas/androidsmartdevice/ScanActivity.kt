package fr.isen.nicolas.androidsmartdevice

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.companion.AssociationInfo
import android.companion.AssociationRequest
import android.companion.BluetoothDeviceFilter
import android.companion.CompanionDeviceManager
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.ParcelUuid
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import fr.isen.nicolas.androidsmartdevice.databinding.ActivityScanBinding
import java.util.*
import java.util.concurrent.Executor
import java.util.regex.Pattern
import kotlin.collections.ArrayList

private const val SELECT_DEVICE_REQUEST_CODE = 0
private const val PERMISSION_REQUEST_CODE = 123

@RequiresApi(Build.VERSION_CODES.O)
class ScanActivity : AppCompatActivity() {
    private lateinit var binding : ActivityScanBinding
    val bluetoothAdapter : BluetoothAdapter? by
    lazy(LazyThreadSafetyMode.NONE){
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }

    private val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
    private var scanning = false
    private val handler = Handler()
    // Stops scanning after 10 seconds.
    private val SCAN_PERIOD: Long = 10000
    private val leDeviceList = ArrayList<BluetoothDevice>()
    //private val leDeviceListAdapter = ScanAdapter(this,bluetoothLeScanner)
    val requestPermissionLauncher =
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        //permissions.containsValue(false)
        if(permissions.all { it.value }) {
            scanBLEDevice()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val actionBar = supportActionBar
        actionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.blue)))
        binding.RecyclerViewBLE.layoutManager = LinearLayoutManager(this)

        ScanDevice()
    }
/*
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                return
            }
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }

        val deviceFilter: BluetoothDeviceFilter = BluetoothDeviceFilter.Builder()
            .setNamePattern(Pattern.compile("My device"))
            .addServiceUuid(ParcelUuid(UUID(0x123abcL, -1L)), null)
            .build()

        val pairingRequest: AssociationRequest = AssociationRequest.Builder()
            .addDeviceFilter(deviceFilter)
            .setSingleDevice(true)
            .build()

        deviceManager.associate(pairingRequest,
            object : CompanionDeviceManager.Callback() {

                override fun onDeviceFound(chooserLauncher: IntentSender) {
                    startIntentSenderForResult(chooserLauncher,
                        SELECT_DEVICE_REQUEST_CODE, null, 0, 0, 0)
                }

                override fun onFailure(error: CharSequence?) {
                    // Handle the failure.
                }
            }, null)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            SELECT_DEVICE_REQUEST_CODE -> when(resultCode) {
                Activity.RESULT_OK -> {
                    val deviceToPair: BluetoothDevice? =
                        data?.getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE)
                    deviceToPair?.let { device ->
                        if (ActivityCompat.checkSelfPermission(
                                this,
                                Manifest.permission.BLUETOOTH_CONNECT
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            return
                        }
                        device.createBond()
                    }
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }*/

    private fun scanLeDevice() {
        /*if (!scanning) { // Stops scanning after a pre-defined scan period.
            handler.postDelayed({
                scanning = false
                bluetoothLeScanner.stopScan(leScanCallback)
            }, SCAN_PERIOD)
            scanning = true
            bluetoothLeScanner.startScan(leScanCallback)
        } else {
            scanning = false
            bluetoothLeScanner.stopScan(leScanCallback)
        }*/
    }
    private val leScanCallback: ScanCallback = object : ScanCallback() {
       /* override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val device = result.device
            if (!leDeviceList.contains(device)) {
                leDeviceList.add(device)
                leDeviceListAdapter.notifyDataSetChanged()
            }
        }*/
    }

    private fun ScanDevice(){
        if(allPermissionGranted()){
            scanBLEDevice()
            Toast.makeText(baseContext, "Scan Work ! ",
                Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(baseContext, "Warning! No permission granted ! ",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun initToggleDevice(){
        if(allPermissionGranted()){
            scanBLEDevice()
        }
        else {
            requestPermissionLauncher.launch(getAllPermissions())
        }
        //val Scan = bluetoothAdapter.bluetoothLeScanner
        //binding.RecyclerViewBLE.adapter = startScan(bluetoothAdapter.bluetoothLeScanner)

    }
    private fun startScan(bluetoothLeScanner: BluetoothLeScanner){

    }

    private fun scanBLEDevice() {
        initToggleDevice()
    }

    private fun getAllPermissions(): Array<String>{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        } else {
            TODO("VERSION.SDK_INT < S")
        }
    }
    private fun allPermissionGranted(): Boolean{
        val allPermissions = getAllPermissions()
        return allPermissions.all{
            ContextCompat.checkSelfPermission(this,it) == PackageManager.PERMISSION_GRANTED
            true
        }
    }
    /*
    private lateinit var binding : ActivityScanBinding
    private var BLE_List: ArrayList<String> = ArrayList(30)
    companion object {
        private const val REQUEST_ENABLE_BT = 123
        // other constants or static methods can be defined here
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val actionBar = supportActionBar
        actionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.blue)))
        binding.RecyclerViewBLE.layoutManager = LinearLayoutManager(this)

        val Empty_List : ArrayList<String> = ArrayList(0)
        BLE_List.add(0,"Reseau du KGB")
        BLE_List.add(1,"jules")
        BLE_List.add(2,"Wifisen")
        BLE_List.add(3,"SFR")
        BLE_List.add(4,"200 gigas")
        //Log.w("Adapter", "Liste : $BLE_List")

        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
        }
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                return
            }
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
        val deviceFilter: BluetoothDeviceFilter = BluetoothDeviceFilter.Builder()
            // Match only Bluetooth devices whose name matches the pattern.
            .setNamePattern(Pattern.compile("My device"))
            // Match only Bluetooth devices whose service UUID matches this pattern.
            .addServiceUuid(ParcelUuid(UUID(0x123abcL, -1L)), null)
            .build()
        val pairingRequest: AssociationRequest = AssociationRequest.Builder()
            // Find only devices that match this request filter.
            .addDeviceFilter(deviceFilter)
            // Stop scanning as soon as one device matching the filter is found.
            .setSingleDevice(true)
            .build()

        val deviceManager =
            requireContext().getSystemService(Context.COMPANION_DEVICE_SERVICE)

        deviceManager.associate(pairingRequest,
            object : CompanionDeviceManager.Callback() {
                // Called when a device is found. Launch the IntentSender so the user
                // can select the device they want to pair with.
                override fun onDeviceFound(chooserLauncher: IntentSender) {
                    startIntentSenderForResult(chooserLauncher,
                        SELECT_DEVICE_REQUEST_CODE, null, 0, 0, 0)
                }
                override fun onFailure(error: CharSequence?) {
                    // Handle the failure.
                }
            }, null)





        /*
        var scanning = false
        binding.buttonScan.setOnClickListener {
            scanning = !scanning
            if(scanning){
                val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                if (bluetoothAdapter == null) {
                    // Bluetooth n'est pas disponible sur ce périphérique
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)}
                } else {
                    // Le Bluetooth est disponible
                    binding.progressBarScan.isIndeterminate = true
                    binding.buttonScan.text = "Scan BLE en cours ..."
                    binding.imageView2.visibility = View.INVISIBLE
                    binding.RecyclerViewBLE.adapter = ScanAdapter(BLE_List)
                }
            }
            if(!scanning){
                binding.progressBarScan.isIndeterminate = false
                binding.buttonScan.text = "Lancer le Scan BLE"
                binding.imageView2.visibility = View.VISIBLE
                binding.RecyclerViewBLE.adapter = ScanAdapter(Empty_List)
            }
        }*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            SELECT_DEVICE_REQUEST_CODE -> when(resultCode) {
                Activity.RESULT_OK -> {
                    val deviceToPair: BluetoothDevice? =
                        data?.getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE)
                    deviceToPair?.let { device ->
                        if (ActivityCompat.checkSelfPermission(
                                this,
                                Manifest.permission.BLUETOOTH_CONNECT
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            return
                        }
                        device.createBond()
                        // Continue to interact with the paired device.
                    }
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun requireContext(): Context {
        return this
    }*/
    data class BLE(
        val name: String,
        val address: String
            )
}