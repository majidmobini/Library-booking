package com.ict.dialogs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.ict.classes.Set_font;
import com.ict.librarybooking.MainActivity;
import com.ict.librarybooking.R;
import com.ict.util.IabHelper;
import com.ict.util.IabResult;
import com.ict.util.Inventory;
import com.ict.util.Purchase;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BuyDialog extends AppCompatActivity {

    static final String TAG = "";

    // SKUs for our products: the premium upgrade (non-consumable)
    static final String SKU_PREMIUM = "One_time_all";

    // Does the user have the premium upgrade?
    boolean mIsPremium = false;

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 5;

    // The helper object
    com.ict.util.IabHelper mHelper;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buy_activity);

        ViewGroup godfatherView = (ViewGroup)this.getWindow().getDecorView();;
        Set_font.setKodakFont(BuyDialog.this, godfatherView);

        findViewById(R.id.btOkbuy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        SetUpBazar();


    }

    private void SetUpBazar()
    {
        String base64EncodedPublicKey = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwDTPVyTVztIAAqeAHjHvYVN4MtqrbkIDqrlTx/mrWr3ymFv8fehg4/ZEKjNWHJwrFvtcD5D/ZI2c4+m/UaNGYO4OQaKLDYFbEcZBfXSQo1a/ms34wdHf8aBJ3Mnii380BBVEJBpE4kJ1TJwWf2NlD9NkEgl/CJJIO30kX5LzmBEJNDXcMibGmR6YfCvDAZBlxDwDFF/5441DSkCmEhKgUEUtvNE67eCaT44IjhdK5ECAwEAAQ==";

        mHelper = new com.ict.util.IabHelper(this, base64EncodedPublicKey);
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new com.ict.util.IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(com.ict.util.IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.d(TAG, "Problem setting up In-app Billing: " + result);
                }
                // Hooray, IAB is fully set up!
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });
    }

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");
            if (result.isFailure()) {
                Log.d(TAG, "Failed to query inventory: " + result);
                return;
            }
            else {
                Log.d(TAG, "Query inventory was successful.");
                // does the user have the premium upgrade?
                mIsPremium = inventory.hasPurchase(SKU_PREMIUM);
                MainActivity.isFullAccess = inventory.hasPurchase(SKU_PREMIUM);;
                if (!mIsPremium)
                {
                    StartBuying();
                }
                // update UI accordingly

                Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));
            }

            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (result.isFailure()) {
                Log.d(TAG, "Error purchasing: " + result);
                return;
            }
            else if (purchase.getSku().equals(SKU_PREMIUM)) {
                // give user access to premium content and update the UI
                mIsPremium = true;
                MainActivity.isFullAccess = true;
            }

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");

        }
    }

    public  void StartBuying()
    {//One_time_all
        mHelper.launchPurchaseFlow(this, SKU_PREMIUM, RC_REQUEST, mPurchaseFinishedListener, "payload-string");
    }
}
