package lagoon.markets.explorer.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import lagoon.markets.UserProfile
import lagoon.markets.explorer.AppLinearLoader
import lagoon.markets.explorer.LoadingState
import lagoon.markets.rustffiGetProfile

@Composable
fun CheckSiws(paddingValues: PaddingValues) {
    val userProfile = remember { mutableStateOf<UserProfile?>(null) }
    val errorExists = remember { mutableStateOf<Exception?>(null) }
    var loadingState by remember { mutableStateOf(LoadingState.Initial) }

    LaunchedEffect(Unit) {
        try {
            userProfile.value = rustffiGetProfile()
        } catch (error: Exception) {
            errorExists.value = error
        }

        loadingState = LoadingState.Loaded
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        when (loadingState) {
            LoadingState.Initial -> {
                AppLinearLoader()
            }

            LoadingState.Loaded -> {
                userProfile.value?.let {
                    Text(it.publicKey.address)
                } ?: Text("User profile not found")
            }
        }
    }
}

