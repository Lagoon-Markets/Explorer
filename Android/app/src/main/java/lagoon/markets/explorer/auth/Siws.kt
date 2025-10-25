package lagoon.markets.explorer.auth

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.solana.mobilewalletadapter.clientlib.ConnectionIdentity
import com.solana.mobilewalletadapter.clientlib.MobileWalletAdapter
import com.solana.mobilewalletadapter.clientlib.TransactionResult
import com.solana.mobilewalletadapter.clientlib.protocol.MobileWalletAdapterClient
import com.solana.mobilewalletadapter.common.signin.SignInWithSolana
import kotlinx.coroutines.launch
import lagoon.markets.AppDetailsFfi
import lagoon.markets.SiwsFfiAuthResult
import lagoon.markets.explorer.AuthorizeNotificationsRoute
import lagoon.markets.explorer.DashboardRoute
import lagoon.markets.explorer.ErrorBottomSheet
import lagoon.markets.explorer.LagoonMarketsLogo
import lagoon.markets.explorer.LocalActivityResultSender
import lagoon.markets.explorer.ProgressGradientButton
import lagoon.markets.explorer.TextPurpleMountainMajesty
import lagoon.markets.explorer.notifications.hasNotificationPermission
import lagoon.markets.rustffiSiws

@Composable
fun SiwsSignup(
    navController: NavController
) {
    val appDetails = AppDetailsFfi()

    // `this` is the current Android activity
    val sender = LocalActivityResultSender.current
    val solanaUri = appDetails.domain().toUri()
    val iconUri = appDetails.favicon().toUri()
    val identityName = appDetails.identity()

    // Construct the client
    val walletAdapter = MobileWalletAdapter(
        connectionIdentity = ConnectionIdentity(
            identityUri = solanaUri,
            iconUri = iconUri,
            identityName = identityName
        )
    )

    val signInStatement = appDetails.signInStatement()

    // `connect` dispatches an association intent to MWA-compatible wallet apps.
    val result = remember {
        mutableStateOf<TransactionResult<MobileWalletAdapterClient.AuthorizationResult.SignInResult>?>(
            null
        )
    }

    val coroutineScope = rememberCoroutineScope()
//    val sender = LocalActivityResultSender.current
    val errorHandler = remember { mutableStateOf<String?>(null) }
    val buttonEnabled = remember { mutableStateOf(true) }
    val context = LocalContext.current
    val activity = context as? Activity

    val showSheet = remember { mutableStateOf(false) }

    if (showSheet.value) {
        ErrorBottomSheet(
            error = errorHandler,
            showSheet = showSheet,
            callback = {
                errorHandler.value = null
                activity?.recreate()
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
        ) {
            LagoonMarketsLogo()
        }
        Column(
            Modifier
                .weight(1f)
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextPurpleMountainMajesty(
                textContent = "Sign In with your Solana wallet to create a profile, it's that simple!",
                fontSize = 20.sp
            )

            ProgressGradientButton(
                {
                    buttonEnabled.value = false
                    coroutineScope.launch {
                        // `connect` dispatches an association intent to MWA-compatible wallet apps.
                        val result = walletAdapter.signIn(
                            sender,
                            SignInWithSolana.Payload(
                                appDetails.domain(),
                                null, // address
                                appDetails.signInStatement(),
                                null, // uri
                                "1",  // version
                                "solana:mainnet", // chainId
                                null, // nonce
                                null, // issuedAt
                                null, // expirationTime
                                null, // notBefore
                                null, // requestId
                                null  // resources
                            )
                        )


                        when (result) {
                            is TransactionResult.Success -> {
                                // On success, an `AuthorizationResult` with a `signInResult` object is returned.
                                val signInAuthResult = result.authResult
                                val signInResult = result.authResult.signInResult


                                signInResult?.let {
                                    val siwsSignInResultToFfi = SiwsFfiAuthResult(
                                        publicKey = it.publicKey.toList(),
                                        signedMessage = it.signedMessage.toList(),
                                        signature = it.signature.toList(),
                                        signatureType = it.signatureType,
                                        authToken = signInAuthResult.authToken
                                    )

                                    try {
                                        rustffiSiws(siwsSignInResultToFfi)

                                        if (activity != null) {

                                            if (hasNotificationPermission(activity)) {
                                                navController.navigate(DashboardRoute)
                                            } else {
                                                navController.navigate(AuthorizeNotificationsRoute)
                                            }
                                        } else {
                                            navController.navigate(AuthorizeNotificationsRoute)
                                        }


                                    } catch (error: Exception) {
                                        errorHandler.value =
                                            error.message ?: "User rejected the request"
                                        showSheet.value = true
                                    }
                                }
                            }

                            is TransactionResult.NoWalletFound -> {
                                errorHandler.value = "No MWA compatible wallet app found on device."
                                showSheet.value = true
                            }

                            is TransactionResult.Failure -> {
                                errorHandler.value = result.e.message ?: "User rejected the request"
                                showSheet.value = true
                            }
                        }
                    }
                },
                "SIGN IN WITH SOLANA",
                enabled = buttonEnabled.value
            )
        }
    }
}