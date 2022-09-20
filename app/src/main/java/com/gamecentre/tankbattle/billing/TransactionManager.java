package com.gamecentre.tankbattle.billing;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.gamecentre.tankbattle.utils.MessageRegister;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class TransactionManager {

    private static TransactionManager instance;
    private BillingClient billingClient;
    private ProductDetails productDetails;
    private Purchase purchase;
    private  Activity activity;

    static final String TAG = "InAppPurchaseTag";

    public TransactionManager () {

    }

    public static synchronized TransactionManager getInstnce() {
        if(instance == null) {
            instance = new TransactionManager();
        }
        return instance;
    }

    public void billingSetup(Activity activity) {
        billingClient = BillingClient.newBuilder(activity)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        billingClient.startConnection(new BillingClientStateListener() {

            @Override
            public void onBillingSetupFinished(
                    @NonNull BillingResult billingResult) {

                if (billingResult.getResponseCode() ==
                        BillingClient.BillingResponseCode.OK) {
                    Log.i(TAG, "OnBillingSetupFinish connected");
                    queryProduct(activity);
                } else {
                    Log.i(TAG, "OnBillingSetupFinish failed");
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.i(TAG, "OnBillingSetupFinish connection lost");
            }
        });
    }

    private final PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult,
                                       List<Purchase> purchases) {

            if (billingResult.getResponseCode() ==
                    BillingClient.BillingResponseCode.OK
                    && purchases != null) {
                for (Purchase purchase : purchases) {
                    completePurchase(purchase);
                }
            } else if (billingResult.getResponseCode() ==
                    BillingClient.BillingResponseCode.USER_CANCELED) {
                Log.i(TAG, "onPurchasesUpdated: Purchase Canceled");
            } else {
                Log.i(TAG, "onPurchasesUpdated: Error");
            }
        }
    };

    private void queryProduct(Activity activity) {

        QueryProductDetailsParams queryProductDetailsParams =
                QueryProductDetailsParams.newBuilder()
                        .setProductList(
                                ImmutableList.of(
                                        QueryProductDetailsParams.Product.newBuilder()
                                                .setProductId("gold_200")
                                                .setProductType(
                                                        BillingClient.ProductType.INAPP)
                                                .build()))
                        .build();

        billingClient.queryProductDetailsAsync(
                queryProductDetailsParams,
                new ProductDetailsResponseListener() {
                    public void onProductDetailsResponse(
                            @NonNull BillingResult billingResult,
                            @NonNull List<ProductDetails> productDetailsList) {

                        if (!productDetailsList.isEmpty()) {
                            productDetails = productDetailsList.get(0);
                            activity.runOnUiThread(() -> {
                                //TODO Product is available
                                Log.d("QUERY", "Product is available");
//                                Toast.makeText(activity, "Product is available", Toast.LENGTH_SHORT).show();
//                                binding.buyButton.setEnabled(true);
//                                binding.statusText.setText(productDetails.getName());
                            });
                        } else {
                            Log.i(TAG, "onProductDetailsResponse: No products");
                        }
                    }
                }
        );
    }

    public void makePurchase(Activity activity) {

        this.activity = activity;

        BillingFlowParams billingFlowParams =
                BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(
                                ImmutableList.of(
                                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                                .setProductDetails(productDetails)
                                                .build()
                                )
                        )
                        .build();

        billingClient.launchBillingFlow(this.activity, billingFlowParams);
    }

    private void completePurchase(Purchase item) {

        purchase = item;


        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED)
            activity.runOnUiThread(() -> {
                //TODO Purchase successful
                Log.d("PURCHASE", "Purchase successful");
//                Toast.makeText(this.activity, "Purchase successful", Toast.LENGTH_SHORT).show();
                MessageRegister.getInstance().registerTransactionListener();
//                binding.consumeButton.setEnabled(true);
//                binding.statusText.setText("Purchase Complete");
            });
    }

    //Should be called once purchase is successful

    public void consumePurchase() {
        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();

        ConsumeResponseListener listener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult,
                                          @NonNull String purchaseToken) {
                if (billingResult.getResponseCode() ==
                        BillingClient.BillingResponseCode.OK) {
                    TransactionManager.this.activity.runOnUiThread(() -> {
//                        binding.consumeButton.setEnabled(false);
//                        binding.statusText.setText("Purchase consumed");
                    });
                }
            }
        };
        billingClient.consumeAsync(consumeParams, listener);
    }


}
