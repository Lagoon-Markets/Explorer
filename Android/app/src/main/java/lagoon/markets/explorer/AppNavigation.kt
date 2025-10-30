package lagoon.markets.explorer

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import lagoon.markets.X402UriSchemeFfi
import lagoon.markets.explorer.auth.SiwsSignup
import lagoon.markets.explorer.dashboard.Dashboard
import lagoon.markets.explorer.notifications.AuthorizeNotifications
import lagoon.markets.explorer.x402_handlers.HandleDiscoveryRoute

@Serializable
object CheckProfileInitializedRoute

@Serializable
object AuthorizeNotificationsRoute

@Serializable
object DashboardRoute

@Serializable
object OnboardingRoute

@Serializable
object SignUpRoute

@Serializable
data class DiscoveredItemRoute(
    var uriScheme: X402UriSchemeFfi,
    var uri: String,
    var title: String?,
    var description: String?,
    var headerImage: String?,
    var amount: String,
    var asset: String,
    var payTo: String,
    var feePayer: String,
    var address: String?,
    var symbol: String?,
    var name: String?,
    var logoUri: String?,
    val maxTimeoutSeconds: String?,
    val decimals: Int?,
)

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun AppNavigation(
    activity: MainActivity,
    appStateViewModel: AppStateViewModel,
) {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
//    navBackStackEntry?.destination?.route?.let { route ->
//        appLog("Navigated to route: $route")
//    }

    NavHost(navController = navController, startDestination = CheckProfileInitializedRoute) {
        composable<CheckProfileInitializedRoute> {
            CheckProfileInitialized(activity, navController)
        }
        composable<OnboardingRoute> {
            OnboardingView(navController)
        }
        composable<SignUpRoute> {
            SiwsSignup(navController)
        }
        composable<AuthorizeNotificationsRoute> {
            AuthorizeNotifications(activity, navController)
        }

        composable<DashboardRoute> {
            Dashboard(
                appStateViewModel = appStateViewModel,
                navController = navController
            )
        }

        composable(
            route = "discover/{discovery_route}",
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "x402://discover/{discovery_route}"
                    action = Intent.ACTION_VIEW
                }
            ),
            arguments = listOf(
                navArgument("discovery_route") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val discovery_route = backStackEntry.arguments?.getString("discovery_route")
            HandleDiscoveryRoute(navController, discovery_route)
        }
        composable<DiscoveredItemRoute> { backStackEntry ->
            val item: DiscoveredItemRoute = backStackEntry.toRoute()
            DiscoveryItemView(navController, item)
        }
    }
}

