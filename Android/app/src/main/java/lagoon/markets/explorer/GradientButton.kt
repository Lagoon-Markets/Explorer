package lagoon.markets.explorer

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import lagoon.markets.explorer.ui.theme.EnglishViolet
import lagoon.markets.explorer.ui.theme.HanPurple
import lagoon.markets.explorer.ui.theme.RussianViolet
import lagoon.markets.explorer.ui.theme.brushDarkHorizontalGradient

@Composable
fun GradientButton(
    callback: () -> Unit,
    textContent: String,
    brush: Brush? = brushDarkHorizontalGradient,
    backgroundColor: Color = RussianViolet,
    backgroundColorPressed: Color = HanPurple,
    fillMaxWidth: Float = 0.65f,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val haptic = LocalHapticFeedback.current

    val modifier = Modifier
        .height(48.dp)
        .fillMaxWidth(fillMaxWidth)
        .shadow(
            elevation = 50.dp, // Blur strength
            shape = RoundedCornerShape(50.dp),     // Match the shape!
            clip = false                           // Keep true blur edge
        )
        .clip(RoundedCornerShape(50.dp))
        .background(brush = brushDarkHorizontalGradient)

    var computeBackgroundColor = Color.Transparent

    if (brush == null) {
        modifier.background(color = backgroundColor)
        computeBackgroundColor = backgroundColor
    } else {
        modifier.background(brush = brush)
    }

    Button(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
            callback()
        },
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(containerColor = if (isPressed) backgroundColorPressed else computeBackgroundColor),
        contentPadding = PaddingValues(),
        shape = RoundedCornerShape(50.dp),
        modifier = modifier,
        enabled = enabled,
    ) {
        TextWhite(textContent = textContent)
    }
}

@Composable
fun ProgressGradientButton(
    callback: () -> Unit,
    textContent: String,
    brush: Brush? = brushDarkHorizontalGradient,
    backgroundColor: Color = RussianViolet,
    backgroundColorPressed: Color = HanPurple,
    fillMaxWidth: Float = 0.65f,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val haptic = LocalHapticFeedback.current

    val modifier = Modifier
        .height(48.dp)
        .fillMaxWidth(fillMaxWidth)
        .shadow(
            elevation = 50.dp, // Blur strength
            shape = RoundedCornerShape(50.dp),     // Match the shape!
            clip = false                           // Keep true blur edge
        )
        .clip(RoundedCornerShape(50.dp))
        .background(brush = brushDarkHorizontalGradient)

    var computeBackgroundColor = Color.Transparent

    if (brush == null) {
        modifier.background(color = backgroundColor)
        computeBackgroundColor = backgroundColor
    } else {
        modifier.background(brush = brush)
    }

    Button(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
            callback()
        },
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(containerColor = if (isPressed) backgroundColorPressed else computeBackgroundColor),
        contentPadding = PaddingValues(),
        shape = RoundedCornerShape(50.dp),
        modifier = modifier,
        enabled = enabled,
    ) {
        if (enabled) {
            TextWhite(textContent = textContent)
        } else {
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
    }
}

@Composable
fun NonPriorityButton(
    callback: () -> Unit,
    textContent: String,
    backgroundColor: Color = RussianViolet,
    backgroundColorPressed: Color = HanPurple,
    fillMaxWidth: Float = 0.65f,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val haptic = LocalHapticFeedback.current

    val modifier = Modifier
        .height(48.dp)
        .fillMaxWidth(fillMaxWidth)
        .shadow(
            elevation = 50.dp, // Blur strength
            shape = RoundedCornerShape(50.dp),     // Match the shape!
            clip = false                           // Keep true blur edge
        )
        .clip(RoundedCornerShape(50.dp))
        .background(color = backgroundColor)

    Button(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
            callback()
        },
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(containerColor = if (isPressed) backgroundColorPressed else backgroundColor),
        contentPadding = PaddingValues(),
        shape = RoundedCornerShape(50.dp),
        modifier = modifier,
        enabled = enabled,
    ) {
        TextWhite(textContent = textContent)
    }
}