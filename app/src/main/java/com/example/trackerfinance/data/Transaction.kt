package com.example.trackerfinance.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val amount: Double,
    val category: String,
    val type: TransactionType,
    val date: Date = Date()
) : Parcelable

@Parcelize
enum class TransactionType : Parcelable {
    INCOME, EXPENSE
} 