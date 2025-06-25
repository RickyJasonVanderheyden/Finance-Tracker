package com.example.trackerfinance.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trackerfinance.MainActivity
import com.example.trackerfinance.MainViewModel
import com.example.trackerfinance.R
import com.example.trackerfinance.data.Transaction
import com.example.trackerfinance.data.TransactionType
import com.example.trackerfinance.databinding.FragmentActivityBinding
import com.example.trackerfinance.ui.transaction.AddTransactionFragment

class ActivityFragment : Fragment() {
    private var _binding: FragmentActivityBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TransactionAdapter
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActivityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        setupRecyclerView()
        setupToggleGroup()
        observeTransactions()
    }

    private fun observeTransactions() {
        viewModel.allTransactions.observe(viewLifecycleOwner) { transactions ->
            adapter.submitList(transactions)
            binding.textEmpty.visibility = if (transactions.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun setupRecyclerView() {
        adapter = TransactionAdapter(
            onEditClick = { transaction ->
                val bundle = Bundle().apply {
                    putParcelable("transaction", transaction)
                }
                findNavController().navigate(R.id.navigation_add, bundle)
            },
            onDeleteClick = { transaction ->
                showDeleteConfirmationDialog(transaction)
            }
        )
        binding.recyclerViewTransactions.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@ActivityFragment.adapter
        }
    }

    private fun showDeleteConfirmationDialog(transaction: Transaction) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Transaction")
            .setMessage("Are you sure you want to delete this transaction?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteTransaction(transaction)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setupToggleGroup() {
        binding.toggleFilter.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btnAll -> observeAllTransactions()
                    R.id.btnIncome -> observeIncomeTransactions()
                    R.id.btnExpense -> observeExpenseTransactions()
                }
            }
        }
        binding.toggleFilter.check(R.id.btnAll)
    }

    private fun observeAllTransactions() {
        viewModel.allTransactions.observe(viewLifecycleOwner) { transactions ->
            adapter.submitList(transactions)
            binding.textEmpty.visibility = if (transactions.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun observeIncomeTransactions() {
        viewModel.incomeTransactions.observe(viewLifecycleOwner) { transactions ->
            adapter.submitList(transactions)
            binding.textEmpty.visibility = if (transactions.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun observeExpenseTransactions() {
        viewModel.expenseTransactions.observe(viewLifecycleOwner) { transactions ->
            adapter.submitList(transactions)
            binding.textEmpty.visibility = if (transactions.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 