package lagoon.markets.explorer.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import lagoon.markets.X402Data
import lagoon.markets.explorer.AppLinearLoader
import lagoon.markets.explorer.GradientButton
import lagoon.markets.explorer.LagoonMarketsLogo
import lagoon.markets.explorer.R
import lagoon.markets.explorer.ShowAppError
import lagoon.markets.explorer.TextPurpleMountainMajesty
import lagoon.markets.explorer.bottomBorder
import lagoon.markets.explorer.ui.theme.poppinsFamily
import lagoon.markets.explorer.ui.theme.smoochSansFamily
import lagoon.markets.rustffiGetX402Resources

@Composable
fun Resources(
    navController: NavController,
    dashboardNavController: NavController,
) {
    val allResources = remember { mutableStateOf<List<X402Data>>(listOf()) }

    var loading by remember { mutableStateOf(true) }
    var errorExists by remember { mutableStateOf<Exception?>(null) }

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        try {
            allResources.value = rustffiGetX402Resources()
            loading = false
        } catch (error: Exception) {
            errorExists = error
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(.9f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextPurpleMountainMajesty(
                    textContent = "X402 Resources",
                    fontFamily = smoochSansFamily
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (loading) {
                LagoonMarketsLogo()
                Spacer(Modifier.height(50.dp))
                TextPurpleMountainMajesty(
                    textContent = "Checking x402 resource history",
                    fontFamily = smoochSansFamily
                )
                Spacer(Modifier.height(10.dp))
                AppLinearLoader()

            } else {
                if (allResources.value.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.baby_birds_singing_in_a_nest),
                            contentDescription = "Birds signing",
                            modifier = Modifier.size(300.dp)
                        )
                        Spacer(Modifier.height(20.dp))
                        TextPurpleMountainMajesty(
                            textContent = "No x402 resources you have interacted with!\n Just some birds singing...",
                            fontSize = 22.sp
                        )
                        Spacer(Modifier.height(50.dp))
                        GradientButton(
                            callback = {
                                coroutineScope.launch {
                                    dashboardNavController.navigate(ROUTE_DISCOVER)
                                }
                            },
                            textContent = "Discover Resources",
                            brush = null,
                        )
                    }
                } else {
                    for (resource in allResources.value) {
                        ResourceEntry(resource, navController)
                    }
                }
            }
        }
    }

    errorExists?.let { ShowAppError(it) }
}

@Composable
fun ResourceEntry(resource: X402Data, navController: NavController) {
    val haptic = LocalHapticFeedback.current

    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth(.9f)
            .padding(bottom = 20.dp)
            .background(Color.Transparent, shape = RoundedCornerShape(50.dp))
            .padding(start = 20.dp)
            .defaultMinSize(minHeight = 80.dp)
            .clickable(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
//                    navController.navigate()  TODO show resource
                }
            )
            .bottomBorder(thickness = 2.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
        ) {
            ResourceOverview(
                resource
            )
        }
    }
}

@Composable
fun ResourceOverview(
    resource: X402Data,
    fontFamily: FontFamily = poppinsFamily,
    fontSize: TextUnit = 22.sp,
    iconSize: Dp = 25.dp
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Spacer(Modifier.width(5.dp))
        TextPurpleMountainMajesty(
            textContent = resource.uri,
            fontSize = fontSize,
            fontFamily = fontFamily
        )
    }
}