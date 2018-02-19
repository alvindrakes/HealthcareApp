package com.alvindrakes.loginpage;

/**
 * Created by Cheng Yee on 2/6/2018.
 */
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.fitness.FitnessStatusCodes;

public class ActivityOfGoogleFit extends AppCompatActivity
{
    public final static String TAG = "GoogleFitService";
    private ConnectionResult mFitResultResolution;
    private static final String AUTH_PENDING = "Authenticating pending";
    private boolean authInProgress = false;
    private static final int REQUEST_OAUTH = 1431;
    private Button mConnectButton;
    private Button mGetStepsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

//        mConnectButton = (Button)findViewById(R.id.connectBtn);
//        mConnectButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                handleConnectButton();
//            }
//        });
//
//        mGetStepsButton = (Button)findViewById(R.id.getStepsBtn);
//        mGetStepsButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                handleGetStepsButton();
//            }
//        });

        LocalBroadcastManager.getInstance(this).registerReceiver(mFitStatusReceiver, new IntentFilter(ServiceOfGoogleFit.FIT_NOTIFY_INTENT));
        LocalBroadcastManager.getInstance(this).registerReceiver(mFitDataReceiver, new IntentFilter(ServiceOfGoogleFit.HISTORY_INTENT));

        requestFitConnection();
    }//end of onCreate

    private void handleConnectButton()
    {
        try {
            authInProgress = true;
            mFitResultResolution.startResolutionForResult(ActivityOfGoogleFit.this, REQUEST_OAUTH);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG,
                    "Activity Thread Google Fit Exception while starting resolution activity", e);
        }
    }//end of method handleConnectButton

    private void handleGetStepsButton()
    {
        //Start Service and wait for broadcast
        Intent service = new Intent(this, ServiceOfGoogleFit.class);

        service.putExtra(ServiceOfGoogleFit.SERVICE_REQUEST_TYPE, ServiceOfGoogleFit.TYPE_GET_STEP_TODAY_DATA);
        startService(service);
    }//end of method handle GetStepsButton

    private void requestFitConnection()
    {
        Intent service = new Intent(this, ServiceOfGoogleFit.class);
        service.putExtra(ServiceOfGoogleFit.SERVICE_REQUEST_TYPE, ServiceOfGoogleFit.TYPE_REQUEST_CONNECTION);
        startService(service);
    }//end of method requestFitConnection

    private BroadcastReceiver mFitStatusReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            if (intent.hasExtra(ServiceOfGoogleFit.FIT_EXTRA_NOTIFY_FAILED_STATUS_CODE) &&
                    intent.hasExtra(ServiceOfGoogleFit.FIT_EXTRA_NOTIFY_FAILED_STATUS_CODE)) {
                //Recreate the connection result
                int statusCode = intent.getIntExtra(ServiceOfGoogleFit.FIT_EXTRA_NOTIFY_FAILED_STATUS_CODE, 0);
                PendingIntent pendingIntent = intent.getParcelableExtra(ServiceOfGoogleFit.FIT_EXTRA_NOTIFY_FAILED_INTENT);
                ConnectionResult result = new ConnectionResult(statusCode, pendingIntent);
                Log.d(TAG, "Fit connection failed - opening connect screen.");
                fitHandleFailedConnection(result);
            }
            if (intent.hasExtra(ServiceOfGoogleFit.FIT_EXTRA_CONNECTION_MESSAGE)) {
                Log.d(TAG, "Fit connection successful - closing connect screen if it's open.");
                fitHandleConnection();
            }
        }
    };

    //This would typically go into fragment.
    private BroadcastReceiver mFitDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            if (intent.hasExtra(ServiceOfGoogleFit.HISTORY_EXTRA_STEPS_TODAY)) {

                final int totalSteps = intent.getIntExtra(ServiceOfGoogleFit.HISTORY_EXTRA_STEPS_TODAY, 0);
                Toast.makeText(ActivityOfGoogleFit.this, "Total Steps: " + totalSteps, Toast.LENGTH_SHORT).show();

            }
        }
    };

    private void fitHandleConnection()
    {
        Toast.makeText(this, "Fit connected", Toast.LENGTH_SHORT).show();
        mConnectButton.setEnabled(false);
        mGetStepsButton.setEnabled(true);
    }//end of method fitHandleConnection

    private void fitHandleFailedConnection(ConnectionResult result)
    {
        Log.i(TAG, "Activity Thread Google Fit Connection failed. Cause: " + result.toString());
        if (!result.hasResolution())
        {
            // Show the localized error dialog
            GoogleApiAvailability.getInstance().getErrorDialog(ActivityOfGoogleFit.this, result.getErrorCode(),  0).show();
            return;
        }

        // The failure has a resolution. Resolve it.
        // Called typically when the app is not yet authorized, and an authorization dialog is displayed to the user.
        if (!authInProgress) {
            if (result.getErrorCode() == FitnessStatusCodes.NEEDS_OAUTH_PERMISSIONS) {
                try {
                    Log.d(TAG, "Google Fit connection failed with OAuth failure.  Trying to ask for consent (again)");
                    result.startResolutionForResult(ActivityOfGoogleFit.this, REQUEST_OAUTH);
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Activity Thread Google Fit Exception while starting resolution activity", e);
                }
            } else {

                Log.i(TAG, "Activity Thread Google Fit Attempting to resolve failed connection");

                mFitResultResolution = result;
                mConnectButton.setEnabled(true);
            }
        }
    }//end of method fitHandleFailedConnection
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        fitSaveInstanceState(outState);
    }

    private void fitSaveInstanceState(Bundle outState) {
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        fitActivityResult(requestCode, resultCode);
    }

    private void fitActivityResult(int requestCode, int resultCode) {
        if (requestCode == REQUEST_OAUTH) {
            authInProgress = false;
            if (resultCode == Activity.RESULT_OK) {
                //Ask the service to reconnect.
                Log.d(TAG, "Fit auth completed.  Asking for reconnect.");
                requestFitConnection();

            } else {
                try {
                    authInProgress = true;
                    mFitResultResolution.startResolutionForResult(ActivityOfGoogleFit.this, REQUEST_OAUTH);

                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG,
                            "Activity Thread Google Fit Exception while starting resolution activity", e);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mFitStatusReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mFitDataReceiver);

        super.onDestroy();
    }

}
