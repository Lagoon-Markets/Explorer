package lagoon.markets.explorer

import android.content.Intent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
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
import lagoon.markets.explorer.ui.theme.commitMonoFamily

@Serializable
object HomeRoute

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
            val x402CurrentUri = "https://lagoon.markets"
            val fooBgImage = "https://lagoon.markets/typewriter.jpg"
            val bar = "Convert newsletter to audio using an AI agent and download locally!"

            val itemList = listOf(
                DiscoveryItem(
                    backgroundImage = "https://lagoon.markets/typewriter.jpg",
                    actionType = "Http",
                    actionTypeIcon = R.drawable.ic_launcher_background,
                    actionTypeIconDescription = "Http Action Icon",
                    actionTitle = "Latest Newsletter",
                    actionDescription = "Get latest insights on onchain activity and developer productivity",
                    paymentCoinIcon = R.drawable.ic_launcher_background,
                    paymentCoinIconDescription = "USDC",
                    paymentCoinAmount = "1.5",
                    x402UriScheme = X402UriScheme.Https,
                    x402Uri = "https://lagoon.markets/latest_newsletter"
                ),
                DiscoveryItem(
                    backgroundImage = "https://lagoon.markets/typewriter.jpg",
                    actionType = "Agent",
                    actionTypeIcon = R.drawable.ic_launcher_background,
                    actionTypeIconDescription = "Agent Action Icon",
                    actionTitle = "Audio Newsletter",
                    actionDescription = "Listen to newsletter as audio by converting it to audio using an AI agent",
                    paymentCoinIcon = R.drawable.ic_launcher_background,
                    paymentCoinIconDescription = "USDC",
                    paymentCoinAmount = "1.5",
                    x402UriScheme = X402UriScheme.Https,
                    x402Uri = "https://lagoon.markets/latest_newsletter"
                )
            )

            DiscoveryList(navController, itemList, x402CurrentUri)
        }
        composable(
            route = "discover/{id}",
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "x402://discover/{id}"
                    action = Intent.ACTION_VIEW
                }
            ),
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            Text("GOT ROUTE: $id")
        }
        composable<DiscoveryItem> { backStackEntry ->
            val item: DiscoveryItem = backStackEntry.toRoute()
            DiscoveryItemView(
                navController,
                discoveryItem = item
            )
        }
    }
}

@Composable
fun Home() {
    val foo =
        "x402:// https://lagoon.markets"

    TextPurpleMountainMajesty(
        textContent = foo,
        fontWeight = FontWeight.Bold,
        fontFamily = commitMonoFamily, fontSize = 15.sp
    )
}