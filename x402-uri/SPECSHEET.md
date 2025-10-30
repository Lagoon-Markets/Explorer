# x402-URI
> Universal Resource Identifier for x402 resources

The x402-URI is a protocol to improve the user-experience of discovering and interacting with x402 resources. It is inspired by `Solana pay` QR codes and the HTML scheme `tel:+` that opens a number on a website in the user's telephone dialer. 

x402 protocol defines a protocol for discovering and interacting with services that implement the HTTP 402 status code for payments. Beyond that the x402 protocol is agnostic and dosen't define a unified way of users interacting with the x402 resources.

Imagine a scenario where Alice (a Solana DeFi user) walks past a billboard and sees a new book `Conqueror of Blockhains; Taker Of Markets.` by her favourite author `Toly The Great`. The publisher's website supports x402 payments where Alice dosen't need to create an account, she can just pay for the book using x402 microtransactions. So does she need to search online for the book, lookup which publishers sell the eBook version and create an account? Or is there a better way to do it?

Using the x402-URI, the billboard can add an x402-URI as part of the billboard graphic next to the book. Alice can simply scan it and her phone will prompt her to open the x402-URI with an app on her phone that supports the x402-URI protocol. The app shows her the resource and a button to buy the resource. When Alice clicks the button the app prepares a transaction and sends a `signTransaction` request to her mobile wallet where she signs the transaction and then the x402-URI app sends the transaction to the publisher's server who fulfills the request using the x402 protocol.

This user experience ensures Alice can get access to the book in a few seconds (assuming a fast blockchain like Solana) by just viewing and tapping away. The app that supports x402-URI does all the heavy lifting from fetching the information about the resource, any graphics (like the book's cover), the price, the address she is paying to, preparing a transaction and returning the payment to the publisher's x402 server. Alice just had some funds in a wallet, no need to know what protocol was used to give her access to her book. Now she has something to do this weekend :)

## The x402-URI protocol

```sh
# Example of a x402-URI that discovers x402 resources at the URL
x402://discover/example.com/x402
```

### The x402:// scheme
The x402-URI is identified by the scheme `x402://`. While just using the `x402:` as the scheme is sufficient, for maximum compatibility with URL parsers the `//` is added to `x402:` to reduce friction of adopting this URI protocol.

### The domain
The domain after the `x402://` scheme is used to decide how to handle a link. This can be useful especially in handling android URIs using a navigation mechanism. Note that all URLs must be URL encoded.

The domain is of four types:
1. `discover/`

    The `discover/` domain is used to discover x402 resources on a server. What follows the `discover/` domain is a URI used to fetch the resources as JSON as described in the x402 specification. The URI does not need to be in the route, query parameters can be defined by the server meaning that the URL is agnostic as long as it is a HTTPS url
    ```sh
    # An example
    x402://discover/https://example.com/x402/discover
    ```

2. `once/`

    This is used to perform an action on a x402 resource only once. In the case of Alice above, like buying the book only once, no subscriptions, no notifications, no signups. `once/` is followed by the x402 endpoint that will handle her request using the x402 headers defined in the x402 specification.
    ```sh
    #example
    x402://once/https://example.com/x402/publisher?Toly-The-Great&name=Conqueror-of-Blockhains-Taker-Of-Markets&version=latest-version
    ```

3. `subscribe/`

    The `subscribe/` domain is used to subscribe for certain events or services. For example, Alice can susbscribe to an AI agent for new updates on a book. The x402-URI app handles receiving updates on an new books published and can for example show push notifications on a mobile device to keep Alice informed.

    ```sh
    #example
    x402://subscribe/https://example.com/x402/topic?Solana&author=Toly-The-Great
    ```

4. `unsubscribe/`

    This domain the handles unsusbcribing from notifications and is the opposite of `subscribe/` domain. It has the same structure but uses `unsubscribe/` instead of `subscribe/`

    ```sh
    #example
    x402://subscribe/https://example.com/x402/topic?Solana&author=Toly-The-Great
    ```
## Usage Examples

### HTML Usage
A HTML a tag is used to add the x402-URI link:
```html
<a href="x402://discover/https%3A%2F%2Fexample.com%2Fx402%2Fdiscover">Explore agents</a>
``` 

When clicked from a smartphone, most browsers like Chrome and Firefox will prompt the user to open in an app that parses `x402-URI`s if the user agrees then the mobile plaftorm open the URI in the app.

### Dapp store
The dapp store for mobile app is agnostic based on the platform. For example the Solana dapp store (Android apps).
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <application
       ...
        <activity
            ...>

            <intent-filter>
                <action android:name="android.intent.action.VIEW" />

                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />

                <data
                    android:host="discover"
                    android:scheme="x402" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

Here the `<intent-filter>` has the `<data android:host="discover" android:scheme="x402" />` entry which handles the `x402://discover/...` x402-URI. This tells android that the app can open `x402://` URIs.

Other mobile platforms handle this differently so check how `iOS`, `KaiOS`, `HarmonyOS`, etc, handle opening URIs.

### QR-CODES
QR-Codes can be used in the same way.
```
x402://<domain>/<uri>
```
Example QR:

![x402-URI QR](./x402-URI.png)

> Scan this QR and share the link to Firefox or Chrome. These browsers will prompt you to open the x402-URI with an installed app that supports handling x402-URIs.
>

## x402-URI Live Updates
Platforms like `Android`, `iOS` and `HarmonyOS` implement live updates to give live feedback of ongoing events via notifications.

URLs that support server-side events like agent-to-agent communication can simply define return JSON as part of the message data in the EventStream of an EventSource in server-sent events.

```json
{
    "contentTitle": "<String>",
    "contentText": "<String>",
    "shortCriticalText": "<String>",
    "progress": {
        "point": <Int (u32::MAX)>,
        "color": "<String>"
    },
    "isProgressIndeterminate": <Boolean>,
    "actions": ["<String>"],
    "style": {
        "points": [
            {
                "point": <Int (u32::MAX)>,
                "color": "<String>"
            }
        ],
        "segments":[
            {
                "segment": <Int (u32::MAX)>,
                "color": "<String>"
            }
        ]
    }
}

```


## Summary
The x402-URI improve the user experience for ordinary users opening up x402 payments anywhere on any website, app or QR-Code in the physical world. The services with the x402 resources no longer need to spend resources making users understand x402 payments. The user just taps on a button or scans a QR code.