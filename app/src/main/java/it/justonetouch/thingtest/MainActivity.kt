package it.justonetouch.thingtest

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.google.android.things.pio.PeripheralManagerService

class MainActivity : Activity() {
    private val TAG = "HomeActivity"

    val hat = EasyRainbowHat()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Available GPIO: " + PeripheralManagerService().gpioList)

        hat.apply {
            initValue()
            ledStrip.colors(colorsRainbow)
            buttonA.onClick {
                if (it) {
                    segment.display("A")
                    ledStrip.colors(colorsRed)
                } else {
                    initValue()
                }
            }
            buttonB.onClick { segment.display(" B ${if (it) 1 else 0}") }
            buttonC.onClick {
                if (it) {
                    ledRed.on()
                    ledGreen.off()
                    ledBlue.on()
                    segment.display(temperature())
                    ledStrip.colors(colorsBlue)
                } else {
                    initValue()
                    ledGreen.on()
                    ledStrip.colors(colorsRainbow)
                }
            }
        }
    }

    private fun EasyRainbowHat.initValue() {
        segment.display("2+2=4")
        ledRed.off()
        ledGreen.off()
        ledBlue.off()
    }

    override fun onDestroy() {
        hat.onDestroy()
        super.onDestroy()
    }
}
