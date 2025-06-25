package com.example.trackerfinance.ui.budget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trackerfinance.R
import com.example.trackerfinance.utils.CurrencyUtils

data class CategorySpending(
    val category: String,
    val amount: Double
)

class CategoryBreakdownAdapter : RecyclerView.Adapter<CategoryBreakdownAdapter.ViewHolder>() {
    private var categories = listOf<CategorySpending>()

    fun submitList(newCategories: List<CategorySpending>) {
        categories = newCategories
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_breakdown, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category)
    }

    override fun getItemCount() = categories.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryText: TextView = itemView.findViewById(R.id.textCategory)
        private val amountText: TextView = itemView.findViewById(R.id.textAmount)

        fun bind(category: CategorySpending) {
            categoryText.text = category.category
            amountText.text = CurrencyUtils.formatAmount(itemView.context, category.amount)
        }
    }
} 