package lagoon.markets.explorer

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import lagoon.markets.explorer.ui.theme.AmericanPurple
import lagoon.markets.explorer.ui.theme.HanPurple
import lagoon.markets.explorer.ui.theme.Licorice
import lagoon.markets.explorer.ui.theme.RussianViolet
import lagoon.markets.explorer.ui.theme.brushDarkHorizontalGradient

@Composable
fun OnboardingView(navController: NavController) {
    val currentOnboardingView = remember { mutableStateOf(CurrentOnboardingView.Discover) }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CurrentOnboardingImage(currentOnboardingView)
            CurrentOnboardingDescription(currentOnboardingView)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(0.8f)
            ) {
                if (currentOnboardingView.value != CurrentOnboardingView.Discover) {
                    OnboardingPreviousButton(
                        currentOnboardingView,
                        callback = {
                            if (currentOnboardingView.value == CurrentOnboardingView.MicroTransact) {
                                currentOnboardingView.value = CurrentOnboardingView.Discover
                            }

                            if (currentOnboardingView.value == CurrentOnboardingView.Subscribe) {
                                currentOnboardingView.value = CurrentOnboardingView.MicroTransact
                            }
                        },
                        buttonDescription = "Back",
                    )
                }
            }

            Row(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier
                        .padding(5.dp)
                ) { OnboardingCircle(currentOnboardingView.value == CurrentOnboardingView.Discover) }
                Box(
                    Modifier
                        .padding(5.dp)
                ) { OnboardingCircle(currentOnboardingView.value == CurrentOnboardingView.MicroTransact) }
                Box(
                    Modifier
                        .padding(5.dp)
                ) { OnboardingCircle(currentOnboardingView.value == CurrentOnboardingView.Subscribe) }
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
            ) {
                if (currentOnboardingView.value != CurrentOnboardingView.Subscribe) {
                    OnboardingButton(
                        currentOnboardingView,
                        callback = {
                            when (currentOnboardingView.value) {
                                CurrentOnboardingView.Discover -> currentOnboardingView.value =
                                    CurrentOnboardingView.MicroTransact

                                CurrentOnboardingView.MicroTransact -> currentOnboardingView.value =
                                    CurrentOnboardingView.Subscribe

                                CurrentOnboardingView.Subscribe -> {}
                            }
                        },
                        buttonDescription = "Next",
                    )
                } else {
                    OnboardingButton(
                        currentOnboardingView,
                        callback = {
                            navController.navigate(SignUpRoute)
                        },
                        buttonDescription = "Sign Me Up! \uD83C\uDF89",
                    )
                }
            }
        }
    }
}


enum class CurrentOnboardingView {
    Discover,
    MicroTransact,
    Subscribe,
}

@Composable
fun CurrentOnboardingImage(currentOnboardingView: MutableState<CurrentOnboardingView>) {
    val selectImageAndContentDescription = when (currentOnboardingView.value) {
        CurrentOnboardingView.Discover -> Pair(R.drawable.onboarding_1_image, "Explore SVG Image")
        CurrentOnboardingView.MicroTransact -> Pair(R.drawable.onboarding_2_image, "x402 SVG Image")
        CurrentOnboardingView.Subscribe -> Pair(
            R.drawable.onboarding_3_image,
            "Subscribe x402 SVG Image"
        )
    }

    val (image, contentDescription) = selectImageAndContentDescription

    AnimatedContent(
        targetState = currentOnboardingView.value,
        transitionSpec = {
            (slideInHorizontally(
                initialOffsetX = { it } // same as 100% width
            ) + fadeIn()).togetherWith(
                slideOutHorizontally(
                    targetOffsetX = { -it } // slide old out to left
                ) + fadeOut()
            ).using(SizeTransform(clip = false))
        },
        contentAlignment = Alignment.Center
    ) { targetText ->
        targetText
        Image(
            painter = painterResource(id = image),
            contentDescription = contentDescription,
            modifier = Modifier.size(300.dp)
        )
    }
}

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
fun CurrentOnboardingDescription(currentOnboardingView: MutableState<CurrentOnboardingView>) {
    val titleAndDescription = when (currentOnboardingView.value) {
        CurrentOnboardingView.Discover -> Pair(
            "Discover",
            "Discover x402 services",
        )

        CurrentOnboardingView.MicroTransact -> Pair(
            "Micro-Transact",
            "Pay for x402 services in micro-transactions"
        )

        CurrentOnboardingView.Subscribe -> Pair(
            "Subscribe",
            "Receive updates to subscribed services or long-running agentic actions"
        )
    }

    val (title, description) = titleAndDescription


    return Column(
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(.9f)
    ) {
        AnimatedContent(
            targetState = currentOnboardingView.value,
            transitionSpec = {
                (slideInHorizontally(
                    initialOffsetX = { it } // same as 100% width
                ) + fadeIn()).togetherWith(
                    slideOutHorizontally(
                        targetOffsetX = { -it } // slide old out to left
                    ) + fadeOut()
                ).using(SizeTransform(clip = false))
            },
            contentAlignment = Alignment.Center
        ) { targetText ->
            TextEnglishViolet(
                textContent = title,
                fontSize = 70.sp,
            )
        }

        Spacer(Modifier.height(50.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            TextPurpleMountainMajesty(
                textContent = description,
                fontSize = 20.sp,
            )
        }
    }
}

@Composable
fun OnboardingButton(
    currentOnboardingView: MutableState<CurrentOnboardingView>,
    callback: (state: MutableState<CurrentOnboardingView>) -> Unit,
    buttonDescription: String,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val backgroundColor by animateColorAsState(
        targetValue = if (isPressed) HanPurple else Color.Transparent,
        animationSpec = tween(durationMillis = 600) // 👈 slow down to 600ms
    )
    val haptic = LocalHapticFeedback.current

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                    callback(currentOnboardingView)
                }, interactionSource = interactionSource,
                colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
                contentPadding = PaddingValues(),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth()
                    .shadow(
                        elevation = 50.dp,                      // Blur strength
                        shape = RoundedCornerShape(50.dp),     // Match the shape!
                        clip = false                           // Keep true blur edge
                    )
                    .clip(RoundedCornerShape(50.dp))
                    .background(brush = brushDarkHorizontalGradient)
            ) {
                TextWhite(textContent = buttonDescription)
            }
        }
    }
}

@Composable
fun OnboardingPreviousButton(
    currentOnboardingView: MutableState<CurrentOnboardingView>,
    callback: (state: MutableState<CurrentOnboardingView>) -> Unit,
    buttonDescription: String,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val backgroundColor by animateColorAsState(
        targetValue = if (!isPressed) RussianViolet else Licorice,
        animationSpec = tween(durationMillis = 600) // 👈 slow down to 600ms
    )
    val haptic = LocalHapticFeedback.current
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                    callback(currentOnboardingView)
                }, interactionSource = interactionSource,
                colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
                contentPadding = PaddingValues(),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth()
                    .shadow(
                        elevation = 50.dp,                      // Blur strength
                        shape = RoundedCornerShape(50.dp),     // Match the shape!
                        clip = false                           // Keep true blur edge
                    )
                    .clip(RoundedCornerShape(50.dp))
                    .background(RussianViolet)
            ) {
                TextWhite(textContent = buttonDescription)
            }
        }
    }
}

@Composable
fun OnboardingCircle(active: Boolean) {

    if (active) {
        Box(
            modifier = Modifier
                .height(10.dp)
                .width(20.dp)
                .clip(RoundedCornerShape(50)) // pill-like curve
                .background(HanPurple)
        )
    } else {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(AmericanPurple)
        )
    }
}