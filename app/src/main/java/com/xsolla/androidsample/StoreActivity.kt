package com.xsolla.androidsample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xsolla.android.payments.XPayments
import com.xsolla.android.store.XStore
import com.xsolla.android.store.callbacks.GetVirtualItemsCallback
import com.xsolla.android.store.entity.response.items.VirtualItemsResponse
import com.xsolla.androidsample.adapter.BuyItemAdapter

class StoreActivity : AppCompatActivity() {

    private lateinit var itemsView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store)

        val projectId: Int = -1// Your Xsolla project ID
        XStore.init(projectId)

        initUI()
        loadVirtualItems()
    }

    private fun initUI() {
        itemsView = findViewById(R.id.buy_recycler_view)
        itemsView.layoutManager = LinearLayoutManager(this)
    }

    private fun loadVirtualItems() {
        val parentActivity = this
        XStore.getVirtualItems(object : GetVirtualItemsCallback {
            override fun onSuccess(response: VirtualItemsResponse) {
                itemsView.adapter = BuyItemAdapter(parentActivity, response.items.filter { item -> item.virtualPrices.isEmpty() && !item.isFree })
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                showNotificationMessage(errorMessage ?: throwable?.javaClass?.name ?: "Error")
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            val (status, _) = XPayments.Result.fromResultIntent(data)
            when (status) {
                XPayments.Status.COMPLETED -> showNotificationMessage("Payment completed")
                XPayments.Status.CANCELLED -> showNotificationMessage("Payment canceled")
                XPayments.Status.UNKNOWN -> showNotificationMessage("Payment error")
            }
        }
    }

    private fun showNotificationMessage(message: String) {
        Toast.makeText(
            baseContext,
            message,
            Toast.LENGTH_SHORT,
        ).show()
    }
}