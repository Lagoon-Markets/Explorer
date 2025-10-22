package lagoon.markets.explorer

import android.app.Application
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import lagoon.markets.explorer.ui.theme.brushDarkVerticalGradient
import lagoon.markets.explorer.ui.theme.poppinsFamily
import lagoon.markets.explorer.ui.theme.smoochSansFamily
import lagoon.markets.rustffiGetAuth
import lagoon.markets.rustffiInitDb

class AppStateViewModel(application: Application) : AndroidViewModel(application) {

    val appStorageDir: String = application.filesDir.absolutePath

    private val _appState = MutableStateFlow<Result<Unit>?>(null)
    val appState: StateFlow<Result<Unit>?> = _appState

    init {
        viewModelScope.launch {
            _appState.value = initNative(appStorageDir)
        }
    }

    private suspend fun initNative(appStorageDir: String): Result<Unit> {
        return try {
            rustffiInitDb(appStorageDir)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


@Composable
fun InitApp(
    appStateViewModel: AppStateViewModel = viewModel(),
    sender: ActivityResultSender,
    paddingValues: PaddingValues
) {
    val initResult by appStateViewModel.appState.collectAsState() // Result<Unit>?
    val activity = LocalActivity.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brushDarkVerticalGradient)
            .padding(paddingValues = paddingValues)
    ) {
        when {
            initResult == null -> {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    TextPurpleMountainMajesty(
                        textContent = "Initializing App", fontFamily = smoochSansFamily
                    )
                    Spacer(Modifier.height(10.dp))
                    AppLinearLoader()
                }
            }

            initResult?.isSuccess == true -> {
                AppNavigation(appStateViewModel)
            }

            else -> { // initResult != null && isFailure
                val error = initResult?.exceptionOrNull()
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    LagoonMarketsLogo()
                    Spacer(Modifier.height(50.dp))
                    TextPurpleMountainMajesty(
                        textContent = "Initialization failed: ${error.toString()}",
                        fontFamily = smoochSansFamily
                    )
                    Spacer(Modifier.height(50.dp))
                    GradientButton(callback = { activity?.finish() }, textContent = "Exit App")
                }
            }
        }
    }
}

@Composable
fun CheckProfileInitialized(navController: NavController) {

    val isProfileInitialized = remember { mutableStateOf<String?>(null) }
    var viewState by remember { mutableStateOf(LoadingState.Initial) }
    val errorExists = remember { mutableStateOf<Exception?>(null) }

    LaunchedEffect(Unit) {
        isProfileInitialized.value = rustffiGetAuth()
        try {
            viewState = LoadingState.Loaded
        } catch (error: Exception) {
            errorExists.value = error
        }
    }

    if (viewState == LoadingState.Initial) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            TextPurpleMountainMajesty(
                textContent = "Loading user profile!", fontFamily = smoochSansFamily
            )
            Spacer(Modifier.height(10.dp))
            AppLinearLoader()
        }
    } else {

        if (errorExists.value != null) {
            ShowAppError(errorExists.value!!)
        } else if (isProfileInitialized.value != null) {
            navController.navigate(DashboardRoute)

        } else {
            navController.navigate(OnboardingRoute)
        }
    }
}

@Composable
fun ShowAppError(error: Exception) {
    val activity = LocalActivity.current

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        LagoonMarketsLogo()
        Spacer(Modifier.height(50.dp))
        TextPurpleMountainMajesty(
            textContent = "App Error",
            fontFamily = smoochSansFamily,
            fontSize = 30.sp
        )
        Spacer(Modifier.height(50.dp))
        TextPurpleMountainMajesty(
            textContent = error.message ?: toString(),
            fontFamily = poppinsFamily,
            fontSize = 25.sp
        )
        Spacer(Modifier.height(10.dp))

        GradientButton(callback = { activity?.finish() }, textContent = "Exit App")
    }

}


