package lagoon.markets.explorer.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import lagoon.markets.explorer.AppStateViewModel
import lagoon.markets.explorer.AppText
import lagoon.markets.explorer.R
import lagoon.markets.explorer.TextPurpleMountainMajesty
import lagoon.markets.explorer.ui.theme.PurpleMountainMajesty
import lagoon.markets.explorer.ui.theme.RussianViolet
import lagoon.markets.explorer.ui.theme.White


const val ROUTE_INBOX = "Inbox"
const val ROUTE_SCAN_QR = "Scan QR"

const val ROUTE_DISCOVER = "Discover"
const val ROUTE_SUBSCRIPTIONS = "Subscriptions"
const val ROUTE_PROFILE = "Profile"

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean,
    val badgeCount: Int? = null
)

@Composable
fun Dashboard(
    appStateViewModel: AppStateViewModel,
    navController: NavController
) {
    val dashboardNavController = rememberNavController()

    val items = listOf(
        BottomNavigationItem(
            title = ROUTE_INBOX,
            selectedIcon = ImageVector.vectorResource(R.drawable.inbox),
            unselectedIcon = ImageVector.vectorResource(R.drawable.inbox),
            hasNews = false,
        ),
        BottomNavigationItem(
            title = ROUTE_SCAN_QR,
            selectedIcon = ImageVector.vectorResource(R.drawable.qrcode_purple_mountain_magesty),
            unselectedIcon = ImageVector.vectorResource(R.drawable.qrcode_purple_mountain_magesty),
            hasNews = false,
        ),
        BottomNavigationItem(
            title = ROUTE_DISCOVER,
            selectedIcon = ImageVector.vectorResource(R.drawable.discover),
            unselectedIcon = ImageVector.vectorResource(R.drawable.discover),
            hasNews = false,
        ),
        BottomNavigationItem(
            title = ROUTE_SUBSCRIPTIONS,
            selectedIcon = ImageVector.vectorResource(R.drawable.subscribe),
            unselectedIcon = ImageVector.vectorResource(R.drawable.subscribe),
            hasNews = false,
        ),
        BottomNavigationItem(
            title = ROUTE_PROFILE,
            selectedIcon = ImageVector.vectorResource(R.drawable.profile_avatar_purple_mountain_majesty),
            unselectedIcon = ImageVector.vectorResource(R.drawable.profile_avatar_purple_mountain_majesty),
            hasNews = false,
        ),
    )

    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }

    val haptic = LocalHapticFeedback.current

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .padding(
                        bottom = WindowInsets.navigationBars
                            .asPaddingValues()
                            .calculateBottomPadding() + 0.dp
                    ),
                contentAlignment = Alignment.Center
            ) {
                NavigationBar(
                    modifier = Modifier
                        .fillMaxWidth(.95f)
                        .clip(RoundedCornerShape(30.dp))
                        .shadow(
                            8.dp,
                            shape = RoundedCornerShape(20.dp),
                            clip = true
                        )
                        .background(glassBrush),
//                        .background(brushDarkHorizontalGradient, shape = RoundedCornerShape(20.dp))
//                        .height(80.dp)
//                        .padding(4.dp),
                    containerColor = Color.Transparent,
                    windowInsets = WindowInsets(0)
                ) {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedItemIndex == index,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)

                                selectedItemIndex = index
                                dashboardNavController.navigate(item.title)
                            },
                            label = {
                                AppText(
                                    textContent = item.title,
                                    color = White,
                                    fontSize = 15.sp,
                                )
                            },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (item.badgeCount != null) {
                                            Badge {
                                                TextPurpleMountainMajesty(
                                                    textContent = item.badgeCount.toString(),
                                                    fontSize = 10.sp
                                                )
                                            }
                                        } else if (item.hasNews) {
                                            Badge()
                                        }
                                    }) {
                                    Icon(
                                        imageVector = if (index == selectedItemIndex) {
                                            item.selectedIcon
                                        } else {
                                            item.unselectedIcon
                                        },
                                        contentDescription = item.title,
                                        modifier = Modifier.size(30.dp),
                                        tint = PurpleMountainMajesty
                                    )
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
//                                selectedIconColor = Color.White,
//                                unselectedIconColor = Color.Gray,
//                                selectedTextColor = Color.White,
//                                unselectedTextColor = Color.Gray,
                                indicatorColor = RussianViolet
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->

        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .padding(padding),
        ) {
            NavHost(
                navController = dashboardNavController,
                startDestination = ROUTE_INBOX,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(ROUTE_INBOX) {
                    Inbox()
                }
                composable(ROUTE_SCAN_QR) { ScanQR() }
                composable(ROUTE_DISCOVER) {
                    Discover()
                }
                composable(ROUTE_SUBSCRIPTIONS) { Subscriptions() }
                composable(ROUTE_PROFILE) { Profile() }
            }
        }
    }
}

val glassBrush = Brush.linearGradient(
    colors = listOf(
        PurpleMountainMajesty.copy(alpha = 0.25f),
        PurpleMountainMajesty.copy(alpha = 0.1f)
    )
)


@Composable
fun Inbox() {
//    val context = LocalContext.current;
//
//    if (needsPromotedNotificationPermission()) {
//        checkInitialization(context)
//    } else {
//        appLog("LIVE UPDATES: NOT SUPPORTED")
//    }


    Column() { TextPurpleMountainMajesty(textContent = "Inbox") }

}

@Composable
fun Subscriptions() {
    TextPurpleMountainMajesty(textContent = "Subscriptions")
}

@Composable
fun ScanQR() {
    TextPurpleMountainMajesty(textContent = "Scan QR")
}

@Composable
fun Discover() {
    TextPurpleMountainMajesty(textContent = "Discover")
}

@Composable
fun Profile() {
    TextPurpleMountainMajesty(textContent = "Profile")
}
