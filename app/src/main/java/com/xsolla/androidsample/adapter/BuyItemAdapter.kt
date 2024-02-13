package com.xsolla.androidsample.adapter

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xsolla.android.payments.XPayments
import com.xsolla.android.payments.data.AccessToken
import com.xsolla.android.store.entity.response.items.VirtualItemsResponse
import com.xsolla.androidsample.R
import com.xsolla.androidsample.StoreActivity
import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL


class BuyItemAdapter(private val parentActivity: StoreActivity, private val items: List<VirtualItemsResponse.Item>) :
    RecyclerView.Adapter<BuyItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyItemViewHolder {
        return BuyItemViewHolder( LayoutInflater.from(parent.context)
            .inflate(R.layout.buy_item_sample, parent, false))
    }

    override fun onBindViewHolder(holder: BuyItemViewHolder, position: Int) {
        val item = items[position]
        Glide.with(holder.view).load(item.imageUrl).into(holder.itemImage)
        holder.itemName.text = item.name
        holder.itemDescription.text = item.description
        var priceText: String
        if(item.virtualPrices.isNotEmpty()) {
            priceText = item.virtualPrices[0].getAmountRaw() + " " + item.virtualPrices[0].name
        } else {
            priceText = item.price?.getAmountRaw() + " " + item.price?.currency.toString()
        }

        holder.itemPrice.text = priceText

        holder.itemButton.setOnClickListener {
            Thread {
                purchase(item.sku!!)
            }.start()
        }
    }

    private fun purchase(sku: String) {

        val uid = parentActivity.intent.getStringExtra("uid")
        val email = parentActivity.intent.getStringExtra("email")

        val jsonBody = JSONObject()
        jsonBody.put("data", JSONObject().apply {
            put("uid", uid)
            put("email", email)
            put("sku", sku)
            put("returnUrl", "app://xpayment." + parentActivity.packageName)
        })

        val firebaseProjectId = "" //  Your Firebase project ID
        val hostName = "" //  Your host name
        val connection = URL("$hostName/$firebaseProjectId/us-central1/getXsollaPaymentToken").openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.doOutput = true

        val outputStream: OutputStream = connection.outputStream
        val writer = BufferedWriter(OutputStreamWriter(outputStream))
        writer.write(jsonBody.toString())
        writer.flush()
        writer.close()

        val responseCode = connection.responseCode

        if (responseCode == HttpURLConnection.HTTP_OK) {
            val response = connection.inputStream.bufferedReader().use(BufferedReader::readText)
            connection.disconnect()

            val jsonObject = JSONObject(response)
            val token = jsonObject.getString("token")
            val orderId = jsonObject.getString("order_id")

            val intent = XPayments.createIntentBuilder(parentActivity)
                .accessToken(AccessToken(token))
                .isSandbox(true)
                .build()
            parentActivity.startActivityForResult(intent, 1)
        } else {
            Handler(Looper.getMainLooper()).post {
                showNotificationMessage("HTTP request failed with error: $responseCode")
            }
        }
    }

    override fun getItemCount() = items.size

    private fun showNotificationMessage(message: String) {
        Toast.makeText(
            parentActivity,
            message,
            Toast.LENGTH_SHORT,
        ).show()
    }
}