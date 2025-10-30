package lagoon.markets.explorer

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender
import com.solana.mobilewalletadapter.clientlib.ConnectionIdentity
import com.solana.mobilewalletadapter.clientlib.MobileWalletAdapter
import com.solana.mobilewalletadapter.clientlib.TransactionResult
import com.solana.mobilewalletadapter.clientlib.successPayload
import kotlinx.coroutines.launch
import lagoon.markets.AppDetailsFfi
import lagoon.markets.DiscoveryFfi
import lagoon.markets.X402UriSchemeFfi
import lagoon.markets.explorer.notifications.SnackbarNotificationManager
import lagoon.markets.explorer.notifications.needsPromotedNotificationPermission
import lagoon.markets.explorer.notifications.onCheckout
import lagoon.markets.explorer.ui.theme.HanPurple
import lagoon.markets.explorer.ui.theme.Licorice
import lagoon.markets.explorer.ui.theme.PurpleMountainMajesty
import lagoon.markets.explorer.ui.theme.White
import lagoon.markets.explorer.ui.theme.brushDarkHorizontalGradient
import lagoon.markets.explorer.ui.theme.commitMonoFamily
import lagoon.markets.explorer.ui.theme.poppinsFamily
import lagoon.markets.explorer.ui.theme.smoochSansFamily
import lagoon.markets.rustffiConstructTx
import lagoon.markets.rustffiFormatAmount
import lagoon.markets.rustffiOptimizeTransaction
import lagoon.markets.rustffiSendOptimizedTransaction
import lagoon.markets.rustffiShortenBase58
import lagoon.markets.rustffiToBase64


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
                                            textContent = rustffiFormatAmount(
                                                discoveryItem.amount,
                                                discoveryItem.assetInfo?.decimals ?: 0.toUByte()
                                            ),
                                            fontFamily = commitMonoFamily,
                                            fontSize = 15.sp,
                                            maxLines = 1
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

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(.5f)
        ) {
            if (discoveryItem.uriScheme == X402UriSchemeFfi.A2A) {
                LiveUpdates(
                    navController, discoveryItem
                )
            } else {
                PayableResource(
                    navController, discoveryItem
                )
            }
        }
    }


}

@Composable
fun LiveUpdates(navController: NavController, discoveryItem: DiscoveredItemRoute) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (needsPromotedNotificationPermission()) {
            val notificationManager =
                LocalContext.current.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            SnackbarNotificationManager.initialize(
                LocalContext.current.applicationContext,
                notificationManager
            )
            val scope = rememberCoroutineScope()
            val snackbarHostState = remember { SnackbarHostState() }


            ProgressGradientButton(
                callback = {
                    onCheckout(eventsourceUri = discoveryItem.uri.toUri())
                    scope.launch {
                        snackbarHostState.showSnackbar("Voting Started")
                    }
                },
                textContent = "View live updates"
            )
        } else {
            TextPurpleMountainMajesty(
                maxLines = Int.MAX_VALUE,
                textContent = "This feature requires Android 16 to work. It shows how AI agents can use the live updates in Android 16+ to stream updates of long running tasks to a user's device. Keeping them informed!"
            )
        }
    }
}

@Composable
fun PayableResource(navController: NavController, discoveryItem: DiscoveredItemRoute) {
    // `this` is the current Android activity
    val appDetails = AppDetailsFfi()
    var optimizeTransaction by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val sender = LocalActivityResultSender.current
    val solanaUri = appDetails.domain().toUri()
    val iconUri = appDetails.favicon().toUri()
    val identityName = appDetails.identity()


    val showSheet = remember { mutableStateOf(false) }
    val success = remember { mutableStateOf<String?>(null) }

    val errorExists = remember { mutableStateOf<String?>(null) }
    val buttonEnabled = remember { mutableStateOf(true) }

    // Construct the client
    val walletAdapter = MobileWalletAdapter(
        connectionIdentity = ConnectionIdentity(
            identityUri = solanaUri,
            iconUri = iconUri,
            identityName = identityName
        )
    )


    Column(
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth(.8f)
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
                rustffiFormatAmount(discoveryItem.amount, discoveryItem.decimals?.toUByte())
            AppText(
                textContent = amount,
                fontFamily = commitMonoFamily,
                fontSize = 25.sp
            )

        }


        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = optimizeTransaction,
                onCheckedChange = { optimizeTransaction = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = HanPurple,          // fill when checked
                    uncheckedColor = HanPurple,         // border when unchecked
                    checkmarkColor = White,        // tick color
                    disabledCheckedColor = Color.LightGray,
                    disabledUncheckedColor = Color.DarkGray
                )
            )
            TextPurpleMountainMajesty(textContent = "Optimize Transaction", fontSize = 16.sp)
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            ProgressGradientButton(
                enabled = buttonEnabled.value,
                callback = {
                    buttonEnabled.value = false
                    coroutineScope.launch {
                        try {
                            val resourceDetails = DiscoveryFfi(
                                uriScheme = discoveryItem.uriScheme,
                                uri = discoveryItem.uri,
                                title = discoveryItem.title,
                                description = discoveryItem.description,
                                headerImage = discoveryItem.headerImage,
                                amount = discoveryItem.amount,
                                asset = discoveryItem.asset,
                                payTo = discoveryItem.payTo,
                                maxtimeoutSeconds = discoveryItem.maxTimeoutSeconds ?: "",
                                feePayer = discoveryItem.feePayer,
                                assetInfo = null
                            )
                            success.value = signTx(
                                resourceDetails,
                                optimizeTransaction,
                                walletAdapter,
                                sender
                            )
                        } catch (error: Exception) {
                            appLog("OPTIMIZED TX ERROR: ${error.toString()}")

                            errorExists.value =
                                "Optimize transaction only works for Mainnet accounts that exist (are rent exempt). Error details: " + error.message
                            showSheet.value = true
                        }
                    }
                },
                textContent = "Pay with Solana",
                fillMaxWidth = 1f
            )
        }
    }

    if (showSheet.value) {
        ErrorBottomSheet(errorExists, showSheet, { navController.navigate(DashboardRoute) })
    }
    if (success.value != null) {
        appLog("SUCCESS VALUE: ${success}")

        TxSuccessBottomSheet(
            success = success.value!!,
            { navController.navigate(DashboardRoute) })

    }
}

