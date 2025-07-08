package com.example.ezdrive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.ezdrive.navigation.AppNavigation
import com.example.ezdrive.service.SessionManager
import com.example.ezdrive.theme.EZDriveTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(this)

        setContent {
            EZDriveTheme {
                AppNavigation(sessionManager = sessionManager)
            }
        }
    }
}
