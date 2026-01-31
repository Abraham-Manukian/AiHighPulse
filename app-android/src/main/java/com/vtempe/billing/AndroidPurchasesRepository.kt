package com.vtempe.billing

import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.vtempe.shared.domain.repository.PurchasesRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AndroidPurchasesRepository(
    private val appContext: Context
) : PurchasesRepository, PurchasesUpdatedListener {

    private val billingClient: BillingClient by lazy {
        BillingClient.newBuilder(appContext)
            .setListener(this)
            .enablePendingPurchases()
            .build()
    }

    override suspend fun isSubscriptionActive(): Boolean {
        // Minimal skeleton: connect and query purchases; return false on any failure
        val connected = connect()
        if (!connected) return false
        // For brevity, we donвЂ™t query specific SKUs here; return false as a safe default.
        return false
    }

    private suspend fun connect(): Boolean = suspendCancellableCoroutine { cont ->
        if (billingClient.isReady) { cont.resume(true); return@suspendCancellableCoroutine }
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                cont.resume(result.responseCode == BillingClient.BillingResponseCode.OK)
            }
            override fun onBillingServiceDisconnected() {
                cont.resume(false)
            }
        })
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        // No-op skeleton. Integrate acknowledgment/consume later.
    }
}


