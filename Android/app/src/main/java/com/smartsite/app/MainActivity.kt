package com.smartsite.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.smartsite.app.navigation.SmartSiteApp
import com.smartsite.app.ui.theme.SmartSiteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartSiteTheme {
                SmartSiteApp()
            }
        }
    }
}
