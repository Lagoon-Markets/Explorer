package lagoon.markets.explorer

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import lagoon.markets.explorer.ui.theme.EnglishViolet
import lagoon.markets.explorer.ui.theme.brushDarkHorizontalGradient

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