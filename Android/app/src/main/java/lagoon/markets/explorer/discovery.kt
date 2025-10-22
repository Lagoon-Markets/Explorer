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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import lagoon.markets.DiscoveryFfi
import lagoon.markets.X402UriSchemeFfi
import lagoon.markets.explorer.ui.theme.HanPurple
import lagoon.markets.explorer.ui.theme.Licorice
import lagoon.markets.explorer.ui.theme.PurpleMountainMajesty
import lagoon.markets.explorer.ui.theme.White
import lagoon.markets.explorer.ui.theme.brushDarkHorizontalGradient
import lagoon.markets.explorer.ui.theme.commitMonoFamily
import lagoon.markets.explorer.ui.theme.poppinsFamily
import lagoon.markets.explorer.ui.theme.smoochSansFamily
import lagoon.markets.rustffiShortenBase58


@Composable
fun DiscoveredList(navController: NavController, list: List<DiscoveryFfi>, x402CurrentUri: String) {
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
                DiscoveredItem(
                    navController, item
                )
                Spacer(Modifier.height(40.dp))
            }

        }
    }
}


@Composable
fun DiscoveredItem(
    navController: NavController,
    discoveryItem: DiscoveryFfi,
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
                navController.navigate(
                    discoveryFfiToDiscoveredItemRoute(discoveryItem)
                )
            }) {
        // Background image or fallback
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current).data(discoveryItem.headerImage)
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
                .clip(RoundedCornerShape(8.dp))
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
                .fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .padding(10.dp)
                    .fillMaxSize()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth(),
                ) {
                    Image(
                        painter = painterResource(getSchemeIcon(discoveryItem.uriScheme)),
                        contentDescription = getSchemeIconDescription(discoveryItem.uriScheme),
                        modifier = Modifier.width(20.dp)
                    )
                    Spacer(Modifier.width(5.dp))
                    TextWhite(
                        textContent = "${discoveryItem.uriScheme}",
                        fontFamily = smoochSansFamily,
                        fontSize = 25.sp
                    )
                }
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            AppText(
                                textContent = discoveryItem.title ?: "",
                                fontFamily = smoochSansFamily,
                                fontSize = 30.sp,
                                color = PurpleMountainMajesty
                            )
                        }
                        Box(modifier = Modifier.weight(.5f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                discoveryItem.assetInfo?.let {
                                    Box {
                                        Image(
                                            painter = rememberAsyncImagePainter(
                                                ImageRequest.Builder(LocalContext.current)
                                                    .data(it.logoUri)
                                                    .crossfade(true).build()
                                            ),
                                            contentDescription = null,
                                            contentScale = ContentScale.Fit,
                                            modifier = Modifier
                                                .width(20.dp)
                                                .background(Licorice) // fallback if image fails to load
                                        )
                                    }

                                    Spacer(Modifier.width(5.dp))
                                    Box {
                                        TextWhite(
                                            textContent = it.symbol,
                                            fontFamily = commitMonoFamily,
                                            fontSize = 15.sp,
                                            maxLines = 1
                                        )
                                    }
                                }


                            }
                        }
                    }
                    AppText(
                        textContent = discoveryItem.description ?: "",
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
fun DiscoveryItemView(
    navController: NavController, discoveryItem: DiscoveredItemRoute
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
                .verticalScroll(rememberScrollState())
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
                            .data(discoveryItem.headerImage).crossfade(true).build()
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(200.dp)
                        .background(
                            Licorice,
                        ) // fallback if image fails to load
                )
            }
            Spacer(Modifier.height(15.dp))
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth(.9f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(getSchemeIcon(discoveryItem.uriScheme)),
                        contentDescription = getSchemeIconDescription(discoveryItem.uriScheme),
                        modifier = Modifier.width(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    TextPurpleMountainMajesty(
                        textContent = getX402UriSchemeText(discoveryItem.uriScheme),
                        fontFamily = smoochSansFamily,
                        fontSize = 25.sp
                    )
                }
                Spacer(Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .background(
                            color = Licorice, shape = RoundedCornerShape(50.dp)
                        )
                        .padding(horizontal = 15.dp, vertical = 10.dp)
                        .fillMaxWidth()
                ) {

                    TextWhite(
                        textContent = discoveryItem.uri,
                        fontSize = 15.sp,
                        fontFamily = commitMonoFamily,
                        maxLines = 1
                    )
                }
            }

            Spacer(Modifier.height(30.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                AppText(
                    textContent = discoveryItem.title ?: "",
                    fontFamily = smoochSansFamily,
                    fontSize = 40.sp,
                    textAlign = TextAlign.Start,
                    color = White
                )
            }

            Spacer(Modifier.height(30.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                AppText(
                    textContent = discoveryItem.description ?: "",
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
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextPurpleMountainMajesty(
                    textContent = "Pay To:",
                    fontFamily = smoochSansFamily,
                    fontSize = 25.sp
                )
                Spacer(Modifier.width(10.dp))
                TextWhite(
                    textContent = rustffiShortenBase58(discoveryItem.payTo),
                    fontFamily = commitMonoFamily,
                    fontSize = 20.sp
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextPurpleMountainMajesty(
                    textContent = "Fee Payer:",
                    fontFamily = smoochSansFamily,
                    fontSize = 25.sp
                )
                Spacer(Modifier.width(10.dp))
                TextWhite(
                    textContent = if (discoveryItem.feePayer.isEmpty()) {
                        "Me"
                    } else {
                        rustffiShortenBase58(discoveryItem.feePayer)
                    },
                    fontFamily = commitMonoFamily,
                    fontSize = 20.sp
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
            ) {
                Box {
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(discoveryItem.logoUri)
                                .crossfade(true).build()
                        ),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .width(40.dp)
                            .background(Licorice) // fallback if image fails to load
                    )
                }

                Spacer(Modifier.width(5.dp))
                val amount =
                    "${discoveryItem.amount} ${discoveryItem.symbol ?: ""}"
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


fun getX402UriSchemeText(scheme: X402UriSchemeFfi): String {
    return when (scheme) {
        X402UriSchemeFfi.A2A -> {
            "x402://a2a"
        }

        X402UriSchemeFfi.MCP -> {
            "x402://mcp"
        }

        else -> {
            "x402://https"
        }
    }
}

fun getSchemeIcon(scheme: X402UriSchemeFfi): Int {
    return when (scheme) {
        X402UriSchemeFfi.A2A -> R.drawable.a2a
        X402UriSchemeFfi.MCP -> R.drawable.mcp
        X402UriSchemeFfi.HTTPS -> R.drawable.web
    }
}

fun getSchemeIconDescription(scheme: X402UriSchemeFfi): String {
    return when (scheme) {
        X402UriSchemeFfi.A2A -> "A2A icon"
        X402UriSchemeFfi.MCP -> "MCP icon"
        X402UriSchemeFfi.HTTPS -> "HTTPS icon"
    }
}

fun discoveryFfiToDiscoveredItemRoute(value: DiscoveryFfi): DiscoveredItemRoute {
    return DiscoveredItemRoute(
        uriScheme = value.uriScheme,
        uri = value.uri,
        title = value.title,
        description = value.description,
        headerImage = value.headerImage,
        amount = value.amount,
        asset = value.asset,
        payTo = value.payTo,
        feePayer = value.feePayer,
        address = value.assetInfo?.address,
        symbol = value.assetInfo?.symbol,
        name = value.assetInfo?.name,
        logoUri = value.assetInfo?.logoUri
    )
}