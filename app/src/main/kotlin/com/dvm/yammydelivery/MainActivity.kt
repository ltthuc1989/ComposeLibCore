package com.dvm.yammydelivery

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.dvm.navigation.api.model.Destination
import com.dvm.notification.NotificationService.Companion.NOTIFICATION_EXTRA
import com.dvm.ui.YammyDeliveryScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class MainActivity : AppCompatActivity() {

    private val viewModel: NavigationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            YammyDeliveryScreen(this) {
                
                    val navController = rememberNavController()
                    viewModel.navController = navController
                    val startDestination = if (fromNotification(intent)) {
                        Destination.Main.route
                    } else {
                        Destination.Splash.route
                    }
                    NavHost(
                        navController = navController,
                        startDestination = startDestination
                    )
                
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (fromNotification(intent)) {
            viewModel.navigateToNotification()
            intent?.removeExtra(NOTIFICATION_EXTRA)
        }
    }

    private fun fromNotification(intent: Intent?): Boolean =
        intent?.hasExtra(NOTIFICATION_EXTRA) == true
}