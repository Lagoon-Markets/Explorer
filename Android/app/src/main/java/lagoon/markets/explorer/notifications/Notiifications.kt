package lagoon.markets.explorer.notifications

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import lagoon.markets.explorer.DashboardRoute
import lagoon.markets.explorer.GradientButton
import lagoon.markets.explorer.LagoonMarketsLogo
import lagoon.markets.explorer.MainActivity
import lagoon.markets.explorer.NonPriorityButton
import lagoon.markets.explorer.TextPurpleMountainMajesty

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun AuthorizeNotifications(
    activity: MainActivity,
    navController: NavController,
) {

    val context = LocalContext.current
    val permission = Manifest.permission.POST_NOTIFICATIONS

    val permanentlyDeniedText =
        "You have permanently denied showing Notifications! \nGo to the settings of this app to enable notifications permission.\n\n Allow this app to receive notifications and live updates for your x402 subscriptions and running tasks.  \n\nOnly x402 services you have subscribed to can send you notifications and live updates!"
    val allowPermissionsText =
        "Allow this app to receive notifications and live updates for your x402 subscriptions and running tasks.  \n\nOnly x402 services you have subscribed to can send you notifications and live updates!"
    val allowButtonText = "Allow Notifications"

    val innerPermissionState = remember { mutableStateOf(InnerPermissionState.Denied) }
    val active = remember { mutableStateOf(false) }


    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (!isGranted) {
                if (shouldShowRequestPermissionRationale(activity, permission)) {
                    innerPermissionState.value = InnerPermissionState.ShowRationale
                } else {
                    innerPermissionState.value = InnerPermissionState.PermanentlyDenied
                }
            } else {
                navController.navigate(DashboardRoute)
            }

        }
    )

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (hasNotificationPermission(activity)) {
                    navController.navigate(DashboardRoute)
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    when (innerPermissionState.value) {
        InnerPermissionState.Denied -> {
            NotificationView(
                navController,
                contentText = allowPermissionsText,
                buttonText = "Allow Notifications",
                buttonCallback = {
                    permissionLauncher.launch(permission)
                }
            )
        }

        InnerPermissionState.PermanentlyDenied -> {
            NotificationView(
                navController,
                contentText = permanentlyDeniedText,
                buttonText = "App Settings",
                buttonCallback = {
                    val intent = Intent().apply {
                        action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    }
                    context.startActivity(intent)
                }
            )
        }

        InnerPermissionState.ShowRationale -> NotificationView(
            navController,
            contentText = allowPermissionsText,
            buttonText = "Allow Notifications",
            buttonCallback = {
                permissionLauncher.launch(permission)
            }
        )
    }

}

@Composable
fun NotificationView(
    navController: NavController,
    contentText: String,
    buttonText: String,
    buttonCallback: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(.9f)
        ) {
            LagoonMarketsLogo()
            Spacer(Modifier.height(50.dp))
            TextPurpleMountainMajesty(
                textContent = contentText,
                fontSize = 20.sp,
                textAlign = TextAlign.Left
            )
        }

        Row(
            modifier = Modifier
                .weight(.4f)
                .fillMaxWidth(.9f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            NonPriorityButton(
                callback = {
                    navController.navigate(DashboardRoute) {
                        popUpTo(DashboardRoute) { inclusive = true }
                    }
                },
                "Skip",
                fillMaxWidth = .25f
            )

            GradientButton(
                callback =
                    buttonCallback,
                buttonText,
                fillMaxWidth = 0.8f,
            )
        }

    }
}

enum class InnerPermissionState { Denied, PermanentlyDenied, ShowRationale }

fun hasNotificationPermission(context: Context): Boolean {
    return if (needsNotificationPermission()) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

    } else {
        true
    }
}

fun needsNotificationPermission(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
}


fun needsPromotedNotificationPermission(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA
}