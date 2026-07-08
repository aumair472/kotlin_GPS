package com.geosnap

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.geosnap.core.designsystem.theme.GeoSnapTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single activity host. AppCompatActivity is used so AndroidX per-app locales apply correctly.
 * All UI is Compose; navigation is wired in [com.geosnap.core.navigation].
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            GeoSnapTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    GeoSnapApp()
                }
            }
        }
    }
}
