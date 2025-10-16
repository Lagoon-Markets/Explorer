package lagoon.markets.explorer.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode

val brushDarkVerticalGradient = Brush.verticalGradient(
    endY = Float.POSITIVE_INFINITY,
    colorStops = arrayOf(
        0.0f to Color(0xFF3A1951),
        0.40f to Color(0xFF1F072A),
        0.6f to Color(0xFF14001A),
        1.0f to Color(0xFF09000C)
    )
)

val brushDarkHorizontalGradient = Brush.horizontalGradient(
    colors = listOf(Purple, HanPurple),
    startX = 0.0f,
    endX = Float.POSITIVE_INFINITY,
    tileMode = TileMode.Clamp
)