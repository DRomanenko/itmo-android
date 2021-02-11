package com.github.dromanenko.calculator

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.Button
import android.widget.TextView
import net.objecthunter.exp4j.ExpressionBuilder

class MainActivity : AppCompatActivity() {

    companion object {
        const val KEY_STATE = "KEY_STATE"
        const val KEY_TV = "KEY_TV"
    }

    private lateinit var tvInRes: TextView
    var stateMinus: Boolean = false
    var stateInf: Boolean = false
    var stateNum: Boolean = false
    var stateDot: Boolean = false
    var stateError: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvInRes = findViewById(R.id.tvInRes)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBooleanArray(
            KEY_STATE,
            booleanArrayOf(stateMinus, stateInf, stateNum, stateDot, stateError)
        )
        outState.putString(KEY_TV, tvInRes.text.toString())
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        tvInRes.text = savedInstanceState.getString(KEY_TV)
        val keys = savedInstanceState.getBooleanArray(KEY_STATE)
        stateMinus = keys!![0]
        stateInf = keys[1]
        stateNum = keys[2]
        stateDot = keys[3]
        stateError = keys[4]
    }

    fun onDigit(view: View) {
        vibrate()
        if (!stateInf) {
            if (stateError) {
                tvInRes.text = (view as Button).text
                stateError = false
            } else {
                tvInRes.append((view as Button).text)
            }
            stateNum = true
        }
    }

    fun onDecimalPoint(view: View) {
        vibrate()
        if (stateNum && !stateError && !stateDot) {
            tvInRes.append(".")
            stateNum = false
            stateDot = true
        }
    }

    fun onOperator(view: View) {
        vibrate()
        if (stateNum && !stateError) {
            tvInRes.append((view as Button).text)
            stateNum = false
            stateDot = false
        }
    }

    fun clear() {
        stateMinus = false
        stateInf = false
        stateNum = false
        stateDot = false
        stateError = false
    }

    fun onClear(view: View) {
        vibrate()
        clear()
        tvInRes.text = ""
    }

    fun onEqual(view: View) {
        vibrate()
        if (!stateError) {
            if (stateNum) {
                try {
                    val result = ExpressionBuilder(tvInRes.text.toString()).build().evaluate()
                    tvInRes.text = result.toString()
                    if (result.isInfinite()) {
                        clear()
                        stateInf = true
                    } else {
                        stateDot = true
                    }
                } catch (ex: ArithmeticException) {
                    clear()
                    tvInRes.text = "Error"
                    stateError = true
                }
            }
        }
    }

    fun vibrate() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) { // Vibrator availability checking
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        50,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                ) // New vibrate method for API Level 26 or higher
            } else {
                vibrator.vibrate(50) // Vibrate method for below API Level 26
            }
        }
    }
}