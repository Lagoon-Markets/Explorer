package lagoon.markets.explorer

import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import lagoon.markets.explorer.ui.theme.EnglishViolet
import lagoon.markets.explorer.ui.theme.PurpleMountainMajesty
import lagoon.markets.explorer.ui.theme.White
import lagoon.markets.explorer.ui.theme.commitMonoFamily
import lagoon.markets.explorer.ui.theme.poppinsFamily
import lagoon.markets.explorer.ui.theme.smoochSansFamily

@Composable
fun AppText(
    modifier: Modifier = Modifier,
    textContent: String,
    fontFamily: FontFamily = smoochSansFamily,
    fontWeight: FontWeight = FontWeight.Normal,
    fontSize: TextUnit = 30.sp,
    textAlign: TextAlign = TextAlign.Center,
    color: Color = White,
    lineHeight: TextUnit = 35.sp,
    maxLines: Int = Int.MAX_VALUE,
    textOverflow: TextOverflow = TextOverflow.Ellipsis
) {
    Text(
        color = color,
        fontFamily = fontFamily,
        fontWeight = fontWeight,
        textAlign = textAlign,
        text = textContent,
        modifier = modifier.wrapContentWidth(),
        style = TextStyle(
            fontSize = fontSize,
            lineHeight = lineHeight
        ),
        maxLines = maxLines,
        overflow = textOverflow
    )
}


@Composable
fun TextEnglishViolet(
    modifier: Modifier = Modifier,
    textContent: String,
    fontFamily: FontFamily = smoochSansFamily,
    fontWeight: FontWeight = FontWeight.Normal,
    fontSize: TextUnit = 30.sp,
    textAlign: TextAlign = TextAlign.Center,
    lineHeight: TextUnit = 35.sp,
    maxLines: Int = Int.MAX_VALUE,
    textOverflow: TextOverflow = TextOverflow.Ellipsis
) {
    AppText(
        textContent = textContent,
        fontSize = fontSize,
        color = EnglishViolet,
        fontFamily = fontFamily,
        fontWeight = fontWeight,
        textAlign = textAlign,
        modifier = modifier,
        lineHeight = lineHeight,
        maxLines = maxLines,
        textOverflow = textOverflow
    )
}

@Composable
fun TextPurpleMountainMajesty(
    modifier: Modifier = Modifier,
    textContent: String,
    fontFamily: FontFamily = poppinsFamily,
    fontWeight: FontWeight = FontWeight.Normal,
    fontSize: TextUnit = 30.sp,
    textAlign: TextAlign = TextAlign.Center,
    lineHeight: TextUnit = 35.sp,
    maxLines: Int = Int.MAX_VALUE,
    textOverflow: TextOverflow = TextOverflow.Ellipsis
) {
    AppText(
        textContent = textContent,
        fontSize = fontSize,
        color = PurpleMountainMajesty,
        fontFamily = fontFamily,
        fontWeight = fontWeight,
        textAlign = textAlign,
        modifier = modifier,
        lineHeight = lineHeight,
        maxLines = maxLines,
        textOverflow = textOverflow
    )
}

@Composable
fun TextWhite(
    modifier: Modifier = Modifier,
    textContent: String,
    fontFamily: FontFamily = smoochSansFamily,
    fontWeight: FontWeight = FontWeight.Normal,
    fontSize: TextUnit = 30.sp,
    textAlign: TextAlign = TextAlign.Center,
    lineHeight: TextUnit = 35.sp,
    maxLines: Int = Int.MAX_VALUE,
    textOverflow: TextOverflow = TextOverflow.Ellipsis
) {
    AppText(
        textContent = textContent,
        fontSize = fontSize,
        color = White,
        fontFamily = fontFamily,
        fontWeight = fontWeight,
        textAlign = textAlign,
        modifier = modifier,
        lineHeight = lineHeight,
        maxLines = maxLines,
        textOverflow = textOverflow
    )
}

@Composable
fun TextMonospaceWhite(
    modifier: Modifier = Modifier,
    textContent: String,
    fontFamily: FontFamily = commitMonoFamily,
    fontWeight: FontWeight = FontWeight.Normal,
    fontSize: TextUnit = 30.sp,
    textAlign: TextAlign = TextAlign.Center,
    lineHeight: TextUnit = 35.sp,
    maxLines: Int = Int.MAX_VALUE,
    textOverflow: TextOverflow = TextOverflow.Ellipsis
) {
    AppText(
        textContent = textContent,
        fontSize = fontSize,
        color = White,
        fontFamily = fontFamily,
        fontWeight = fontWeight,
        textAlign = textAlign,
        modifier = modifier,
        lineHeight = lineHeight,
        maxLines = maxLines,
        textOverflow = textOverflow
    )
}