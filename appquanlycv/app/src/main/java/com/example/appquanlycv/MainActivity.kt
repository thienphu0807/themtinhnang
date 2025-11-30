package com.example.appquanlycv

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.appquanlycv.reminder.TaskReminderScheduler
import com.example.appquanlycv.ui.home.TaskScreen
import com.example.appquanlycv.ui.home.TaskViewModel
import com.example.appquanlycv.ui.home.TaskViewModelFactory
import com.example.appquanlycv.ui.splash.SplashScreen
import com.example.appquanlycv.ui.theme.AppquanlycvTheme

class MainActivity : ComponentActivity() {

    private val viewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(
            (application as TaskApplication).repository,
            TaskReminderScheduler(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermissionIfNeeded()

        setContent {
            AppquanlycvTheme {
                var showSplash by remember { mutableStateOf(true) }
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                if (showSplash) {
                    SplashScreen(
                        onFinish = { showSplash = false }
                    )
                } else {
                    TaskScreen(
                        uiState = uiState,
                        onAddTask = viewModel::addTask,
                        onSelectDate = viewModel::selectDate,
                        onToggleCompletion = viewModel::toggleCompletion,
                        onToggleReminder = viewModel::toggleReminder,
                        onDeleteTask = viewModel::deleteTask
                    )
                }
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST
                )
            }
        }
    }

    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST = 1001
    }
}
