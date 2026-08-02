package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.analytics.ClickstreamTracker
import com.example.model.UserRole
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.screens.ReadingMaterialScreen
import com.example.ui.theme.MyApplicationTheme

enum class ScreenState {
    LOGIN,
    READING,
    QUIZ,
    ADMIN_DASHBOARD
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val tracker = ClickstreamTracker.getInstance(applicationContext)

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PhotosynthesisApp(tracker = tracker)
                }
            }
        }
    }
}

@Composable
fun PhotosynthesisApp(tracker: ClickstreamTracker) {
    var currentScreen by remember { mutableStateOf(ScreenState.LOGIN) }

    Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
        when (screen) {
            ScreenState.LOGIN -> {
                LoginScreen { username, role ->
                    tracker.startSession(username, role)
                    currentScreen = if (role == UserRole.STUDENT) {
                        ScreenState.READING
                    } else {
                        ScreenState.ADMIN_DASHBOARD
                    }
                }
            }

            ScreenState.READING -> {
                ReadingMaterialScreen(
                    tracker = tracker,
                    onStartQuizClicked = {
                        currentScreen = ScreenState.QUIZ
                    },
                    onLogoutClicked = {
                        tracker.logEvent("LOGOUT", "reading_screen", metadata = "User logged out")
                        currentScreen = ScreenState.LOGIN
                    }
                )
            }

            ScreenState.QUIZ -> {
                QuizScreen(
                    tracker = tracker,
                    onQuizFinished = {
                        currentScreen = ScreenState.READING
                    },
                    onBackToReading = {
                        currentScreen = ScreenState.READING
                    }
                )
            }

            ScreenState.ADMIN_DASHBOARD -> {
                AdminDashboardScreen(
                    tracker = tracker,
                    onLogoutClicked = {
                        tracker.logEvent("LOGOUT", "admin_dashboard", metadata = "Admin logged out")
                        currentScreen = ScreenState.LOGIN
                    }
                )
            }
        }
    }
}
