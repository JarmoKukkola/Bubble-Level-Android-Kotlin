package com.orbitalsonic.bubble

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.content.res.Configuration
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.orbitalsonic.bubble.R
import com.orbitalsonic.bubble.widget.AccelerometerView

class BubbleFragment:Fragment(),SensorEventListener {
    private lateinit var acceleroMeterView:AccelerometerView
    private var sensorManager:SensorManager? = null
    private var aSensor:Sensor? = null
    private val acceleration = FloatArray(3) //    private val I1 = FloatArray(9)
    private var accelerometerPrevTime:Long = 0
    private val alpha = 0.96f
    private val updateInterval = 10 //mills

    override fun onCreateView(
        inflater:LayoutInflater,container:ViewGroup?,savedInstanceState:Bundle?
    ):View? {
        return inflater.inflate(
            R.layout.fragment_bubble,container,false
        ).apply {
            acceleroMeterView = findViewById(R.id.acm_view)
            sensorManager = requireContext().getSystemService(SENSOR_SERVICE) as SensorManager
            aSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        }
    }

    override fun onSensorChanged(event:SensorEvent?) {
        synchronized(this) {
            val time = System.currentTimeMillis()
            event?.let {mEvent->
                if(mEvent.sensor.type==Sensor.TYPE_ACCELEROMETER) {
                    if(aSensor!=null) {
                        acceleration[0] = acceleration[0]*alpha+mEvent.values[0]*(1-alpha)
                        acceleration[1] = acceleration[1]*alpha+mEvent.values[1]*(1-alpha)
                        acceleration[2] = acceleration[2]*alpha+mEvent.values[2]*(1-alpha)
                        updateOrientation(time)
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor:Sensor?,accuracy:Int) {
    }

    private fun startSensors() {
        aSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if(aSensor==null) {
            notSupported()
            return
        } else {
            sensorManager?.registerListener(this,aSensor,SensorManager.SENSOR_DELAY_GAME)
        }

    }

    private fun updateOrientation(time:Long) {
        val rotation = (requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager).run {
            if(android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.R) {
                requireContext().display
            } else {
                defaultDisplay
            }
        }?.rotation ?: 0
        if(time-accelerometerPrevTime>updateInterval) {
            accelerometerPrevTime = time
            acceleroMeterView.updateOrientation(-acceleration[1]/9.81f*90f,-acceleration[0]/9.81f*90f) //                when(rotation) {
            when(rotation) {
                0->acceleroMeterView.updateOrientation(-acceleration[1]/9.81f*90f,-acceleration[0]/9.81f*90f)
                1->acceleroMeterView.updateOrientation(-acceleration[0]/9.81f*90f,acceleration[1]/9.81f*90f)
                else->acceleroMeterView.updateOrientation(acceleration[0]/9.81f*90f,-acceleration[1]/9.81f*90f)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startSensors()
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    private fun notSupported() {}
}