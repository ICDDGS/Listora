// Utils.kt
package com.dan.listora.util

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.dan.listora.R
import com.google.android.material.snackbar.Snackbar

fun View.styledSnackbar(message: String, context: Context) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT)
        .setTextColor(ContextCompat.getColor(context, R.color.text_on_primary))
        .setBackgroundTint(ContextCompat.getColor(context, R.color.listora_mint))
        .show()
}
