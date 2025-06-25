package com.example.trackerfinance.ui.activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trackerfinance.R
import com.example.trackerfinance.data.Transaction
import com.example.trackerfinance.data.TransactionType
import com.example.trackerfinance.utils.CurrencyUtils
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(
    private val onEditClick: (Transaction) -> Unit = {},
    private val onDeleteClick: (Transaction) -> Unit = {}
) : ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = getItem(position)
        holder.bind(transaction)
    }

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(R.id.textTitle)
        private val categoryText: TextView = itemView.findViewById(R.id.textCategory)
        private val dateText: TextView = itemView.findViewById(R.id.textDate)
        private val amountText: TextView = itemView.findViewById(R.id.textAmount)
        private val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)

        private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        fun bind(transaction: Transaction) {
            titleText.text = transaction.title
            categoryText.text = transaction.category
            dateText.text = dateFormat.format(transaction.date)
            
            val amount = CurrencyUtils.formatAmount(itemView.context, transaction.amount)
            amountText.text = amount
            
            val textColor = if (transaction.type == TransactionType.EXPENSE) {
                ContextCompat.getColor(itemView.context, R.color.expense)
            } else {
                ContextCompat.getColor(itemView.context, R.color.income)
            }
            amountText.setTextColor(textColor)

            btnEdit.setOnClickListener {
                onEditClick(transaction)
            }

            btnDelete.setOnClickListener {
                onDeleteClick(transaction)
            }
        }
    }

    private class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
} 