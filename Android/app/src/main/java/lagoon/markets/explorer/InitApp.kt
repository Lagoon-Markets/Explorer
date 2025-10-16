package lagoon.markets.explorer

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import lagoon.markets.explorer.ui.theme.brushDarkVerticalGradient
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
    viewModel: AppStateViewModel = viewModel(),
    paddingValues: PaddingValues
) {
    val initResult by viewModel.appState.collectAsState() // Result<Unit>?

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brushDarkVerticalGradient)
            .padding(paddingValues = paddingValues)
    ) {
        when {
            initResult == null -> {
                Text("Initializing...")
            }

            initResult?.isSuccess == true -> {
                Text("Initialized successfully")
            }

            else -> { // initResult != null && isFailure
                val error = initResult?.exceptionOrNull()
                Text("Initialization failed: ${error?.message}")
            }
        }
    }

}