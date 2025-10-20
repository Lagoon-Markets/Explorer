package lagoon.markets.explorer

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lagoon.markets.explorer.ui.theme.EnglishViolet
import lagoon.markets.explorer.ui.theme.PurpleMountainMajesty
import lagoon.markets.explorer.ui.theme.brushDarkHorizontalGradient
import lagoon.markets.explorer.ui.theme.poppinsFamily
import lagoon.markets.explorer.ui.theme.smoochSansFamily


@Composable
fun LagoonMarketsLogo() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {

            Image(
                painter = painterResource(R.drawable.lagoon_markets_logo),
                contentDescription = "Lagoon.Markets Logo",
                modifier = Modifier.width(50.dp)
            )

            Spacer(Modifier.width(10.dp))

            AppText(
                textContent = "Lagoon.Markets",
                fontFamily = smoochSansFamily,
                fontSize = 40.sp,
                color = PurpleMountainMajesty
            )
        }
        Spacer(Modifier.height(10.dp))
        AppText(
            textContent = "x402 micro-transactions for agentic era",
            fontFamily = poppinsFamily,
            fontSize = 15.sp,
            color = EnglishViolet
        )
    }
}

fun appLog(value: String) {
    Log.d("LAGOON.MARKETS> ", value)
}

enum class LoadingState {
    Initial,
    Loaded
}

enum class ScreenState {
    Initial,
    Processing,
    Completed
}

@Composable
fun AppLinearLoader() {
    LinearProgressIndicator(
        modifier = Modifier
            .fillMaxWidth(.8f)
            .background(brush = brushDarkHorizontalGradient)
            .height(3.dp),
        color = Color.Transparent,
        trackColor = EnglishViolet,
        strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
    )
}