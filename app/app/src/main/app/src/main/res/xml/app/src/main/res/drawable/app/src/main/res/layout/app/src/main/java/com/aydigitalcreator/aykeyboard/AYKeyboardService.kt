package com.aydigitalcreator.aykeyboard

import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout

/**
 * AYKeyboardService — the core Input Method Editor (IME).
 * This is what Android calls whenever the user taps into a text field
 * anywhere on the device (WhatsApp, Messages, browser, etc.) and this
 * keyboard is selected as the active input method.
 */
class AYKeyboardService : InputMethodService() {

    private var isShifted = false
    private var isSymbols = false

    // Letter rows (lowercase). We render Row 1/2/3 from these.
    private val row1Letters = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")
    private val row2Letters = listOf("a", "s", "d", "f", "g", "h", "j", "k", "l")
    private val row3Letters = listOf("z", "x", "c", "v", "b", "n", "m")

    // Symbol rows (shown when the "123" key is toggled).
    private val row1Symbols = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
    private val row2Symbols = listOf("@", "#", "$", "_", "&", "-", "+", "(", ")")
    private val row3Symbols = listOf("*", "\"", "'", ":", ";", "!", "?")

    /**
     * Called by Android to build the actual keyboard view that gets
     * shown above the app's UI. We build it entirely in code so you
     * can see exactly how each key is created and wired up.
     */
    override fun onCreateInputView(): View {
        val root = LayoutInflater.from(this)
            .inflate(R.layout.keyboard_view, null) as LinearLayout

        val row1 = root.findViewById<LinearLayout>(R.id.row1)
        val row2 = root.findViewById<LinearLayout>(R.id.row2)
        val row3 = root.findViewById<LinearLayout>(R.id.row3)
        val row4 = root.findViewById<LinearLayout>(R.id.row4)

        buildLetterRows(row1, row2, row3)
        buildBottomRow(row4)
        
     return root
    }

    /** Builds rows 1–3 (letters or symbols depending on mode) plus shift/backspace. */
    private fun buildLetterRows(row1: LinearLayout, row2: LinearLayout, row3: LinearLayout) {
        row1.removeAllViews()
        row2.removeAllViews()
        row3.removeAllViews()

        val r1 = if (isSymbols) row1Symbols else row1Letters
        val r2 = if (isSymbols) row2Symbols else row2Letters
        val r3 = if (isSymbols) row3Symbols else row3Letters

        r1.forEach { row1.addView(makeKey(it)) }
        r2.forEach { row2.addView(makeKey(it)) }

        // Row 3 has Shift on the left and Backspace on the right, letters in between.
        row3.addView(makeSpecialKey("⇧") { onShiftPressed() })
        r3.forEach { row3.addView(makeKey(it)) }
        row3.addView(makeSpecialKey("⌫") { onBackspacePressed() })
    }

    /** Builds row 4: symbol toggle, comma, space, period, enter. */
    private fun buildBottomRow(row4: LinearLayout) {
        row4.addView(makeSpecialKey("⏎") { onEnterPressed() })
    }

    /** Creates a standard letter/symbol key. */
    private fun makeKey(label: String): Button {
        val display = if (isShifted && !isSymbols) label.uppercase() else label
        return Button(this).apply {
            text = display
            textSize = 18f
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            setOnClickListener { onKeyPressed(display) }
        }
    }

    /** Creates a special (non-character) key like Shift, Backspace, Enter. */
    private fun makeSpecialKey(label: String, action: () -> Unit): Button {
        return Button(this).apply {
            text = label
            textSize = 16f
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.3f)
            setOnClickListener { action() }
        }
    }
      /** Creates the wide spacebar key. */
    private fun makeSpaceKey(): Button {
        return Button(this).apply {
            text = " "
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 3f)
            setOnClickListener { onKeyPressed(" ") }
        }
    }

    // ---- Key actions: these send the actual character to the focused app ----

    private fun onKeyPressed(char: String) {
        currentInputConnection?.commitText(char, 1)
        if (isShifted) {
            isShifted = false
            onCreateInputView() // refresh view to show lowercase again
        }
    }

    private fun onShiftPressed() {
        isShifted = !isShifted
        setInputView(onCreateInputView())
    }

    private fun onBackspacePressed() {
        currentInputConnection?.deleteSurroundingText(1, 0)
    }

    private fun onEnterPressed() {
        currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
        
currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
    }

    private fun onSymbolsToggled() {
        isSymbols = !isSymbols
        setInputView(onCreateInputView())
    }
}
