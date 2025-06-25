package com.example.trackerfinance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.trackerfinance.databinding.DialogBudgetBinding

class BudgetDialogFragment : DialogFragment() {
    private var _binding: DialogBudgetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        val activity = requireActivity() as MainActivity
        val currentBudget = activity.transactionRepository.getMonthlyBudget()
        binding.etBudget.setText(currentBudget.toString())

        binding.btnSave.setOnClickListener {
            val budget = binding.etBudget.text.toString().toDoubleOrNull() ?: 0.0
            activity.transactionRepository.setMonthlyBudget(budget)
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 