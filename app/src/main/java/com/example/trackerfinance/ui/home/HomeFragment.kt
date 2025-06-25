package com.example.trackerfinance.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trackerfinance.MainActivity
import com.example.trackerfinance.MainViewModel
import com.example.trackerfinance.R
import com.example.trackerfinance.data.Transaction
import com.example.trackerfinance.data.TransactionType
import com.example.trackerfinance.databinding.FragmentHomeBinding
import com.example.trackerfinance.ui.activity.TransactionAdapter
import com.example.trackerfinance.utils.CurrencyUtils
import java.text.NumberFormat
import java.util.*

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)
    private lateinit var transactionAdapter: TransactionAdapter
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
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        setupUI()
        observeData()
    }

    private fun setupUI() {
        // Setup View All button
        binding.buttonViewAll.setOnClickListener {
            findNavController().navigate(R.id.navigation_activity)
        }

        // Setup RecyclerView
        transactionAdapter = TransactionAdapter(
            onEditClick = { transaction ->
                val bundle = Bundle().apply {
                    putParcelable("transaction", transaction)
                }
                findNavController().navigate(R.id.navigation_add, bundle)
            },
            onDeleteClick = { transaction ->
                viewModel.deleteTransaction(transaction)
            }
        )
        
        binding.recyclerViewRecentTransactions.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = transactionAdapter
        }
    }

    private fun observeData() {
        viewModel.allTransactions.observe(viewLifecycleOwner) { transactions ->
            updateSummary(transactions)
        }

        viewModel.totalIncome.observe(viewLifecycleOwner) { totalIncome ->
            binding.textTotalIncome.text = CurrencyUtils.formatAmount(requireContext(), totalIncome ?: 0.0)
        }

        viewModel.totalExpense.observe(viewLifecycleOwner) { totalExpenses ->
            binding.textTotalExpenses.text = CurrencyUtils.formatAmount(requireContext(), totalExpenses ?: 0.0)
            updateBudgetStatus(totalExpenses ?: 0.0)
        }
    }

    private fun updateSummary(transactions: List<Transaction>) {
        val monthlyBudget = viewModel.getMonthlyBudget()

        // Calculate budget balance (budget - expenses)
        val totalExpenses = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }
        val budgetBalance = monthlyBudget - totalExpenses

        // Update UI
        binding.textCurrentBalance.text = CurrencyUtils.formatAmount(requireContext(), budgetBalance)

        // Update recent transactions
        val recentTransactions = transactions.sortedByDescending { it.date }.take(5)
        transactionAdapter.submitList(recentTransactions)
    }

    private fun updateBudgetStatus(totalExpenses: Double) {
        val monthlyBudget = viewModel.getMonthlyBudget()

        // Update budget status
        binding.textBudgetStatus.text = "${CurrencyUtils.formatAmount(requireContext(), totalExpenses)} / ${CurrencyUtils.formatAmount(requireContext(), monthlyBudget)}"
        
        // Update progress bar and show warnings
        if (monthlyBudget > 0) {
            val spendingRatio = totalExpenses / monthlyBudget
            val progress = (spendingRatio * 100).toInt().coerceIn(0, 100)
            binding.progressBudget.progress = progress

            // Check if notifications are enabled
            val sharedPreferences = requireContext().getSharedPreferences("TrackerFinancePrefs", Context.MODE_PRIVATE)
            val notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true)

            // Update warning message and progress bar color based on spending
            when {
                spendingRatio >= DANGER_THRESHOLD -> {
                    if (notificationsEnabled) {
                        showBudgetWarning("Budget exceeded! You've spent ${CurrencyUtils.formatAmount(requireContext(), totalExpenses - monthlyBudget)} over your budget.", true)
                    }
                    binding.progressBudget.progressTintList = ContextCompat.getColorStateList(requireContext(), R.color.expense)
                }
                spendingRatio >= WARNING_THRESHOLD -> {
                    if (notificationsEnabled) {
                        val remainingBudget = monthlyBudget - totalExpenses
                        showBudgetWarning("Warning: Only ${CurrencyUtils.formatAmount(requireContext(), remainingBudget)} remaining in your budget!", false)
                    }
                    binding.progressBudget.progressTintList = ContextCompat.getColorStateList(requireContext(), R.color.warning)
                }
                else -> {
                    hideBudgetWarning()
                    binding.progressBudget.progressTintList = ContextCompat.getColorStateList(requireContext(), R.color.primary)
                }
            }
        } else {
            binding.progressBudget.progress = 0
            hideBudgetWarning()
        }
    }

    private fun showBudgetWarning(message: String, isDanger: Boolean) {
        binding.textBudgetWarning.apply {
            text = message
            setTextColor(ContextCompat.getColor(requireContext(), 
                if (isDanger) R.color.expense else R.color.warning))
            visibility = View.VISIBLE
        }
    }

    private fun hideBudgetWarning() {
        binding.textBudgetWarning.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 