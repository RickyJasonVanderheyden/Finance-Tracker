package com.example.trackerfinance

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.trackerfinance.data.Transaction
import com.example.trackerfinance.data.TransactionType
import com.example.trackerfinance.databinding.DialogTransactionBinding
import java.text.SimpleDateFormat
import java.util.*

class TransactionDialogFragment : DialogFragment() {
    private var transaction: Transaction? = null
    private val expenseCategories = listOf("Food", "Transport", "Bills", "Shopping", "Health", "Other")
    private val incomeCategories = listOf("Salary", "Gift", "Investment", "Other")
    private var _currentCategories: List<String> = expenseCategories
    private var _binding: DialogTransactionBinding? = null
    private val binding get() = _binding!!
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private lateinit var viewModel: MainViewModel

    companion object {
        private const val ARG_TRANSACTION = "transaction"

        fun newInstance(transaction: Transaction? = null): TransactionDialogFragment {
            return TransactionDialogFragment().apply {
                arguments = Bundle().apply {
                    if (transaction != null) {
                        putParcelable(ARG_TRANSACTION, transaction)
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        transaction = arguments?.getParcelable(ARG_TRANSACTION)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        setupUI()
    }

    private fun setupUI() {
        setupTypeToggle()
        setupCategorySpinner()
        setupDatePicker()
        setupSaveButton()

        if (transaction != null) {
            populateFields()
        }
    }

    private fun setupTypeToggle() {
        binding.toggleType.check(R.id.btnExpense)
        binding.toggleType.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                _currentCategories = when (checkedId) {
                    R.id.btnIncome -> incomeCategories
                    else -> expenseCategories
                }
                updateCategorySpinner()
            }
        }
    }

    private fun setupCategorySpinner() {
        updateCategorySpinner()
    }

    private fun updateCategorySpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            _currentCategories
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.setAdapter(adapter)
    }

    private fun setupDatePicker() {
        binding.etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    binding.etDate.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupSaveButton() {
        binding.btnSave.setOnClickListener {
            if (validateInput()) {
                saveTransaction()
            }
        }
    }

    private fun validateInput(): Boolean {
        return when {
            binding.etTitle.text.isNullOrEmpty() -> {
                binding.etTitle.error = getString(R.string.title) + " is required"
                false
            }
            binding.etAmount.text.isNullOrEmpty() -> {
                binding.etAmount.error = getString(R.string.amount) + " is required"
                false
            }
            binding.etDate.text.isNullOrEmpty() -> {
                binding.etDate.error = getString(R.string.date) + " is required"
                false
            }
            else -> true
        }
    }

    private fun saveTransaction() {
        val title = binding.etTitle.text.toString()
        val amount = binding.etAmount.text.toString().toDouble()
        val category = binding.spinnerCategory.text.toString()
        val date = dateFormat.parse(binding.etDate.text.toString()) ?: Date()
        val type = if (binding.toggleType.checkedButtonId == R.id.btnIncome) {
            TransactionType.INCOME
        } else {
            TransactionType.EXPENSE
        }

        val newTransaction = transaction?.copy(
            title = title,
            amount = amount,
            category = category,
            type = type,
            date = date
        ) ?: Transaction(
            title = title,
            amount = amount,
            category = category,
            type = type,
            date = date
        )

        try {
            if (transaction == null) {
                viewModel.saveTransaction(newTransaction)
            } else {
                viewModel.updateTransaction(newTransaction)
            }
            (activity as? MainActivity)?.refreshDisplayedAmounts()
            dismiss()
        } catch (e: Exception) {
            Toast.makeText(context, "Error saving transaction: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun populateFields() {
        transaction?.let { t ->
            binding.etTitle.setText(t.title)
            binding.etAmount.setText(t.amount.toString())
            binding.etDate.setText(dateFormat.format(t.date))
            binding.toggleType.check(
                if (t.type == TransactionType.INCOME) {
                    _currentCategories = incomeCategories
                    updateCategorySpinner()
                    R.id.btnIncome
                } else {
                    _currentCategories = expenseCategories
                    updateCategorySpinner()
                    R.id.btnExpense
                }
            )
            val index = _currentCategories.indexOf(t.category)
            if (index >= 0) {
                binding.spinnerCategory.setText(_currentCategories[index])
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 