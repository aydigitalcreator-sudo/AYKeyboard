package com.aydigitalcreator.aykeyboard

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * SettingsActivity — the screen that opens when the user taps the AY Keyboard
 * app icon. Since Android keyboards can't be "opened" directly (they only run
 * when the system requests them for text input), this screen's main job is to
 * walk the user through two steps:
 *   1. Enable AY Keyboard in system settings
 *   2. Switch to AY Keyboard as the active input method
 */
class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 96, 48, 48)
        }
val title = TextView(this).apply {
            text = "AY Keyboard"
            textSize = 26f
            setPadding(0, 0, 0, 24)
        }

        val subtitle = TextView(this).apply {
            text = "Set up your keyboard in two quick steps:"
            textSize = 16f
            setPadding(0, 0, 0, 40)
        }

        val enableButton = Button(this).apply {
            text = "Step 1: Enable AY Keyboard"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
            }
        }

        val switchButton = Button(this).apply {
            text = "Step 2: Switch to AY Keyboard"
            setOnClickListener {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showInputMethodPicker()
            }
        }

        root.addView(title)
        root.addView(subtitle)
        root.addView(enableButton)
        root.addView(switchButton)

        setContentView(root)
    }
}
