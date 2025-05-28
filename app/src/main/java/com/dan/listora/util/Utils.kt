// Utils.kt
package com.dan.listora.util

import android.content.Context
import android.graphics.Color
import android.view.View

import com.google.android.material.color.MaterialColors
import com.google.android.material.snackbar.Snackbar


fun View.styledSnackbar(message: String, context: Context) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).apply {
        val backgroundColor = MaterialColors.getColor(this@styledSnackbar, com.google.android.material.R.attr.colorPrimary, Color.BLACK)
        val textColor = MaterialColors.getColor(this@styledSnackbar, com.google.android.material.R.attr.colorOnPrimary, Color.WHITE)
        setBackgroundTint(backgroundColor)
        setTextColor(textColor)
        show()
    }
}

