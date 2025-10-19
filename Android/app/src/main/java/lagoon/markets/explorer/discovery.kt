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
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import lagoon.markets.explorer.ui.theme.HanPurple
import lagoon.markets.explorer.ui.theme.Licorice
import lagoon.markets.explorer.ui.theme.PurpleMountainMajesty
import lagoon.markets.explorer.ui.theme.brushDarkHorizontalGradient
import lagoon.markets.explorer.ui.theme.commitMonoFamily
import lagoon.markets.explorer.ui.theme.poppinsFamily
import lagoon.markets.explorer.ui.theme.smoochSansFamily

@Composable
fun DiscoveryList() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(.95f)
                .padding(horizontal = 20.dp)
                .background(color = Licorice, shape = RoundedCornerShape(50.dp))
        ) {
            val x402CurrentUri =
                "https://lagoon.markets"
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
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .background(
                            color = HanPurple,
                            shape = RoundedCornerShape(
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
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(10.dp)
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

        Spacer(Modifier.height(50.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())

        ) {
            val foo = "Get latest insights on onchain activity and developer productivity"
            val fooBgImage = "https://lagoon.markets/typewriter.jpg"
            val bar = "Convert newsletter to audio using an AI agent and download locally!"

            DiscoveryItem(
                backgroundImage = fooBgImage,
                actionType = "Web",
                actionTypeIcon = R.drawable.ic_launcher_background,
                actionTypeIconDescription = "Web Action Icon",
                actionTitle = "Latest Newsletter",
                actionDescription = foo,
                paymentCoinIcon = R.drawable.ic_launcher_background,
                paymentCoinIconDescription = "USDC",
                paymentCoinAmount = "1",
                onClick = {}
            )
            Spacer(Modifier.height(40.dp))
            DiscoveryItem(
                backgroundImage = fooBgImage,
                actionType = "Agent",
                actionTypeIcon = R.drawable.ic_launcher_background,
                actionTypeIconDescription = "Agent Action Icon",
                actionTitle = "Audio Newsletter",
                actionDescription = bar,
                paymentCoinIcon = R.drawable.ic_launcher_background,
                paymentCoinIconDescription = "USDC",
                paymentCoinAmount = "1.5",
                onClick = {}
            )
        }
    }
}

@Composable
fun DiscoveryItem(
    backgroundImage: String,
    actionType: String,
    actionTypeIcon: Int,
    actionTypeIconDescription: String,
    actionTitle: String,
    actionDescription: String,
    paymentCoinIcon: Int,
    paymentCoinIconDescription: String,
    paymentCoinAmount: String,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(250.dp)
            .clip(RoundedCornerShape(25.dp))
            .border(
                width = 2.dp,
                brush = brushDarkHorizontalGradient,
                shape = RoundedCornerShape(25.dp)
            )
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                // Handle click action here
            }
    ) {
        // Background image or fallback
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data(backgroundImage).crossfade(true).build()
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
                    painter = painterResource(actionTypeIcon),
                    contentDescription = actionTypeIconDescription,
                    modifier = Modifier
                        .width(20.dp)
                )
                Spacer(Modifier.width(5.dp))
                TextWhite(
                    textContent = actionType, fontFamily = smoochSansFamily, fontSize = 25.sp
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
                            textContent = actionTitle,
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
                                    painter = painterResource(paymentCoinIcon),
                                    contentDescription = paymentCoinIconDescription,
                                    modifier = Modifier.width(25.dp)
                                )
                            }
                            Spacer(Modifier.width(5.dp))
                            Box {
                                TextWhite(
                                    textContent = paymentCoinAmount,
                                    fontFamily = commitMonoFamily,
                                    fontSize = 20.sp
                                )
                            }
                        }
                    }
                }
                AppText(
                    textContent = actionDescription,
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
