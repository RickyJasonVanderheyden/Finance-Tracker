package com.example.trackerfinance.ui.transaction

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.trackerfinance.MainActivity
import com.example.trackerfinance.MainViewModel
import com.example.trackerfinance.R
import com.example.trackerfinance.data.Transaction
import com.example.trackerfinance.data.TransactionType
import com.example.trackerfinance.databinding.FragmentAddTransactionBinding
import com.example.trackerfinance.ui.activity.ActivityFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddTransactionFragment : Fragment() {
    private var transaction: Transaction? = null
    private val expenseCategories = listOf("Food", "Transport", "Bills", "Shopping", "Health", "Other")
    private val incomeCategories = listOf("Salary", "Gift", "Investment", "Other")
    private var _currentCategories: List<String> = expenseCategories
    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        transaction = arguments?.getParcelable("transaction")
        setupUI()
        
        // Set default date to current date if not in edit mode
        if (transaction == null) {
            binding.editTextDate.setText(dateFormat.format(Date()))
        }
    }

    private fun setupUI() {
        setupTypeToggle()
        setupCategorySpinner()
        setupDatePicker()
        setupSaveButton()

        if (transaction != null) {
            populateFields()
            binding.btnSave.text = getString(R.string.save)
        } else {
            binding.btnSave.text = getString(R.string.add)
        }
    }

    private fun setupTypeToggle() {
        binding.btnExpense.isSelected = true
        updateButtonStyles()

        binding.btnExpense.setOnClickListener {
            binding.btnExpense.isSelected = true
            binding.btnIncome.isSelected = false
            _currentCategories = expenseCategories
            updateButtonStyles()
            updateCategorySpinner()
        }

        binding.btnIncome.setOnClickListener {
            binding.btnIncome.isSelected = true
            binding.btnExpense.isSelected = false
            _currentCategories = incomeCategories
            updateButtonStyles()
            updateCategorySpinner()
        }
    }

    private fun updateButtonStyles() {
        val selectedColor = ContextCompat.getColor(requireContext(), R.color.primary)
        val unselectedColor = ContextCompat.getColor(requireContext(), android.R.color.white)

        binding.btnExpense.setBackgroundColor(
            if (binding.btnExpense.isSelected) selectedColor else unselectedColor
        )
        binding.btnIncome.setBackgroundColor(
            if (binding.btnIncome.isSelected) selectedColor else unselectedColor
        )

        binding.btnExpense.setTextColor(
            if (binding.btnExpense.isSelected) ContextCompat.getColor(requireContext(), android.R.color.white)
            else ContextCompat.getColor(requireContext(), R.color.primary)
        )
        binding.btnIncome.setTextColor(
            if (binding.btnIncome.isSelected) ContextCompat.getColor(requireContext(), android.R.color.white)
            else ContextCompat.getColor(requireContext(), R.color.primary)
        )
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
        binding.editTextDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val currentDate = calendar.timeInMillis
            
            // Set calendar to 2 days before current date
            calendar.add(Calendar.DAY_OF_MONTH, -2)
            val twoDaysAgo = calendar.timeInMillis
            
            // Reset calendar to current date for initial display
            calendar.timeInMillis = currentDate
            
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    binding.editTextDate.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
                // Set minimum date to 2 days ago
                datePicker.minDate = twoDaysAgo
                // Set maximum date to current date
                datePicker.maxDate = currentDate
            }.show()
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
            binding.editTextTitle.text.isNullOrEmpty() -> {
                binding.editTextTitle.error = getString(R.string.title) + " is required"
                false
            }
            binding.editTextAmount.text.isNullOrEmpty() -> {
                binding.editTextAmount.error = getString(R.string.amount) + " is required"
                false
            }
            binding.editTextDate.text.isNullOrEmpty() -> {
                binding.editTextDate.error = getString(R.string.date) + " is required"
                false
            }
            else -> true
        }
    }

    private fun saveTransaction() {
        val title = binding.editTextTitle.text.toString()
        val amount = binding.editTextAmount.text.toString().toDouble()
        val category = binding.spinnerCategory.text.toString()
        val date = dateFormat.parse(binding.editTextDate.text.toString()) ?: Date()
        val type = if (binding.btnIncome.isSelected) {
            TransactionType.INCOME
        } else {
            TransactionType.EXPENSE
        }

        CoroutineScope(Dispatchers.IO).launch {
            if (transaction != null) {
                // Update existing transaction with the same ID
                val updatedTransaction = Transaction(
                    id = transaction!!.id, // Keep the original ID
                    title = title,
                    amount = amount,
                    category = category,
                    type = type,
                    date = date
                )
                viewModel.updateTransaction(updatedTransaction)
            } else {
                // Create new transaction only when not in edit mode
                val newTransaction = Transaction(
                    title = title,
                    amount = amount,
                    category = category,
                    type = type,
                    date = date
                )
                viewModel.saveTransaction(newTransaction)
            }
            
            // Switch to main thread for navigation
            requireActivity().runOnUiThread {
                findNavController().navigate(R.id.navigation_activity)
            }
        }
    }

    private fun populateFields() {
        transaction?.let { t ->
            binding.editTextTitle.setText(t.title)
            binding.editTextAmount.setText(t.amount.toString())
            binding.editTextDate.setText(dateFormat.format(t.date))
            
            // Set transaction type and update categories
            if (t.type == TransactionType.INCOME) {
                binding.btnIncome.isSelected = true
                binding.btnExpense.isSelected = false
                _currentCategories = incomeCategories
            } else {
                binding.btnExpense.isSelected = true
                binding.btnIncome.isSelected = false
                _currentCategories = expenseCategories
            }
            updateButtonStyles()
            updateCategorySpinner()
            
            // Set the selected category
            val index = _currentCategories.indexOf(t.category)
            if (index >= 0) {
                binding.spinnerCategory.setText(_currentCategories[index], false)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 