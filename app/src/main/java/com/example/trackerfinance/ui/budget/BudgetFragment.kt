package com.example.trackerfinance.ui.budget

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trackerfinance.MainActivity
import com.example.trackerfinance.MainViewModel
import com.example.trackerfinance.R
import com.example.trackerfinance.data.Transaction
import com.example.trackerfinance.data.TransactionType
import com.example.trackerfinance.databinding.FragmentBudgetBinding
import com.example.trackerfinance.utils.CurrencyUtils
import com.example.trackerfinance.utils.NotificationUtils

class BudgetFragment : Fragment() {
    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!
    private lateinit var expenseCategoryAdapter: CategoryBreakdownAdapter
    private lateinit var incomeCategoryAdapter: CategoryBreakdownAdapter
    private lateinit var viewModel: MainViewModel

    companion object {
        private const val WARNING_THRESHOLD = 0.8 // 80% of budget
        private const val DANGER_THRESHOLD = 1.0 // 100% of budget
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        setupUI()
        observeData()
        
        // Create notification channel
        NotificationUtils.createNotificationChannel(requireContext())
    }

    private fun setupUI() {
        // Setup expense categories recycler view
        expenseCategoryAdapter = CategoryBreakdownAdapter()
        binding.recyclerViewExpenseCategories.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = expenseCategoryAdapter
        }

        // Setup income categories recycler view
        incomeCategoryAdapter = CategoryBreakdownAdapter()
        binding.recyclerViewIncomeCategories.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = incomeCategoryAdapter
        }

        // Setup edit budget button
        binding.btnEditBudget.setOnClickListener {
            showEditBudgetDialog()
        }
    }

    private fun observeData() {
        // Observe all transactions
        viewModel.allTransactions.observe(viewLifecycleOwner) { transactions ->
            updateCategoryBreakdowns(transactions)
            checkBudgetStatus(transactions)
        }

        // Observe monthly budget
        binding.textBudgetAmount.text = CurrencyUtils.formatAmount(
            requireContext(),
            viewModel.getMonthlyBudget()
        )
    }

    private fun updateCategoryBreakdowns(transactions: List<Transaction>) {
        val expenseCategories = calculateCategoryBreakdown(transactions, TransactionType.EXPENSE)
        val incomeCategories = calculateCategoryBreakdown(transactions, TransactionType.INCOME)
        
        expenseCategoryAdapter.submitList(expenseCategories)
        incomeCategoryAdapter.submitList(incomeCategories)
    }

    private fun checkBudgetStatus(transactions: List<Transaction>) {
        val monthlyBudget = viewModel.getMonthlyBudget()
        if (monthlyBudget <= 0) return

        val totalExpenses = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }

        val spendingRatio = totalExpenses / monthlyBudget
        val spendingPercentage = (spendingRatio * 100).toInt()

        // Check if notifications are enabled
        val sharedPreferences = requireContext().getSharedPreferences("TrackerFinancePrefs", Context.MODE_PRIVATE)
        val notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true)

        when {
            spendingRatio >= DANGER_THRESHOLD -> {
                // Budget exceeded
                val exceededAmount = totalExpenses - monthlyBudget
                if (notificationsEnabled) {
                    NotificationUtils.showBudgetExceededNotification(
                        requireContext(),
                        exceededAmount
                    )
                }
            }
            spendingRatio >= WARNING_THRESHOLD -> {
                // Approaching budget limit
                val remainingBudget = monthlyBudget - totalExpenses
                if (notificationsEnabled) {
                    NotificationUtils.showBudgetWarningNotification(
                        requireContext(),
                        remainingBudget,
                        spendingPercentage
                    )
                }
            }
        }
    }

    private fun calculateCategoryBreakdown(transactions: List<Transaction>, type: TransactionType): List<CategorySpending> {
        val categoryMap = mutableMapOf<String, Double>()
        
        // Sum up transactions by category for the specified type
        transactions.filter { it.type == type }.forEach { transaction ->
            val currentAmount = categoryMap.getOrDefault(transaction.category, 0.0)
            categoryMap[transaction.category] = currentAmount + transaction.amount
        }

        // Convert to list and sort by amount
        return categoryMap.map { (category, amount) ->
            CategorySpending(category, amount)
        }.sortedByDescending { it.amount }
    }

    private fun showEditBudgetDialog() {
        val editText = EditText(context).apply {
            hint = "Enter monthly budget"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or 
                       android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Monthly Budget")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val budgetText = editText.text.toString()
                if (budgetText.isNotEmpty()) {
                    val budget = budgetText.toDouble()
                    viewModel.setMonthlyBudget(budget)
                    binding.textBudgetAmount.text = CurrencyUtils.formatAmount(requireContext(), budget)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 