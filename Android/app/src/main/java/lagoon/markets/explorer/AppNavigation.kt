package lagoon.markets.explorer

import android.content.Intent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
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
import lagoon.markets.explorer.ui.theme.commitMonoFamily
import lagoon.markets.explorer.x402_handlers.HandleDiscoveryRoute

@Serializable
object HomeRoute

@Serializable
data class DiscoveredItemRoute(
    var uriScheme: X402UriSchemeFfi,
    var uri: kotlin.String,
    var title: kotlin.String?,
    var description: kotlin.String?,
    var headerImage: kotlin.String?,
    var amount: kotlin.String,
    var asset: kotlin.String,
    var payTo: kotlin.String,
    var feePayer: kotlin.String,
    var address: kotlin.String?,
    var symbol: kotlin.String?,
    var name: kotlin.String?,
    var logoUri: kotlin.String?
)

@Composable
fun AppNavigation(
    appStateViewModel: AppStateViewModel,
    paddingValues: PaddingValues,
) {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    navBackStackEntry?.destination?.route?.let { route ->
        appLog("Navigated to route: $route")
    }

    NavHost(navController = navController, startDestination = HomeRoute) {
        composable<HomeRoute> {
            Home()
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

@Composable
fun Home() {
    val foo = "x402:// https://lagoon.markets"

    TextPurpleMountainMajesty(
        textContent = foo,
        fontWeight = FontWeight.Bold,
        fontFamily = commitMonoFamily, fontSize = 15.sp
    )
}