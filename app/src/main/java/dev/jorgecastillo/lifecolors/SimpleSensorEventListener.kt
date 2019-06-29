package dev.jorgecastillo.lifecolors

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener

abstract class SimpleSensorEventListener : SensorEventListener {
  override fun onAccuracyChanged(
    sensor: Sensor?,
    accuracy: Int
  ) {
  }

  override fun onSensorChanged(event: SensorEvent?) {
  }
}
