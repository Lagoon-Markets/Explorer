package lagoon.markets.explorer

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import lagoon.markets.explorer.ui.theme.EnglishViolet
import lagoon.markets.explorer.ui.theme.Licorice
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


@Composable
fun ScreenLoader(textContent: String) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        LagoonMarketsLogo()
        Spacer(Modifier.height(50.dp))
        TextPurpleMountainMajesty(
            textContent = textContent,
            fontFamily = smoochSansFamily,
            fontSize = 25.sp
        )
        Spacer(Modifier.height(20.dp))
        AppLinearLoader()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowErrorAsBottomSheet(
    title: String,
    error: String,
    imageID: Int,
    imageDescription: String,
    buttonTextContent: String,
    callback: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val openBottomSheet = rememberSaveable { mutableStateOf(true) }

    val bottomSheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Sheet content
    if (openBottomSheet.value) {

        ModalBottomSheet(
            onDismissRequest = { openBottomSheet.value = false },
            sheetState = bottomSheetState,
            containerColor = Licorice
        ) {
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .wrapContentWidth()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = imageID),
                        contentDescription = imageDescription,
                        modifier = Modifier
                            .size(50.dp)
                            .padding(5.dp)
                    )

                    TextEnglishViolet(
                        textContent = title,
                        fontSize = 40.sp,
                    )
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .defaultMinSize(minHeight = 100.dp)
                ) {
                    TextPurpleMountainMajesty(textContent = error, fontSize = 20.sp)
                }

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom
                ) {
                    GradientButton(
                        {
                            scope
                                .launch { bottomSheetState.hide() }
                                .invokeOnCompletion {
                                    callback()

                                    if (!bottomSheetState.isVisible) {
                                        openBottomSheet.value = false
                                    }
                                }
                        },
                        textContent = buttonTextContent, brush = null,
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuccessBottomSheet(
    success: String,
    showSheet: MutableState<Boolean>,
    callback: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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

                TextPurpleMountainMajesty(
                    textContent = success,
                    fontSize = 20.sp,
                    fontFamily = poppinsFamily,
                    textAlign = TextAlign.Left
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorBottomSheet(
    error: MutableState<String?>,
    showSheet: MutableState<Boolean>,
    callback: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Modal sheet
    if (showSheet.value) {
        ModalBottomSheet(
            onDismissRequest = {
                showSheet.value = false
                error.value = null
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
                    textContent = "Encountered Error",
                    fontSize = 35.sp,
                    fontFamily = smoochSansFamily
                )
                Spacer(Modifier.height(20.dp))
                TextPurpleMountainMajesty(
                    textContent = error.value ?: "",
                    fontSize = 20.sp,
                    fontFamily = poppinsFamily,
                    textAlign = TextAlign.Left
                )
                Spacer(Modifier.height(50.dp))
                GradientButton(
                    callback = {
                        showSheet.value = false
                        error.value = null
                        callback()
                    },
                    "I understand"
                )
            }
        }
    }
}

fun Modifier.bottomBorder(
    color: Color = PurpleMountainMajesty,
    thickness: Dp = 2.dp
) = this.then(
    Modifier.drawBehind {
        val strokeWidth = thickness.toPx()
        val y = size.height - strokeWidth / 2
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(0f, y),
            end = androidx.compose.ui.geometry.Offset(size.width, y),
            strokeWidth = strokeWidth
        )
    }
)