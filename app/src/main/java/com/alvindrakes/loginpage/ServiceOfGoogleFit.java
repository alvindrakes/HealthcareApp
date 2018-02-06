package com.alvindrakes.loginpage;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Cheng Yee on 2/6/2018.
 */

public class ServiceOfGoogleFit extends IntentService {
    public static final String TAG = "GoogleFitService";
    private GoogleApiClient mGoogleApiFitnessClient;
    private boolean mTryingToConnect = false;

    public static final String SERVICE_REQUEST_TYPE = "requestType";
    public static final int TYPE_GET_STEP_TODAY_DATA = 1;
    public static final int TYPE_REQUEST_CONNECTION = 2;
    public static final int TYPE_GET_STEP_MONTH_DATA = 3;

    public static final int TYPE_GET_RAW_STEP_MONTH_DATA = 4;

    public static final String HISTORY_INTENT = "fitHistory";
    public static final String HISTORY_EXTRA_STEPS_TODAY = "stepsToday";

    public static final String HISTORY_OVERTIME_INTENT = "fitHistory";
    public static final String HISTORY_EXTRA_STEPS_MONTH = "stepsMonth";

    public static final String FIT_NOTIFY_INTENT = "fitStatusUpdateIntent";
    public static final String FIT_EXTRA_CONNECTION_MESSAGE = "fitFirstConnection";
    public static final String FIT_EXTRA_NOTIFY_FAILED_STATUS_CODE = "fitExtraFailedStatusCode";
    public static final String FIT_EXTRA_NOTIFY_FAILED_INTENT = "fitExtraFailedIntent";

    private String last;

    @Override
    public void onDestroy() {
        Log.d(TAG, "GoogleFitService destroyed");
        if (mGoogleApiFitnessClient.isConnected()) {
            Log.d(TAG, "Disconecting Google Fit.");
            mGoogleApiFitnessClient.disconnect();
        }
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        buildFitnessClient();
        Log.d(TAG, "GoogleFitService created");
    }

    public ServiceOfGoogleFit()
    {
        super("GoogleFitService");
    }//constructor

