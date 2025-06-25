package com.example.trackerfinance.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.trackerfinance.MainActivity
import com.example.trackerfinance.R

object NotificationUtils {
    private const val CHANNEL_ID = "budget_alerts"
    private const val CHANNEL_NAME = "Budget Alerts"
    private const val CHANNEL_DESCRIPTION = "Notifications for budget alerts"
    
    private const val NOTIFICATION_ID_WARNING = 1
    private const val NOTIFICATION_ID_DANGER = 2

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showBudgetWarningNotification(
        context: Context,
        remainingBudget: Double,
        spendingPercentage: Int
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val formattedAmount = CurrencyUtils.formatAmount(context, remainingBudget)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_warning)
            .setContentTitle("Budget Warning")
            .setContentText("You've used $spendingPercentage% of your monthly budget. Only $formattedAmount remaining.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_WARNING, notification)
        } catch (e: SecurityException) {
            // Handle notification permission not granted
        }
    }

    fun showBudgetExceededNotification(
        context: Context,
        exceededAmount: Double
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val formattedAmount = CurrencyUtils.formatAmount(context, exceededAmount)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_warning)
            .setContentTitle("Budget Exceeded!")
            .setContentText("You've exceeded your monthly budget by $formattedAmount")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_DANGER, notification)
        } catch (e: SecurityException) {
            // Handle notification permission not granted
        }
    }
} 