suspend fun signTx(
    resourceData: DiscoveryFfi,
    isTxOptimized: Boolean,
    walletAdapter: MobileWalletAdapter,
    sender: ActivityResultSender
): String {

    if (isTxOptimized) {
        appLog("OPTIMIZED TX")

        val optimizedTxResult = rustffiOptimizeTransaction(resourceData, mainnet = true)

        try {
            val optimizedTx = optimizedTxResult
            appLog("OPTIMIZED TX SUCCESS: ${optimizedTx}")

            val result = walletAdapter.transact(sender) { signTransactions(arrayOf(optimizedTx)) }
            return when (result) {
                is TransactionResult.Success -> {
                    appLog("Signing success")

                    val signedTxBytes = result.successPayload?.signedPayloads?.first()
                    if (signedTxBytes != null) {
                        try {
                            val success = rustffiSendOptimizedTransaction(signedTxBytes)

                            success
                        } catch (
                            error: Exception
                        ) {
                            throw error
                        }
                    } else {
                        throw Exception("Signed payload missing")
                    }
                }

                is TransactionResult.NoWalletFound ->
                    throw Exception("No MWA compatible wallet app found on device.")

                is TransactionResult.Failure ->
                    throw Exception("Error during transaction signing: ${result.e.message ?: "Unknown error"}")
            }
        } catch (error: Exception) {
            throw Exception(error.message)
        }
    } else {
        val tx = rustffiConstructTx(resourceData, mainnet = true)
        val result = walletAdapter.transact(sender) {     // Issue a 'signTransactions' request
            signAndSendTransactions(arrayOf(tx));
        }

        return when (result) {
            is TransactionResult.Success -> {
                val txSignatureBytes = result.successPayload?.signatures?.first()
                if (txSignatureBytes != null) {
                    rustffiToBase64(txSignatureBytes)
                } else {
                    throw Exception("Signed payload missing")
                }
            }

            is TransactionResult.NoWalletFound ->
                throw Exception("No MWA compatible wallet app found on device.")

            is TransactionResult.Failure ->
                throw Exception("Error during transaction signing: ${result.e.message ?: "Unknown error"}")
        }
    }
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
        logoUri = value.assetInfo?.logoUri,
        maxTimeoutSeconds = value.maxtimeoutSeconds,
        decimals = value.assetInfo?.decimals?.toInt()
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TxSuccessBottomSheet(
    success: String,
    callback: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val showSheet = remember { mutableStateOf(true) }

    // Modal sheet
    if (showSheet.value) {
        ModalBottomSheet(
            onDismissRequest = {
                showSheet.value = false
                callback()
            },
            sheetState = sheetState,
            containerColor = Licorice,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                LagoonMarketsLogo()
                Spacer(Modifier.height(50.dp))
                TextPurpleMountainMajesty(
                    textContent = "Success",
                    fontSize = 35.sp,
                    fontFamily = smoochSansFamily
                )
                Spacer(Modifier.height(20.dp))
                val link = "https://explorer.solana.com/tx/$success"
                NonPriorityButton(
                    callback = {
                        val intent = Intent(Intent.ACTION_VIEW, link.toUri())
                        context.startActivity(intent)
                    },
                    textContent = "View in Explorer",
                    fillMaxWidth = 0.8f,
                    enabled = true
                )

                Spacer(Modifier.height(50.dp))

                GradientButton(
                    callback = {
                        showSheet.value = false
                        callback()
                    },
                    "Close"
                )
            }
        }
    }
}