    @Override
    protected void onHandleIntent(Intent intent) {

        //Get the request type
        int type = intent.getIntExtra(SERVICE_REQUEST_TYPE, -1);
        last = intent.getStringExtra("lastActiveDate");

        //block until google fit connects.  Give up after 10 seconds.
        if (!mGoogleApiFitnessClient.isConnected()) {
            mTryingToConnect = true;
            mGoogleApiFitnessClient.connect();

            //Wait until the service either connects or fails to connect
            while (mTryingToConnect) {
                try {
                    Thread.sleep(100, 0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        if (mGoogleApiFitnessClient.isConnected()) {
            if (type == TYPE_GET_STEP_TODAY_DATA) {
                Log.d(TAG, "Requesting steps from Google Fit");
                getStepsToday();
                Log.d(TAG, "Fit update complete.  Allowing Android to destroy the service.");
            } else if (type == TYPE_REQUEST_CONNECTION) {
                //Don't need to do anything because the connection is already requested above
            } else if (type == TYPE_GET_STEP_MONTH_DATA) {
                Log.d(TAG, "Get one month's steps");
                getStepsMonth();
            } else if (type == TYPE_GET_RAW_STEP_MONTH_DATA) {
                getMonthRawData();
            }
        }else{
            //Not connected
            Log.w(TAG, "Fit wasn't able to connect, so the request failed.");
        }
    }//end of method onHandleIntent

    private void getStepsToday() {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);

        long endTime = cal.getTimeInMillis();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        long startTime = cal.getTimeInMillis();

        final DataReadRequest readRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_STEP_COUNT_DELTA)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        DataReadResult dataReadResult =
                Fitness.HistoryApi.readData(mGoogleApiFitnessClient, readRequest).await(1, TimeUnit.MINUTES);

        DataSet stepData = dataReadResult.getDataSet(DataType.TYPE_STEP_COUNT_DELTA);

        int totalSteps = 0;

        for (DataPoint dp : stepData.getDataPoints()) {
            for(Field field : dp.getDataType().getFields()) {
                int steps = dp.getValue(field).asInt();

                totalSteps += steps;

            }
        }

        publishTodaysStepData(totalSteps);
    }//end of method getStepsToday

    private void publishTodaysStepData(int totalSteps)
    {
        Intent intent = new Intent(HISTORY_INTENT);
        // You can also include some extra data.
        intent.putExtra(HISTORY_EXTRA_STEPS_TODAY, totalSteps);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }//end of method publishTodaysStepData


    //HISTORY_EXTRA_STEPS_MONTH
    private void getStepsMonth() {
        Log.i(TAG, "In getStep class");
        Calendar eTime = Calendar.getInstance();
        eTime.setTimeInMillis(System.currentTimeMillis());
        eTime.set(Calendar.HOUR_OF_DAY, 00);
        eTime.set(Calendar.MINUTE, 00);
        eTime.set(Calendar.SECOND, 0);
        eTime.set(Calendar.MILLISECOND, 0);


        long endTime = eTime.getTimeInMillis();
        eTime.add(Calendar.WEEK_OF_YEAR, -4);
        long startTime = eTime.getTimeInMillis();

        DataReadRequest readreq = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();
        Log.i(TAG, "After Request");

        DataReadResult dataReadResult = Fitness.HistoryApi.readData(mGoogleApiFitnessClient, readreq).await(1, TimeUnit.MINUTES);
        Log.i(TAG, "After intial pending result");

        ArrayList<historySet> mHistoryList= new ArrayList<historySet>();
        SimpleDateFormat myFmt1=new SimpleDateFormat("MM/dd");

        DateFormat dateFormat = DateFormat.getDateInstance();
        DateFormat timeFormat = DateFormat.getTimeInstance();

        //Used for aggregated data
        if (dataReadResult.getBuckets().size() > 0) {
            Log.e("History", "Number of buckets: " + dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {
                Log.e("History", "Each Bucket");
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    Log.e("History", "Each DataSet");
                    //Process DataPoint
                    for (DataPoint dp : dataSet.getDataPoints()) {
                        Log.e("History", "Each DataPoint");
                        Log.e("History", "Data point:");
                        Log.e("History", "\tType: " + dp.getDataType().getName());
                        Log.e("History", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                        Log.e("History", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                        String start = myFmt1.format(dp.getStartTime(TimeUnit.MILLISECONDS));
                        //end= myFmt1.format(dp.getEndTime(TimeUnit.MILLISECONDS));
                        for(Field field : dp.getDataType().getFields()) {
                            Log.e("History", "Each Field");
                            Log.e("History", "\tField: " + field.getName() +
                                    " Value: " + dp.getValue(field));
                            historySet history = new historySet();
                            history.setTm(start);
                            Log.i("History", "Time="+ history.getTm());
                            history.setStps(dp.getValue(field).toString());
                            Log.i("History", "Steps="+ history.getStps());
                            mHistoryList.add(history);

                            int size =mHistoryList.size();
                            //mBundle.putString(start,dp.getValue(field).toString());
                            historySet temp;
                            for (int i =0; i< size; i++) {
                                temp =mHistoryList.get(i);
                                Log.i("History", "Key=" + temp.getTm() + ", content=" +temp.getStps());
                            }
                        }
                    }
                }
            }
        }
        publishMonthsStepData(mHistoryList);
    }//end of method getStepsMonth

    private void publishMonthsStepData(ArrayList<historySet> arr)
    {
        Intent intent = new Intent(HISTORY_OVERTIME_INTENT);
        intent.putExtra(HISTORY_EXTRA_STEPS_MONTH, arr);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }//end of method publisMonthsStepData

    public String getDate(){
        SimpleDateFormat myFmt1=new SimpleDateFormat("dd-MM-yyyy");
        //DateFormat dateFormat = DateFormat.getDateInstance();
        String date = myFmt1.format(new    java.util.Date());
        Log.e("getDate", date);
        return date;
    };

    //HISTORY_EXTRA_STEPS_MONTH
    private void getMonthRawData() {
        Log.i("Raw", "In getMonthRawData class");
        Calendar cc = Calendar.getInstance();
        Date now = new Date(System.currentTimeMillis() );
        cc.setTime(now);

        Calendar eTime = Calendar.getInstance();
        eTime.setTimeInMillis(System.currentTimeMillis());
        eTime.set(Calendar.HOUR_OF_DAY, 00);
        eTime.set(Calendar.MINUTE, 00);
        eTime.set(Calendar.SECOND, 0);
        eTime.set(Calendar.MILLISECOND, 1);

        Log.i("Timeqqq", "calendar type" + eTime.toString());
        Log.i("Timeqqq", "date type" + eTime.getTime().toString());
        long endTime = eTime.getTimeInMillis();
        eTime.add(Calendar.WEEK_OF_YEAR, -4);
        long startTime = eTime.getTimeInMillis();

        Log.i("Timeqqq", "sTART TIME UN PARSED " + String.valueOf(startTime));
        Log.i("Timeqqq", "intent last " + last);
        SimpleDateFormat onlyDate = new SimpleDateFormat("dd-MM-yyyy");
        if (!last.equals("empty")&&!last.equals("")) {
            try {
                //turn "last"  string into date
                Date date = onlyDate.parse(last);
                Log.i("Timeqqq", "DATA CONVERT" + date.toString());
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                Log.i("Timeqqq", "Calendar CONVERT" + cal.toString());

                startTime = cal.getTimeInMillis();
                Log.i("Timeqqq", "Starttime millions CONVERT" + String.valueOf(startTime));
                Log.i("Timeqqq", "CurrentTime millions CONVERT " + String.valueOf(System.currentTimeMillis()));
                Log.i("Timeqqq", "CurrentTime Dte CONVERT " + cc.getTime().toString());
            } catch (java.text.ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (startTime < System.currentTimeMillis()) {
            DataReadRequest readreq = new DataReadRequest.Builder()
                    .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                    .bucketByTime(1, TimeUnit.HOURS)
                    .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                    .build();
            Log.i("Raw", "After Request");

            DataReadResult dataReadResult =
                    Fitness.HistoryApi.readData(mGoogleApiFitnessClient, readreq).await(1, TimeUnit.MINUTES);

            Log.i("Raw", "After intial pending result");

            ArrayList<historySet> mHistoryList= new ArrayList<historySet>();
            SimpleDateFormat myFmt1=new SimpleDateFormat("dd-MM-yyyy HH:mm");
            //Used for aggregated data
            if (dataReadResult.getBuckets().size() > 0) {
                Log.e("Raw", "Number of buckets: " + dataReadResult.getBuckets().size());
                for (Bucket bucket : dataReadResult.getBuckets()) {
                    Log.e("Raw", "Each Bucket");
                    List<DataSet> dataSets = bucket.getDataSets();
                    for (DataSet dataSet : dataSets) {
                        Log.e("Raw", "Each DataSet");
                        //Process DataPoint
                        for (DataPoint dp : dataSet.getDataPoints()) {
                            Log.e("Raw", "Each DataPoint");
                            Log.e("Raw", "Data point:");
                            String start = myFmt1.format(dp.getStartTime(TimeUnit.MILLISECONDS));
                            //end= myFmt1.format(dp.getEndTime(TimeUnit.MILLISECONDS));
                            for(Field field : dp.getDataType().getFields()) {
                                Log.e("Raw", "Each Field");
                                Log.e("Raw", "\tField: " + field.getName() +
                                        " Value: " + dp.getValue(field));
                                historySet history = new historySet();
                                history.setTm(start);
                                Log.i("Raw", "Time="+ history.getTm());
                                history.setStps(dp.getValue(field).toString());
                                Log.i("Raw", "Steps="+ history.getStps());
                                mHistoryList.add(history);

                            }
                        }
                    }
                }
            }

            //for superlws1
           /* dbRef = mDatabase.getInstance().getReference("userDailyRawData");
            int size =mHistoryList.size();
            //mBundle.putString(start,dp.getValue(field).toString());
            historySet temp;
            for (int i =0; i< size; i++) {
                temp =mHistoryList.get(i);
                Log.i("Raw", "Key=" + temp.getTm() + ", content=" +temp.getStps());

                dbRef.child(getUid()).child(temp.getTm()).setValue(temp.getStps());
            }
            //setLastActiveDate
            db.getInstance().getReference("user").child(getUid()).child("lastActiveDate").setValue(getDate());*/
        }
    }//end of method getmonthrawdata

    private void buildFitnessClient() {
        // Create the Google API Client
        mGoogleApiFitnessClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {

                            @Override
                            public void onConnected(Bundle bundle) {
                                Log.i(TAG, "Google Fit connected.");
                                mTryingToConnect = false;
                                Log.d(TAG, "Notifying the UI that we're connected.");
                                notifyUiFitConnected();

                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                // If your connection to the sensor gets lost at some point,
                                // you'll be able to determine the reason and react to it here.
                                mTryingToConnect = false;
                                if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.i(TAG, "Google Fit Connection lost.  Cause: Network Lost.");
                                } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.i(TAG, "Google Fit Connection lost.  Reason: Service Disconnected");
                                }
                            }
                        }
                )
                .addOnConnectionFailedListener(
                        new GoogleApiClient.OnConnectionFailedListener() {
                            // Called whenever the API client fails to connect.
                            @Override
                            public void onConnectionFailed(ConnectionResult result) {
                                mTryingToConnect = false;
                                notifyUiFailedConnection(result);
                            }
                        }
                )
                .build();
    }

    private void notifyUiFitConnected() {
        Intent intent = new Intent(FIT_NOTIFY_INTENT);
        intent.putExtra(FIT_EXTRA_CONNECTION_MESSAGE, FIT_EXTRA_CONNECTION_MESSAGE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void notifyUiFailedConnection(ConnectionResult result) {
        Intent intent = new Intent(FIT_NOTIFY_INTENT);
        intent.putExtra(FIT_EXTRA_NOTIFY_FAILED_STATUS_CODE, result.getErrorCode());
        intent.putExtra(FIT_EXTRA_NOTIFY_FAILED_INTENT, result.getResolution());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}