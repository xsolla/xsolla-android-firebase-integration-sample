This is a sample Android application that shows combined use of Firebase authentication and Xsolla Pay Station.

The sample web application is created using the following tools: 


* Development tools: [Next.js](https://nextjs.org/) framework
* Xsolla SDKs for Android:
    * com.xsolla.android:store:latest.release
    * com.xsolla.android:payments:latest.release
* Other libraries:
    * com.google.firebase:firebase-bom:32.7.1
    * com.google.firebase:firebase-auth:22.3.1
    * com.github.bumptech.glide:glide:4.12.0

To create an order with user and item data on Xsolla side, the sample application calls the cloud function that uses the [Create payment token for purchase](https://developers.xsolla.com/api/igs-bb/operation/admin-create-payment-token/) server API call. This call returns a payment token, which is required to open the payment UI and make a purchase.

To learn more, see [How to use Pay Station in combination with Firebase authentication](https://developers.xsolla.com/sdk/android/baas-integrations/how-to-use-pay-station-with-firebase/).

