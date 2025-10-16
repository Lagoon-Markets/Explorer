package lagoon.markets.explorer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable

@Serializable
object HomeRoute


@Composable
fun AppNavigation(
    appStateViewModel: AppStateViewModel,
) {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    navBackStackEntry?.destination?.route?.let { route ->
        appLog("Navigated to route: $route")
    }

    NavHost(navController = navController, startDestination = HomeRoute) {
        composable<HomeRoute> { CheckSiws() }
    }
}

