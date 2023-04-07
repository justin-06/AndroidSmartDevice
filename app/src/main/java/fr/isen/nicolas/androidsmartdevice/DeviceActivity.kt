package fr.isen.nicolas.androidsmartdevice

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import fr.isen.nicolas.androidsmartdevice.databinding.ActivityDeviceBinding
import fr.isen.nicolas.androidsmartdevice.databinding.ActivityScanBinding
import java.util.*

class DeviceActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDeviceBinding
    private var bluetoothGatt: BluetoothGatt? = null
    private val bluetoothAdapter: BluetoothAdapter by
    lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager =
            getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }
    private var led1On = false
    private var led2On = false
    private var led3On = false

    private val bluetoothGattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // successfully connected to the GATT Server
                bluetoothGatt?.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {

            }
        }
    }
    private val serviceUUID = UUID.fromString("0000feed-cc7a-482a-984a-7f2ed5b3e58f")
    private val characteristicLedUUID = UUID.fromString("0000abcd-8e22-4541-9d4c-21edae82ed19")
    private val characteristicButtonUUID = UUID.fromString("00001234-8e22-4541-9d4c-21edae82ed19")
    private val configNotifications = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val actionBar = supportActionBar
        actionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.blue)))

        val deviceName = intent.getStringExtra("name")
        val deviceAddress = intent.getStringExtra("address")
        binding.BLEName.text = deviceName
        binding.BLEAddress.text = deviceAddress

        val device = bluetoothAdapter!!.getRemoteDevice(deviceAddress)
        bluetoothGatt = device.connectGatt(this,false, bluetoothGattCallback)

        toggleLed()
    }

    @SuppressLint("MissingPermission")
    override fun onPause() {
        super.onPause()
        bluetoothGatt?.close()
    }

    fun show() {
        runOnUiThread {
            binding.groupLed.visibility = View.VISIBLE
            binding.groupLed.isClickable = true
        }
    }

    //Hide connect tools in the UI
    fun hide() {
        runOnUiThread {
            binding.groupLed.visibility = View.GONE
        }
    }
    @SuppressLint("MissingPermission")
    fun toggleLed(){
        //show()
        Log.w(" toogle LED ", "scan callback")
        binding.led1.setOnClickListener {
            val service = bluetoothGatt?.getService(serviceUUID)
            val characteristic = service?.getCharacteristic(characteristicLedUUID)

            if(!led1On) {
                binding.led1.setColorFilter(Color.BLUE)
                binding.led3.clearColorFilter()
                binding.led2.clearColorFilter()
                led2On = false
                led3On = false
                characteristic?.value = byteArrayOf(0x01)
                bluetoothGatt?.writeCharacteristic(characteristic)
            } else {
                binding.led1.clearColorFilter()
                characteristic?.value = byteArrayOf(0x00)
                bluetoothGatt?.writeCharacteristic(characteristic)
            }
            led1On = !led1On
        }
        binding.led2.setOnClickListener {
            val service = bluetoothGatt?.getService(serviceUUID)
            val characteristic = service?.getCharacteristic(characteristicLedUUID)
            if(!led2On) {
                binding.led2.setColorFilter(Color.GREEN)
                binding.led1.clearColorFilter()
                binding.led3.clearColorFilter()
                led1On = false
                led3On = false
                characteristic?.value = byteArrayOf(0x02)
                bluetoothGatt?.writeCharacteristic(characteristic)
            } else {
                binding.led2.clearColorFilter()
                characteristic?.value = byteArrayOf(0x00)
                bluetoothGatt?.writeCharacteristic(characteristic)
            }
            led2On = !led2On
        }
        binding.led3.setOnClickListener {
            val service = bluetoothGatt?.getService(serviceUUID)
            val characteristic = service?.getCharacteristic(characteristicLedUUID)
            if(!led3On) {
                binding.led3.setColorFilter(Color.RED)
                binding.led1.clearColorFilter()
                binding.led2.clearColorFilter()
                led2On = false
                led1On = false
                characteristic?.value = byteArrayOf(0x03)
                bluetoothGatt?.writeCharacteristic(characteristic)
            } else {
                binding.led3.clearColorFilter()
                characteristic?.value = byteArrayOf(0x00)
                bluetoothGatt?.writeCharacteristic(characteristic)
            }
            led3On = !led3On
        }
    }
}