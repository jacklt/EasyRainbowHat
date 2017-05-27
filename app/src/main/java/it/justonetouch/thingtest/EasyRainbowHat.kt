package it.justonetouch.thingtest

import android.graphics.Color
import android.os.Handler
import android.util.Log
import com.google.android.things.contrib.driver.apa102.Apa102
import com.google.android.things.contrib.driver.bmx280.Bmx280
import com.google.android.things.contrib.driver.button.Button
import com.google.android.things.contrib.driver.ht16k33.AlphanumericDisplay
import com.google.android.things.contrib.driver.ht16k33.Ht16k33
import com.google.android.things.contrib.driver.pwmservo.Servo
import com.google.android.things.contrib.driver.pwmspeaker.Speaker
import com.google.android.things.contrib.driver.rainbowhat.RainbowHat
import com.google.android.things.pio.Gpio

open class EasyRainbowHat {
    private val TAG = "EasyRainbowHat"

    private var initButtonA = false
    private var initButtonB = false
    private var initButtonC = false
    private var initLedRed = false
    private var initLedGreen = false
    private var initLedBlue = false
    private var initBuzzer = false
    private var initSensor = false
    private var initLedStrip = false
    private var initServo = false
    private var initSegment = false

    val colorsRainbow = IntArray(RainbowHat.LEDSTRIP_LENGTH) { Color.HSVToColor(255, floatArrayOf(it * (360f / RainbowHat.LEDSTRIP_LENGTH.toFloat()), 1f, 1f)) }
    val colorsRed = IntArray(RainbowHat.LEDSTRIP_LENGTH) { 0xFFFF0000.toInt() }
    val colorsGreen = IntArray(RainbowHat.LEDSTRIP_LENGTH) { 0xFF00FF00.toInt() }
    val colorsBlue = IntArray(RainbowHat.LEDSTRIP_LENGTH) { 0xFF0000FF.toInt() }

    val buttonA: Button by lazy { initButtonA = true; RainbowHat.openButtonA() }
    val buttonB: Button by lazy { initButtonB = true; RainbowHat.openButtonB() }
    val buttonC: Button by lazy { initButtonC = true; RainbowHat.openButtonC() }
    val ledRed: Gpio by lazy { initLedRed = true; RainbowHat.openLedRed() }
    val ledGreen: Gpio by lazy { initLedGreen = true; RainbowHat.openLedGreen() }
    val ledBlue: Gpio by lazy { initLedBlue = true; RainbowHat.openLedBlue() }
    val buzzer: Speaker by lazy { initBuzzer = true; RainbowHat.openPiezo() }
    val sensor: Bmx280 by lazy {
        initSensor = true
        RainbowHat.openSensor().apply {
            setTemperatureOversampling(Bmx280.OVERSAMPLING_1X)
            setPressureOversampling(Bmx280.OVERSAMPLING_1X)
        }
    }
    val ledStrip: Apa102 by lazy {
        initLedStrip = true
        RainbowHat.openLedStrip().apply { brightness = 1 }
    }
    val servo: Servo by lazy { initServo = true; RainbowHat.openServo() }
    val segment: AlphanumericDisplay by lazy {
        initSegment = true
        RainbowHat.openDisplay().apply {
            setBrightness(Ht16k33.HT16K33_BRIGHTNESS_MAX)
            setEnabled(true)
        }
    }

    fun Button.onClick(onPress: (Boolean) -> Unit) = setOnButtonEventListener { _, pressed -> onPress(pressed) }

    fun Apa102.colors(colors: IntArray) {
        // Light up the rainbow
        Log.d(TAG, "ledStrip: ${colors.asList()}")
        ledStrip.write(colors)
        ledStrip.write(colors) // needed, not sure why :|
    }

    fun temperature() = sensor.readTemperature().toDouble()

    fun pressure() = sensor.readPressure().toDouble()

    fun buzz(frequency: Double = 440.0, delayMillis: Long = 200) {
        buzzer.play(frequency)
        Handler().postDelayed({ buzzer.stop() }, delayMillis)
    }

    fun Gpio.on() { value = true }

    fun Gpio.off() { value = false }

    fun onDestroy() {
        if (initButtonA) buttonA.close()
        if (initButtonB) buttonB.close()
        if (initButtonC) buttonC.close()

        if (initLedRed) ledRed.close()
        if (initLedGreen) ledGreen.close()
        if (initLedBlue) ledBlue.close()

        if (initLedStrip) ledStrip.close()
        if (initBuzzer) {
            buzzer.stop()
            buzzer.close()
        }
        if (initSensor) sensor.close()
        if (initServo) servo.close()

        if (initSegment) segment.close()
    }
}
