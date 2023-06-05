package com.ict.librarybooking;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ict.classes.Set_font;
import com.ict.dialogs.AddBookDialog;
import com.ict.dialogs.AddRentDialog;
import com.ict.dialogs.AddUserDialog;
import com.ict.dialogs.BuyDialog;
import com.ict.util.IabHelper;
import com.ict.util.IabResult;
import com.ict.util.Inventory;
import com.ict.util.Purchase;

import java.io.File;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import static android.os.Build.VERSION.SDK_INT;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener {


    public static final int REQUEST_CODE_PERMISSIONS = 2;
    public boolean shouldBuy = false;
    public static boolean isFullAccess = false;

    // Debug tag, for logging
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewGroup godfatherView = (ViewGroup)this.getWindow().getDecorView();;
        Set_font.setKodakFont(MainActivity.this, godfatherView);
        findViewById(R.id.btBookList).setOnClickListener(this);
        findViewById(R.id.btMemberList).setOnClickListener(this);
        findViewById(R.id.btRented).setOnClickListener(this);
        findViewById(R.id.btAddbook).setOnClickListener(this);
        findViewById(R.id.btAddMember).setOnClickListener(this);
        findViewById(R.id.btRentBook).setOnClickListener(this);
        findViewById(R.id.btImport).setOnClickListener(this);
        findViewById(R.id.btExport).setOnClickListener(this);


       SetUpBazar();

    }

    public void SetUpAndBuy()
    {
        shouldBuy = true;
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
                isFullAccess = inventory.hasPurchase(SKU_PREMIUM);;
                // update UI accordingly

                Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));
            }
            ///isFullAccess = true;
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
                isFullAccess = true;
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

    private void GetCurrentProducts()
    {

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
             mHelper = null;

    }

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE
    };

    //check permissions.
    public  void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int managePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.MANAGE_EXTERNAL_STORAGE);
        if (SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                startActivityForResult(intent, 2296);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2296);
            }
        }
        else {
            if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED ){
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                        activity,
                        PERMISSIONS_STORAGE,
                        REQUEST_CODE_PERMISSIONS
                );
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btBookList:
                startActivity(new Intent(MainActivity.this,BookListActivity.class));
                break;
            case R.id.btMemberList:
                startActivity(new Intent(MainActivity.this,MemberListActivity.class));
                break;
            case R.id.btRented:
                startActivity(new Intent(MainActivity.this,RentListActivity.class));
                break;
            case R.id.btAddbook:
                (new AddBookDialog()).ShowAddBook(MainActivity.this,null);
                break;
            case R.id.btAddMember:
                (new AddUserDialog()).ShowMemberDialog(MainActivity.this,null);
                break;
            case R.id.btRentBook:
                (new AddRentDialog()).ShowRentDialog(MainActivity.this,null);
                break;
            case R.id.btImport:
                verifyStoragePermissions(this);
                DBhelper db2 = new DBhelper(MainActivity.this);
                File DOWNLOAD_DIR2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setTitle("Backup Name in Download folder");
                final EditText input2 = new EditText(this);
                input2.setInputType(InputType.TYPE_CLASS_TEXT);
                input2.setText("Book_DB" + ".enc");
                builder2.setView(input2);
                builder2.setPositiveButton(R.string.importData, (dialog, which) -> {
                    String m_Text = input2.getText().toString();
                    String out = m_Text;
                    db2.importDB(DOWNLOAD_DIR2.getAbsolutePath()+'/'+out);
                });
                builder2.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

                builder2.show();
                break;

            case R.id.btExport:
                if (!MainActivity.isFullAccess)
                {
                    //StartBuying();
                    startActivity(new Intent(MainActivity.this, BuyDialog.class));
                    return;
                }
                verifyStoragePermissions(this);
                DBhelper db = new DBhelper(MainActivity.this);

                File DOWNLOAD_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                Log.d("Folde",DOWNLOAD_DIR.getAbsolutePath());
                boolean success = true;
               // if (!folder.exists())
                 //   success = folder.mkdirs();
                if (success)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Backup Name");
                    final EditText input = new EditText(this);
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    input.setText("Book_DB"  + ".enc");
                    builder.setView(input);
                    builder.setPositiveButton(R.string.backup, (dialog, which) -> {
                        String m_Text = input.getText().toString();
                        String out = m_Text;
                        db.backup(DOWNLOAD_DIR.getAbsolutePath()+'/'+out);
                    });
                    builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

                    builder.show();
                }

        }
    }
}