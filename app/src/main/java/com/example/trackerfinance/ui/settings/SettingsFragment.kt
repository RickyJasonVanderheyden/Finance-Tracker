package com.example.trackerfinance.ui.settings

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.trackerfinance.MainActivity
import com.example.trackerfinance.MainViewModel
import com.example.trackerfinance.databinding.FragmentSettingsBinding
import com.example.trackerfinance.utils.CurrencyUtils
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private lateinit var viewModel: MainViewModel
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        private const val PREFS_NAME = "TrackerFinancePrefs"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_DARK_MODE = "dark_mode"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        setupUI()
        loadSavedCurrencyFormat()
        loadNotificationPreference()
        loadDarkModePreference()
        showDataStorageLocation()
    }

    private fun setupUI() {
        binding.buttonSavePreferences.setOnClickListener {
            saveCurrencyFormat()
        }

        binding.buttonExportData.setOnClickListener {
            exportData()
        }

        binding.buttonImportData.setOnClickListener {
            showImportConfirmation()
        }

        // Setup notification toggle
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            saveNotificationPreference(isChecked)
            if (isChecked) {
                Toast.makeText(context, "Notifications enabled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Notifications disabled", Toast.LENGTH_SHORT).show()
            }
        }

        // Setup dark mode toggle
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            saveDarkModePreference(isChecked)
            applyDarkMode(isChecked)
        }
    }

    private fun loadSavedCurrencyFormat() {
        val savedFormat = CurrencyUtils.getCurrencySymbol(requireContext())
        binding.editTextCurrency.setText(savedFormat)
    }

    private fun loadNotificationPreference() {
        val notificationsEnabled = sharedPreferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
        binding.switchNotifications.isChecked = notificationsEnabled
    }

    private fun loadDarkModePreference() {
        val isDarkMode = sharedPreferences.getBoolean(KEY_DARK_MODE, false)
        binding.switchDarkMode.isChecked = isDarkMode
    }

    private fun saveNotificationPreference(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
    }

    private fun saveDarkModePreference(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    }

    private fun applyDarkMode(enabled: Boolean) {
        if (enabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun saveCurrencyFormat() {
        val currencyFormat = binding.editTextCurrency.text.toString()
        if (currencyFormat.isBlank()) {
            Toast.makeText(context, "Currency format cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        CurrencyUtils.saveCurrencyFormat(requireContext(), currencyFormat)
        Toast.makeText(context, "Currency format saved", Toast.LENGTH_SHORT).show()
        
        // Refresh the main activity to update all displayed amounts
        (activity as? MainActivity)?.refreshDisplayedAmounts()
    }

    private fun showDataStorageLocation() {
        val dataDir = requireContext().filesDir.absolutePath
        binding.textViewDataPath.text = dataDir
    }

    private fun exportData() {
        try {
            val monthlyBudget = viewModel.getMonthlyBudget()
            
            // Observe transactions and export when available
            viewModel.allTransactions.observe(viewLifecycleOwner) { transactions ->
                val backupData = BackupData(
                    transactions = transactions,
                    monthlyBudget = monthlyBudget
                )
                
                val json = gson.toJson(backupData)
                val backupFile = File(requireContext().filesDir, "tracker_finance_backup.json")
                
                FileWriter(backupFile).use { writer ->
                    writer.write(json)
                }
                
                Toast.makeText(
                    context,
                    "Data exported successfully to: ${backupFile.absolutePath}",
                    Toast.LENGTH_LONG
                ).show()
            }
            
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Failed to export data: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun showImportConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Import Data")
            .setMessage("This will replace all current data. Are you sure you want to proceed?")
            .setPositiveButton("Import") { _, _ -> importData() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun importData() {
        try {
            val backupFile = File(requireContext().filesDir, "tracker_finance_backup.json")
            if (!backupFile.exists()) {
                Toast.makeText(context, "No backup file found", Toast.LENGTH_LONG).show()
                return
            }

            val json = FileReader(backupFile).use { reader ->
                reader.readText()
            }

            val backupData = gson.fromJson(json, BackupData::class.java)
            
            // Use coroutine to handle database operations
            CoroutineScope(Dispatchers.IO).launch {
                // Clear existing data and import new data
                backupData.transactions.forEach { transaction ->
                    viewModel.saveTransaction(transaction)
                }
                viewModel.setMonthlyBudget(backupData.monthlyBudget)
                
                // Switch to main thread for UI updates
                requireActivity().runOnUiThread {
                    Toast.makeText(context, "Data imported successfully", Toast.LENGTH_LONG).show()
                    (activity as? MainActivity)?.refreshDisplayedAmounts()
                }
            }
            
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Failed to import data: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    data class BackupData(
        val transactions: List<com.example.trackerfinance.data.Transaction>,
        val monthlyBudget: Double
    )
} 