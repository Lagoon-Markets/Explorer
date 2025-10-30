# Lagoon Explorer

> x402 micro-transactions for agentic era.

Lagoon Explorer is a mobile dapp that simplifies discovery of services using the x402 protocol; allowing users to access one-off services or subscribe to tasks and receive notifications from other services or AI agents on their mobile devices.

The dapp handles [x402-URI](./x402-uri/SPECSHEET.md)(s) allowing users to interact with x402 services from the web, another mobile app or scanning a QR Code in the physical world.

This dapp shows how user experience can be massively improved by using [x402-URI](./x402-uri/SPECSHEET.md)(s) and smartphone features like haptic feedback, notification scheduling, deeplinks and live updates.

## Downloading the app
Download the app from the [Github releases](https://github.com/Lagoon-Markets/Explorer/releases/tag/v1.0.0). The apk contains all the binaries and native shared libraries for armv7, armv8 (arm64) and x86 for simple installation at the expense of binary size.

On opening the app, it will require you to sign in with Solana. The address used to sign the request is the same address used when building transactions from x402 resources. The user signs with their Solana wallet app (Phantom wallet assumes Solana testnet and refuses to sign. The transaction requires solana mainnet in order for Sanctum gateway to route transactions properly using the best available transaction optimizations).

You can use [Lagoon.Markets](https://lagoon.markets) to discover resources by clicking on the `Discover Resources` button on the website.

The agent-2-agent resource from [Lagoon.Markets](https://lagoon.markets) requires a version of Android 16+ that supports all the features of Android 16 live updates. It demos how an agent can keep the user in the loop on long running agentic tasks that implement the server-side events. An Android 16 canary virtual device from android studio work well.

## Building the app.
### Prerequisites 
1. libssl-dev
2. openssl
3. Rust
4. Android Studio (preferably Android Studio canary for live updates preview; unless live updates are already available in your version of Android 16+)
5. The canary android virtual device of Android 16 that fully supports all the features of Android live updates. (Useful for the second demo)
6. [cargo-ndk](https://crates.io/crates/cargo-ndk)
7. [cargo-make](https://crates.io/crates/cargo-make)
8. Solflare mobile wallet. (Phantom wallet assumes Solana testnet and refuses to sign. The transaction requires solana mainnet in order for Sanctum gateway to route transactions properly using the best available transaction optimizations)
   
### Building
1. Clone this repository.
    ```sh
    git clone https://github.com/Lagoon-Markets/Explorer.git

    cd Explorer
    ```
2. Build and host the server that handles discovering x402 resources in the server directory by running.
    ```sh
    cd server

    cargo build --release
    ```
3. Building the Android app's native code
    ```sh
    # From the root directory of the repo
    cd native

    # Automates building the shared libraries android for Arm and X86 and copying them to the required directories
    cargo make 
    ```
4. Open the `Android` directory in Jetpack compose
5. Launch a device as described in `Prerequisites` point `4` or a physical device that implements all the features of Android 16 live updates
6. Open a website that supports discovering x402 resources with x402-URI (like [Lagoon.Markets](https://lagoon.markets)) or scan a QR Code with an Android phone (like Seeker). For the QR code scan it with Firefox or Chrome and for the link open it with Firefox or Chrome or any other browser that supports opening deeplinks on Android.


