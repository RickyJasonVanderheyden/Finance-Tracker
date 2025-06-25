package com.example.trackerfinance

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.trackerfinance.data.Transaction
import com.example.trackerfinance.data.TransactionRepository
import com.example.trackerfinance.data.TransactionType
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TransactionRepository = TransactionRepository(application)
    
    val allTransactions: LiveData<List<Transaction>> = repository.getAllTransactions()
    val incomeTransactions: LiveData<List<Transaction>> = repository.getTransactionsByType(TransactionType.INCOME)
    val expenseTransactions: LiveData<List<Transaction>> = repository.getTransactionsByType(TransactionType.EXPENSE)
    val totalIncome: LiveData<Double?> = repository.getTotalAmount(TransactionType.INCOME)
    val totalExpense: LiveData<Double?> = repository.getTotalAmount(TransactionType.EXPENSE)

    fun saveTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.saveTransaction(transaction)
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.updateTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    fun setMonthlyBudget(budget: Double) {
        repository.setMonthlyBudget(budget)
    }

    fun getMonthlyBudget(): Double {
        return repository.getMonthlyBudget()
    }
} 