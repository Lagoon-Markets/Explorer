package lagoon.markets.explorer

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender
import lagoon.markets.explorer.ui.theme.LagoonMarketsTheme

val LocalActivityResultSender = staticCompositionLocalOf<ActivityResultSender> {
    error("No ActivityResultSender provided")
}


class MainActivity : ComponentActivity() {
    init {
        System.loadLibrary("explorer_native")
    }

    val rootActivity = this
    val sender = ActivityResultSender(rootActivity)
    val localActivity = staticCompositionLocalOf<Activity> {
        error("No Activity provided")
    }
    private val appStateViewModel: AppStateViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        enableEdgeToEdge()
        installSplashScreen()

        setContent {
            CompositionLocalProvider(
                LocalActivityResultSender provides sender,
                localActivity provides this
            ) {
                LagoonMarketsTheme {
                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                    ) { innerPadding ->
                        InitApp(
                            rootActivity,
                            appStateViewModel,
                            sender,
                            paddingValues = innerPadding
                        )
                    }
                }
            }
        }
    }

    // Called after onCreate() or when returning from background
    override fun onStart() {
        super.onStart()
    }

    // Called after onStart(), activity is now interactive
    override fun onResume() {
        super.onResume()
    }

    // Called when activity is partially obscured (e.g., new activity is pushed on top)
    override fun onPause() {
        super.onPause()
    }

    // Called when activity is no longer visible
    override fun onStop() {
        super.onStop()
    }

    // Called when activity is being restarted after being stopped
    override fun onRestart() {
        super.onRestart()
    }

    // Called before the activity is destroyed (e.g., back press, system kill)
    override fun onDestroy() {
        super.onDestroy()
    }

    // Called when the system is restoring state after process death
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }

    // Called when the system is saving state (e.g., before killing the app)
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }
}


