package com.example.trackerfinance.data

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import java.util.Calendar

class TransactionRepository(private val context: Context) {
    private val transactionDao: TransactionDao = TransactionDatabase.getDatabase(context).transactionDao()
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "TransactionPrefs"
        private const val KEY_MONTHLY_BUDGET = "monthly_budget"
    }

    suspend fun saveTransaction(transaction: Transaction) {
        transactionDao.insert(transaction)
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.update(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.delete(transaction)
    }

    fun getAllTransactions(): LiveData<List<Transaction>> {
        return transactionDao.getAllTransactions()
    }

    fun getTransactionsByType(type: TransactionType): LiveData<List<Transaction>> {
        return transactionDao.getTransactionsByType(type)
    }

    fun getTransactionsByMonth(month: Int, year: Int): LiveData<List<Transaction>> {
        // Note: This will need to be handled in the ViewModel since Room doesn't support
        // complex date filtering directly in the DAO
        return transactionDao.getAllTransactions()
    }

    fun getTotalAmount(type: TransactionType): LiveData<Double?> {
        return transactionDao.getTotalAmount(type)
    }

    fun setMonthlyBudget(budget: Double) {
        sharedPreferences.edit().putFloat(KEY_MONTHLY_BUDGET, budget.toFloat()).apply()
    }

    fun getMonthlyBudget(): Double {
        return sharedPreferences.getFloat(KEY_MONTHLY_BUDGET, 0f).toDouble()
    }
} 