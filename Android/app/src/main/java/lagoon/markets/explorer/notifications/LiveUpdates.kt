package lagoon.markets.explorer.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.toColorInt
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lagoon.markets.EventEmitterFfi
import lagoon.markets.EventListenerFfi
import lagoon.markets.EventSourceDataFfi
import lagoon.markets.explorer.R

@RequiresApi(Build.VERSION_CODES.BAKLAVA)
@Composable
fun LiveUpdates(eventsourceUri: Uri) {
    val notificationManager =
        LocalContext.current.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    SnackbarNotificationManager.initialize(
        LocalContext.current.applicationContext,
        notificationManager
    )
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        NotificationPermission()
        Spacer(modifier = Modifier.height(4.dp))
        NotificationPostPromotedPermission()
        Text(stringResource(R.string.live_update_summary_text))
        Spacer(modifier = Modifier.height(4.dp))
        Button(
            onClick = {
                onCheckout(eventsourceUri)
                scope.launch {
                    snackbarHostState.showSnackbar("Voting Started")
                }
            },
        ) {
            Text("Checkout")
        }
    }
}


@RequiresApi(Build.VERSION_CODES.BAKLAVA)
fun onCheckout(eventsourceUri: Uri) {
    SnackbarNotificationManager.start(eventsourceUri)
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NotificationPermission() {
    val notificationPermissionState = rememberPermissionState(
        android.Manifest.permission.POST_NOTIFICATIONS,
    )
    if (!notificationPermissionState.status.isGranted) {
        NotificationPermissionCard(
            shouldShowRationale = notificationPermissionState.status.shouldShowRationale,
            onGrantClick = {
                notificationPermissionState.launchPermissionRequest()
            },
            modifier = Modifier.fillMaxWidth(),
            permissionStringResourceId = R.string.permission_message,
            permissionRationalStringResourceId = R.string.permission_rationale,
        )
    }
}

@RequiresApi(Build.VERSION_CODES.BAKLAVA)
@Composable
fun NotificationPostPromotedPermission() {
    var isPostPromotionsEnabled by remember {
        mutableStateOf(SnackbarNotificationManager.isPostPromotionsEnabled())
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        isPostPromotionsEnabled = SnackbarNotificationManager.isPostPromotionsEnabled()
    }
    if (!isPostPromotionsEnabled) {
        Text(
            text = stringResource(R.string.post_promoted_permission_message),
            modifier = Modifier.padding(horizontal = 10.dp),
        )
        val context = LocalContext.current
        Button(
            onClick = {
                val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
                    Intent(Settings.ACTION_APP_NOTIFICATION_PROMOTION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    }
                } else {
                    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    }
                }
                try {
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    context.startActivity(
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                    )
                }
            }
        ) {
            Text(stringResource(R.string.to_settings))
        }
    }
}

@Composable
private fun NotificationPermissionCard(
    shouldShowRationale: Boolean,
    onGrantClick: () -> Unit,
    modifier: Modifier = Modifier,
    permissionStringResourceId: Int,
    permissionRationalStringResourceId: Int,
) {
    Card(
        modifier = modifier,
    ) {
        Text(
            text = stringResource(permissionStringResourceId),
            modifier = Modifier.padding(16.dp),
        )
        if (shouldShowRationale) {
            Text(
                text = stringResource(permissionRationalStringResourceId),
                modifier = Modifier.padding(horizontal = 10.dp),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            contentAlignment = Alignment.BottomEnd,
        ) {
            Button(onClick = onGrantClick) {
                Text(text = stringResource(R.string.permission_grant))
            }
        }
    }
}

object SnackbarNotificationManager {
    private lateinit var notificationManager: NotificationManager
    private lateinit var appContext: Context
    const val CHANNEL_ID = "live_updates_channel_id"
    private const val CHANNEL_NAME = "live_updates_channel_name"
    private const val NOTIFICATION_ID = 1234

    @RequiresApi(Build.VERSION_CODES.O)
    fun initialize(context: Context, notifManager: NotificationManager) {
        notificationManager = notifManager
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE_DEFAULT)
        appContext = context
        notificationManager.createNotificationChannel(channel)
    }

    @RequiresApi(Build.VERSION_CODES.BAKLAVA)
    fun buildNotification(
        appContext: Context, eventSourceData: EventSourceDataFfi
    ): NotificationCompat.Builder {
        val progressStyle = NotificationCompat.ProgressStyle()
            .setProgressPoints(
                eventSourceData.style.points.map {
                    NotificationCompat.ProgressStyle.Point(it.point).setColor(
                        Color(
                            it.color.toColorInt()
                        ).toArgb()
                    )
                }
            )
            .setProgressSegments(
                eventSourceData.style.segments.map {
                    NotificationCompat.ProgressStyle.Segment(it.segment).setColor(
                        Color(
                            it.color.toColorInt()
                        ).toArgb()
                    )
                }
            )

        // Updates the initial style
        progressStyle.setProgressPoints(
            listOf(
                NotificationCompat.ProgressStyle.Point(eventSourceData.progress.point).setColor(
                    Color(
                        eventSourceData.progress.color.toColorInt()
                    ).toArgb()
                )
            )
        )

        progressStyle.setProgressTrackerIcon(
            IconCompat.createWithResource(
                appContext,
                R.drawable.initial
            )
        )

        val builder = NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setOngoing(true)
            .setRequestPromotedOngoing(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(eventSourceData.contentTitle)
            .setContentText(eventSourceData.contentText)
            .setShortCriticalText(eventSourceData.shortCriticalText)
            .setLargeIcon(
                IconCompat.createWithResource(appContext, R.drawable.discover)
                    .toIcon(appContext)
            )


        if (eventSourceData.isProgressIndeterminate) {
            builder.setStyle(progressStyle.setProgressIndeterminate(true))
        } else {
            builder.setStyle(progressStyle.setProgress(eventSourceData.progress.point))
        }

        eventSourceData.actions.take(3).forEach { action ->
            builder
                .addAction(NotificationCompat.Action.Builder(null, action, null).build())
        }

        return builder
    }

    // ------------------ DYNAMIC STREAM LOGIC ------------------

    @RequiresApi(Build.VERSION_CODES.BAKLAVA)
    fun start(eventsourceUri: Uri) {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            getOrderUpdates(eventsourceUri).collect { state ->
                withContext(Dispatchers.Main) {
                    val notification = buildNotification(appContext, state).build()
                    notificationManager.notify(NOTIFICATION_ID, notification)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.BAKLAVA)
    fun getOrderUpdates(eventsourceUri: Uri): Flow<EventSourceDataFfi> =
        RustEventSourceFlow(eventsourceUri).asFlow()

    @RequiresApi(Build.VERSION_CODES.BAKLAVA)
    fun isPostPromotionsEnabled(): Boolean {
        return notificationManager.canPostPromotedNotifications()
    }
}

class RustEventSourceFlow(eventsourceUri: Uri) : EventListenerFfi {
    private val emitter = EventEmitterFfi()
    private val _flow = MutableSharedFlow<EventSourceDataFfi>()

    init {
        emitter.setListener(this)
        emitter.start(eventsourceUri.toString())
    }

    override fun onEvent(event: EventSourceDataFfi) {
        GlobalScope.launch {
            _flow.emit(event)
        }
    }

    fun asFlow(): Flow<EventSourceDataFfi> = _flow
}


fun decodeIcon(data: ByteArray): IconCompat {
    val bitmap = BitmapFactory.decodeByteArray(
        data,
        0,
        data.size
    )
    return IconCompat.createWithBitmap(bitmap)
}