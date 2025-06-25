package com.example.trackerfinance.utils

import android.content.Context
import java.text.NumberFormat
import java.util.*

object CurrencyUtils {
    private const val PREFS_NAME = "TrackerFinancePrefs"
    private const val KEY_CURRENCY_FORMAT = "currency_format"

    fun formatAmount(context: Context, amount: Double): String {
        val currencySymbol = getCurrencySymbol(context)
        return "$currencySymbol${String.format("%.2f", amount)}"
    }

    fun getCurrencySymbol(context: Context): String {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPrefs.getString(KEY_CURRENCY_FORMAT, "$") ?: "$"
    }

    fun saveCurrencyFormat(context: Context, format: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_CURRENCY_FORMAT, format)
            .apply()
    }
} 