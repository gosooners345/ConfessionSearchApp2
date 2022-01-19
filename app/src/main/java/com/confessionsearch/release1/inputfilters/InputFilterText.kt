package com.confessionsearch.release1.inputfilters

import android.text.InputFilter
import android.text.Spanned

class InputFilterText(minValue: Int, maxValue: Int) : InputFilter {

    private var min = minValue
    private var max = maxValue


    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        try {
            val input = (dest.toString() + source.toString()).toInt()
            if (isInRange(min, max, input))
                return null
        } catch (nfe: NumberFormatException) {
            nfe.printStackTrace()
        }
        return ""
    }

    fun isInRange(minimum: Int, maximum: Int, testValue: Int): Boolean {
        return if (maximum > minimum) testValue in minimum..maximum else testValue in maximum..minimum
    }
}