package lagoon.markets.explorer.x402_handlers

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import lagoon.markets.DiscoveryFfi
import lagoon.markets.explorer.AppLinearLoader
import lagoon.markets.explorer.DiscoveredList
import lagoon.markets.explorer.GradientButton
import lagoon.markets.explorer.LagoonMarketsLogo
import lagoon.markets.explorer.TextWhite
import lagoon.markets.explorer.X402RouteBar
import lagoon.markets.explorer.appLog
import lagoon.markets.explorer.ui.theme.poppinsFamily
import lagoon.markets.rustffiDiscoverResources

@Composable
fun HandleDiscoveryRoute(navController: NavController, x402Path: String?) {
    x402Path?.let { it ->
        PerformDiscovery(navController, it)
    } ?: @androidx.compose.runtime.Composable {
        ShowMissingRoute()
    }
}

@Composable
fun PerformDiscovery(navController: NavController, x402Path: String) {
    val outcome = remember { mutableStateOf<List<DiscoveryFfi>?>(null) }
    appLog("Resources Path: $x402Path")

    LaunchedEffect(Unit) {

        try {
            outcome.value = rustffiDiscoverResources(x402Path)
            appLog("Fetched resources: ${outcome.value} ")
        } catch (error: Exception) {
            appLog("Error discovering resources: ${error.toString()}")
        }

    }

    outcome.value?.let {
        DiscoveredList(
            navController,
            list = it,
            x402CurrentUri = x402Path
        )
    } ?: Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        X402RouteBar(x402Path)
        Spacer(Modifier.height(20.dp))
        AppLinearLoader()
    }
}

@Composable
fun ShowMissingRoute() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            LagoonMarketsLogo()
            Spacer(Modifier.height(50.dp))
        }
        Column(
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth(.9f)
            ) {
                TextWhite(
                    textContent = "The `x402://discover` route was loaded successfully but there was no more data beyond that! The problem stems from the link you clicked. That link is incorrect!",
                    textAlign = TextAlign.Left,
                    fontFamily = poppinsFamily,
                    fontSize = 15.sp
                )
            }
            Spacer(Modifier.height(30.dp))
            val context = LocalContext.current
            val activity = context as? Activity

            GradientButton(
                textContent = "I Understand",
                callback = {
                    activity?.finish()
                }
            )
        }
    }
}
