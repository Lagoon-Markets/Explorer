package lagoon.markets.explorer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import kotlinx.serialization.Serializable
import lagoon.markets.explorer.ui.theme.HanPurple
import lagoon.markets.explorer.ui.theme.Licorice
import lagoon.markets.explorer.ui.theme.PurpleMountainMajesty
import lagoon.markets.explorer.ui.theme.White
import lagoon.markets.explorer.ui.theme.brushDarkHorizontalGradient
import lagoon.markets.explorer.ui.theme.commitMonoFamily
import lagoon.markets.explorer.ui.theme.poppinsFamily
import lagoon.markets.explorer.ui.theme.smoochSansFamily

@Composable
fun DiscoveryList(navController: NavController, list: List<DiscoveryItem>, x402CurrentUri: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()
    ) {
        X402RouteBar(x402CurrentUri)

        Spacer(Modifier.height(50.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())

        ) {

            list.forEach { item ->
                DiscoveryItemList(
                    item, navController
                )
                Spacer(Modifier.height(40.dp))
            }

        }
    }
}

@Composable
fun X402RouteBar(x402CurrentUri: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth(.95f)
            .padding(horizontal = 20.dp)
            .background(color = Licorice, shape = RoundedCornerShape(50.dp))
    ) {
        val x402CurrentUriTrunked = if (x402CurrentUri.length > 55) {
            x402CurrentUri.take(55) + "…"
        } else {
            x402CurrentUri
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            Box(
                contentAlignment = Alignment.Center, modifier = Modifier
                    .background(
                        color = HanPurple, shape = RoundedCornerShape(
                            topStart = 50.dp,
                            bottomStart = 50.dp,
                            topEnd = 0.dp,
                            bottomEnd = 0.dp
                        )
                    )
                    .padding(top = 10.dp, bottom = 10.dp, start = 10.dp, end = 5.dp)
            ) {
                AppText(
                    textContent = "x402://discover",
                    fontSize = 12.sp,
                    fontFamily = commitMonoFamily
                )
            }
            Box(
                contentAlignment = Alignment.Center, modifier = Modifier.padding(10.dp)
            ) {
                AppText(
                    textContent = x402CurrentUriTrunked,
                    fontSize = 12.sp,
                    fontFamily = commitMonoFamily,
                    textAlign = TextAlign.Start,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun DiscoveryItemList(
    discoveryItem: DiscoveryItem, navController: NavController
) {
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(250.dp)
            .clip(RoundedCornerShape(25.dp))
            .border(
                width = 2.dp, brush = brushDarkHorizontalGradient, shape = RoundedCornerShape(25.dp)
            )
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                navController.navigate(discoveryItem)
            }) {
        // Background image or fallback
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current).data(discoveryItem.backgroundImage)
                    .crossfade(true).build()
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .matchParentSize()
                .background(Licorice) // fallback if image fails to load
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            HanPurple.copy(alpha = .2f),
                            HanPurple.copy(alpha = .1f),
                            Licorice.copy(alpha = .8f),
                            Licorice.copy(alpha = 1f),
                        )
                    )
                )
        )

        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .matchParentSize()
                .padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(5.dp),
            ) {
                Image(
                    painter = painterResource(discoveryItem.actionTypeIcon),
                    contentDescription = discoveryItem.actionTypeIconDescription,
                    modifier = Modifier.width(20.dp)
                )
                Spacer(Modifier.width(5.dp))
                TextWhite(
                    textContent = discoveryItem.actionType,
                    fontFamily = smoochSansFamily,
                    fontSize = 25.sp
                )
            }
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(10.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        AppText(
                            textContent = discoveryItem.actionTitle,
                            fontFamily = smoochSansFamily,
                            fontSize = 30.sp,
                            color = PurpleMountainMajesty
                        )
                    }
                    Box(modifier = Modifier.weight(.4f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box {
                                Image(
                                    painter = painterResource(discoveryItem.paymentCoinIcon),
                                    contentDescription = discoveryItem.paymentCoinIconDescription,
                                    modifier = Modifier.width(25.dp)
                                )
                            }
                            Spacer(Modifier.width(5.dp))
                            Box {
                                TextWhite(
                                    textContent = discoveryItem.paymentCoinAmount,
                                    fontFamily = commitMonoFamily,
                                    fontSize = 20.sp
                                )
                            }
                        }
                    }
                }
                AppText(
                    textContent = discoveryItem.actionDescription,
                    fontFamily = poppinsFamily,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Start,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.Thin
                )
            }
        }
    }
}

