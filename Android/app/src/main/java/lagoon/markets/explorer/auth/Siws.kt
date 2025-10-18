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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender
import com.solana.mobilewalletadapter.clientlib.ConnectionIdentity
import com.solana.mobilewalletadapter.clientlib.MobileWalletAdapter
import com.solana.mobilewalletadapter.clientlib.TransactionResult
import com.solana.mobilewalletadapter.clientlib.protocol.MobileWalletAdapterClient
import com.solana.mobilewalletadapter.common.signin.SignInWithSolana
import kotlinx.coroutines.launch
import lagoon.markets.SiwsFfiAuthResult
import lagoon.markets.explorer.AppLinearLoader
import lagoon.markets.explorer.AppStateViewModel
import lagoon.markets.explorer.LoadingState
import lagoon.markets.explorer.ProgressGradientButton
import lagoon.markets.rustffiGetAuth
import lagoon.markets.rustffiSiws

@Composable
fun CheckSiws(
    appStateViewModel: AppStateViewModel,
    sender: ActivityResultSender,
    paddingValues: PaddingValues
) {
    val nativeAuth = remember { mutableStateOf<String?>(null) }
    val errorExists = remember { mutableStateOf<Exception?>(null) }
    var loadingState by remember { mutableStateOf(LoadingState.Initial) }

    LaunchedEffect(Unit) {
        try {
            nativeAuth.value = rustffiGetAuth()
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
                nativeAuth.value?.let {
                    Text(it)
                } ?: SiwsScreen(sender)
            }
        }
    }
}

@Composable
fun SiwsScreen(sender: ActivityResultSender) {
    // `this` is the current Android activity
    val domain = "https://jamiidao.app"
    // Define dApp's identity metadata
    val solanaUri = domain.toUri()
    val iconUri = "favicon.png".toUri() // resolves to https://yourdapp.com/favicon.ico
    val identityName = "The Trenches Newsletter"

    // Construct the client
    val walletAdapter = MobileWalletAdapter(
        connectionIdentity = ConnectionIdentity(
            identityUri = solanaUri,
            iconUri = iconUri,
            identityName = identityName
        )
    )

    val signInStatement = "Sign in to Lagoon.Markets App"

    // `connect` dispatches an association intent to MWA-compatible wallet apps.
    val result = remember {
        mutableStateOf<TransactionResult<MobileWalletAdapterClient.AuthorizationResult.SignInResult>?>(
            null
        )
    }
    val buttonEnabled = remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    result.value?.let {
        when (it) {
            is TransactionResult.Success -> {
                // On success, an `AuthorizationResult` with a `signInResult` object is returned.
                val siwsResult = it.authResult
                val siwsSignInResult = siwsResult.signInResult
                siwsSignInResult?.let {
                    val siwsSignInResultToFfi = SiwsFfiAuthResult(
                        publicKey = siwsSignInResult.publicKey.toList(),
                        signedMessage = siwsSignInResult.signedMessage.toList(),
                        signature = siwsSignInResult.signature.toList(),
                        signatureType = siwsSignInResult.signatureType,
                        authToken = siwsResult.authToken
                    )

                    val address = rustffiSiws(siwsSignInResultToFfi)

                    Column {
                        Text(address)
                    }
                }
            }

            is TransactionResult.NoWalletFound -> {
                Text("No MWA compatible wallet app found on device.")
            }

            is TransactionResult.Failure -> {
                Text("Error connecting to wallet: " + it.e.message)
            }
        }
    } ?: Column {
        ProgressGradientButton(
            callback = {
                buttonEnabled.value = false;

                coroutineScope.launch {
                    result.value = walletAdapter.signIn(
                        sender,
                        SignInWithSolana.Payload(domain, signInStatement)
                    )
                }
            },
            textContent = "Sign In With Solana",
            enabled = buttonEnabled.value
        )
    }
}

@Composable
fun SiwsScreenView() {

}