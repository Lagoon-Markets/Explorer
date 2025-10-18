package lagoon.markets.explorer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
            .width(.8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(10.dp)
                .background(color = Licorice, shape = RoundedCornerShape(50.dp))
                .padding(10.dp)
        ) {
            val x402CurrentUri =
                "x402:// https://lagoon.markets jjgjgjgjgjjgjgjgjgjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj"
            val x402CurrentUriTrunked = x402CurrentUri.take(55) + "…"
            TextPurpleMountainMajesty(
                textContent = x402CurrentUriTrunked,
                fontSize = 10.sp,
                fontFamily = commitMonoFamily,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(Modifier.height(50.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())

        ) {
            DiscoveryItem()
            Spacer(Modifier.height(40.dp))
            DiscoveryItem()
        }
    }
}

@Composable
fun DiscoveryItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(200.dp)
            .clip(RoundedCornerShape(25.dp))
            .border(
                width = 2.dp,
                brush = brushDarkHorizontalGradient,
                shape = RoundedCornerShape(25.dp)
            )
    ) {
        // Background image or fallback
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data("https://lagoon.markets/typewriter.jpg").crossfade(true).build()
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
                            Licorice.copy(alpha = .6f),
                            Licorice.copy(alpha = .8f),
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
                    painter = painterResource(R.drawable.splashscreen_icon),
                    contentDescription = "Web content description",
                    modifier = Modifier.width(40.dp)
                )
                TextWhite(
                    textContent = "Web", fontFamily = smoochSansFamily, fontSize = 20.sp
                )
            }
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(10.dp),
            ) {
                AppText(
                    textContent = "Latest Newsletter",
                    fontFamily = smoochSansFamily,
                    fontSize = 30.sp,
                    color = PurpleMountainMajesty
                )
                AppText(
                    textContent = "Get latest insights on onchain activity and developer productivity",
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
