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
        composable<HomeRoute> { DiscoveryList() }
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