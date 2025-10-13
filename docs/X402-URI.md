# x402 URI

The x402 URI allows mobile browsers on smartphones to open a URI using an installed app. It contains the `x402://` scheme, an `action`, a URI for the resource and optional `query paramenters`.  The scheme can also be embedded as a QR Code.

An example x402 URI `x402://discover/https://example.com/apiv1/new_defi_ipo`

```shell
+---------+  +-----------+  +-------------------------+  +--------------------+
| x402:// |  | discover/ |  |   https://example.com/  |  | apiv1/new_defi_ipo |
+---------+  +-----------|  +-------------------------+  +--------------------+
     ↓            ↓                  ↓                            ↓
x402 Scheme      action             URI                     query parameters
                               (a2a, http, mcp)             (API independent)

```

​	***example of a https resource URI.***

## The four actions include:

### 1. Discover

The `discover` allows a user to discover all the resources a server supports. The URI format is simple

```shell
x402://discover/<URI>/<optional query string>
```

This points to an API that implements the discovery API defined in  [`8. Discovery API`](https://github.com/coinbase/x402/blob/10e38ef73a01877f0cb1a8731c8a068e3c81d481/specs/x402-specification.md) of the x402 specification. The app fetches these resources and displays them to a user.

### 2. Subscribe

These allows a user to subscribe to a service. The app that handles x402 URIs can then add the service to it's local database. This allows the app to receive onchain or agentic notifications or perform agentic actions like authorize, sign in or revoke any further actions or micro-transactions. 

```shell
x402://subscribe/<URI>/<optional query params>?<base64 encoded Subscription Data>
```

The x402 subscribe URI  (`<base64 encoded Subscription Data>`) contains the following structure:

```json
{
    "unsubscribe": "<uri>/<unsubscribe query string>?<address>",
    "x402Payload": {
      "scheme": "exact",
      "network": "solana",
      "maxAmountRequired": "1000",
      "asset": "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v",
      "payTo": "2wKupLR9q6wXYppw8Gr2NvWxKBUqm4PPJKkQfoxHDBg4",
      "resource": "https://example.com/weather",
      "description": "Access to protected content",
      "mimeType": "application/json",
      "maxTimeoutSeconds": 60,
      "outputSchema": null,
      "extra": {
        "feePayer": "EwWqGE4ZFKLofuestmU4LDdK7XM1N4ALgdZccwYugwGd"
      }
    }
}
```

The JSON schema for this structure is:

1.  `unsubscribe` This is a `string` that contains the URI that will be called when a user no longer wishes to keep the subscription to a service. It contains the `URI` (https, a2a or mcp) and a URI query parameter that accepts the user's public key that is recorded as part of the subscription.
   Example address:

   ```
   x402://unsubscribe/https://example.com/apiv1/unsubscribe/So11111111111111111111111111111111111111112`
   ```

   

2. `x402Payload` is the `PaymentRequirements` payload described in the `x402` specification with [`402 payment required` header](https://github.com/coinbase/x402/blob/10e38ef73a01877f0cb1a8731c8a068e3c81d481/specs/schemes/exact/scheme_exact_svm.md#paymentrequirements-for-exact) . This payload will be deserialized according to the x402 specification and a payment will be sent to the resource server address.

### 3. Unsubscribe

The registers a URI where a user can send unsubscribe to the service. This concept is similar to email unsubscribe allowing users to no longer express interest in a service.

```shell
x402://unsubscribe/<URI>/<unsubscribe API>?<address>
```

example: 

`x402://unsubscribe/https://example.com/apiv1/unsubscribe/So11111111111111111111111111111111111111112`

### 4. Once

This allows a user to perform a one off action on a service, no subscriptions necessary. 

```shell
x402://once/<URI>/<base64 encoded PaymentRequirements from x402 specification>
```

The URI can be https, mcp or a2a.

The `<base64 encoded PaymentRequirements from x402 specification>` is the base64 encoded  `PaymentRequirements` payload described in the `x402` specification with [`402 payment required` header](https://github.com/coinbase/x402/blob/10e38ef73a01877f0cb1a8731c8a068e3c81d481/specs/schemes/exact/scheme_exact_svm.md#paymentrequirements-for-exact) .

```json
{
  "scheme": "exact",
  "network": "solana",
  "maxAmountRequired": "1000",
  "asset": "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v",
  "payTo": "2wKupLR9q6wXYppw8Gr2NvWxKBUqm4PPJKkQfoxHDBg4",
  "resource": "https://example.com/weather",
  "description": "Access to protected content",
  "mimeType": "application/json",
  "maxTimeoutSeconds": 60,
  "outputSchema": null,
  "extra": {
    "feePayer": "EwWqGE4ZFKLofuestmU4LDdK7XM1N4ALgdZccwYugwGd"
  }
}
```



## Considerations

The x402 URI is a simple URI specification that allows apps to build on top of the x402 protocol for human, MCP and agent-to-agent communication.

Security consideration are left to the app implementing the x402 URI specification.

## LICENSE

Apache-2.0