@Composable
fun DiscoveryItemView(
    navController: NavController, discoveryItem: DiscoveryItem
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(.9f)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Licorice, shape = RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(25.dp))
                    .border(
                        width = 2.dp,
                        brush = brushDarkHorizontalGradient,
                        shape = RoundedCornerShape(25.dp)
                    )
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(discoveryItem.backgroundImage).crossfade(true).build()
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(200.dp)
                        .clip(RoundedCornerShape(20.dp))  // this enforces rounded corners
                        .background(
                            Licorice,
                        ) // fallback if image fails to load
                )
            }
            Spacer(Modifier.height(15.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth(.9f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.weight(1f)
                ) {
                    Image(
                        painter = painterResource(discoveryItem.actionTypeIcon),
                        contentDescription = discoveryItem.actionTypeIconDescription,
                        modifier = Modifier.width(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    TextPurpleMountainMajesty(
                        textContent = discoveryItem.actionType,
                        fontFamily = smoochSansFamily,
                        fontSize = 25.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .background(
                            color = Licorice, shape = RoundedCornerShape(50.dp)
                        )
                        .padding(horizontal = 15.dp, vertical = 5.dp)
                        .weight(1f)
                ) {
                    val uriSchemeText = getX402UriSchemeText(discoveryItem.x402UriScheme)
                    TextWhite(textContent = uriSchemeText, fontSize = 20.sp)
                }
            }

            Spacer(Modifier.height(30.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                AppText(
                    textContent = discoveryItem.actionTitle,
                    fontFamily = smoochSansFamily,
                    fontSize = 40.sp,
                    textAlign = TextAlign.Start,
                    color = White
                )
            }

            Spacer(Modifier.height(30.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                AppText(
                    textContent = discoveryItem.actionDescription,
                    fontFamily = poppinsFamily,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Start,
                    lineHeight = 22.sp,
                    color = PurpleMountainMajesty
                )
            }

        }

        Column(
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth(.8f)
                .weight(.5f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Image(
                    painter = painterResource(discoveryItem.paymentCoinIcon),
                    contentDescription = discoveryItem.paymentCoinIconDescription,
                    modifier = Modifier.width(30.dp)
                )
                Spacer(Modifier.width(5.dp))
                val amount =
                    "${discoveryItem.paymentCoinAmount} ${discoveryItem.paymentCoinIconDescription}"
                AppText(
                    textContent = amount,
                    fontFamily = commitMonoFamily,
                    fontSize = 25.sp
                )

            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                ProgressGradientButton(
                    callback = {},
                    textContent = "Pay with Solana",
                    fillMaxWidth = 1f
                )
            }
        }
    }

    //    var showSheet by remember { mutableStateOf(false) }
//    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
//
//    // Modal sheet
//    if (showSheet.value) {
//        ModalBottomSheet(
//            onDismissRequest = { showSheet.value = false },
//            sheetState = sheetState
//        ) {
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(24.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                TextPurpleMountainMajesty(textContent = "This is a modal bottom sheet")
//                Spacer(Modifier.height(16.dp))
//                Button(onClick = { showSheet.value = false }) {
//                    TextPurpleMountainMajesty(textContent = "Close")
//                }
//            }
//        }
//    }
}


@Preview
@Composable
fun DiscoveryItemPreview() {
    val navController = rememberNavController()

    DiscoveryItemView(
        navController, DiscoveryItem(
            backgroundImage = "https://lagoon.markets/typewriter.jpg",
            actionType = "Agent",
            actionTypeIcon = R.drawable.ic_launcher_background,
            actionTypeIconDescription = "Agent Action Icon",
            actionTitle = "Audio Newsletter",
            actionDescription = "Listen to newsletter as audio by converting it to audio using an AI agent",
            paymentCoinIcon = R.drawable.ic_launcher_background,
            paymentCoinIconDescription = "USDC",
            paymentCoinAmount = "1.5",
            x402UriScheme = X402UriScheme.A2a,
            x402Uri = "a2a:lagoon.markets/a2a/convert/latest_newsletter"
        )
    )
}

@Serializable
data class DiscoveryItem(
    val backgroundImage: String,
    val actionType: String,
    val actionTypeIcon: Int,
    val actionTypeIconDescription: String,
    val actionTitle: String,
    val actionDescription: String,
    val paymentCoinIcon: Int,
    val paymentCoinIconDescription: String,
    val paymentCoinAmount: String,
    val x402UriScheme: X402UriScheme,
    val x402Uri: String
)

enum class X402UriScheme {
    Https,
    A2a,
    Mcp
}

fun getX402UriSchemeText(scheme: X402UriScheme): String {
    return when (scheme) {
        X402UriScheme.A2a -> {
            "x402://a2a"
        }

        X402UriScheme.Mcp -> {
            "x402://mcp"
        }

        else -> {
            "x402://https"
        }
    }
}