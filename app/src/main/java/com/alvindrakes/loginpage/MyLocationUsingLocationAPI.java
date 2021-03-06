package com.alvindrakes.loginpage;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alvindrakes.loginpage.LocationUtil.PermissionUtils;
import com.alvindrakes.loginpage.LocationUtil.PermissionUtils.PermissionResultCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;

/**
 * Location Service with API
 */
public class MyLocationUsingLocationAPI extends Activity implements ConnectionCallbacks,
                                                                    OnConnectionFailedListener,
                                                                    OnRequestPermissionsResultCallback,
                                                                    PermissionResultCallback {
  
  Handler handler = new Handler();
  
  Runnable runnable = new Runnable() {
    @Override
    public void run () {
      if (handler != null) handler.postDelayed(runnable, 1000);
    }
  };
  
  // LogCat tag
  private static final String TAG = "MyLocation";
  
  private final static int PLAY_SERVICES_REQUEST = 1000;
  private final static int REQUEST_CHECK_SETTINGS = 2000;
  
  private Location mLastLocation;
  
  // Google client to interact with Google API
  
  private GoogleApiClient mGoogleApiClient;
  
  double latitude;
  double longitude;
  
  // list of permissions
  
  ArrayList<String> permissions = new ArrayList<>();
  PermissionUtils permissionUtils;
  
  boolean isPermissionGranted;
  
  @Override
  protected void onCreate (Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // setContentView(R.layout.activity_location_service);
    
    ButterKnife.bind(this);
    
    //Permission for using GPS tracking
    permissionUtils = new PermissionUtils(MyLocationUsingLocationAPI.this);
    
    permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
    permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
    
    permissionUtils.check_permission(permissions,
                                     "Need GPS permission for getting your location",
                                     1);
    
    handler.postDelayed(runnable, 1000);
    
    // check availability of play services
    if (checkPlayServices()) {
      
      // Building the GoogleApi client
      buildGoogleApiClient();
    }
    
    finish();
    
  }
  
  /**
   * Method to display the location on UI
   */
  
  private void getLocation () {
    
    if (isPermissionGranted) {
      
      try {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
      } catch (SecurityException e) {
        e.printStackTrace();
      }
      
      if (mLastLocation != null) {
        latitude = mLastLocation.getLatitude();
        longitude = mLastLocation.getLongitude();
        getAddress();
        
      }
      
    }
    
  }
  
  public Address getAddress (double latitude, double longitude) {
    Geocoder geocoder;
    List<Address> addresses;
    geocoder = new Geocoder(this, Locale.getDefault());
    
    try {
      addresses = geocoder.getFromLocation(latitude,
                                           longitude,
                                           1); // Here 1 represent max location result to
      // returned, by documents it recommended 1 to 5
      return addresses.get(0);
      
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return null;
    
  }
  
  
  public void getAddress () {
    
    Address locationAddress = getAddress(latitude, longitude);
    
    if (locationAddress != null) {
      String address = locationAddress.getAddressLine(0);
      String address1 = locationAddress.getAddressLine(1);
      String city = locationAddress.getLocality();
      String state = locationAddress.getAdminArea();
      String country = locationAddress.getCountryName();
      String postalCode = locationAddress.getPostalCode();
      
      String currentLocation;
      
      if (!TextUtils.isEmpty(address)) {
        currentLocation = address;
        
        if (!TextUtils.isEmpty(address1)) currentLocation += "\n" + address1;
        
        if (!TextUtils.isEmpty(city)) {
          currentLocation += "\n" + city;
          
          if (!TextUtils.isEmpty(postalCode)) currentLocation += " - " + postalCode;
        } else {
          if (!TextUtils.isEmpty(postalCode)) currentLocation += "\n" + postalCode;
        }
        
        if (!TextUtils.isEmpty(state)) currentLocation += "\n" + state;
        
        if (!TextUtils.isEmpty(country)) currentLocation += "\n" + country;
        
/*
        tvAddress.setText(currentLocation);
        tvAddress.setVisibility(View.VISIBLE);
*/
        
        //Save into database
        User.updateLocation(currentLocation);
        
      }
      
    }
    
  }
  
  /**
   * Creating google api client object
   */
  
  protected synchronized void buildGoogleApiClient () {
    mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(LocationServices.API)
        .build();
    
    mGoogleApiClient.connect();
    
    LocationRequest mLocationRequest = new LocationRequest();
    mLocationRequest.setInterval(10000);
    mLocationRequest.setFastestInterval(5000);
    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    
    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
        .addLocationRequest(
        mLocationRequest);
    
    PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
        .checkLocationSettings(
        mGoogleApiClient,
        builder.build());
    
    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
      @Override
      public void onResult (LocationSettingsResult locationSettingsResult) {
        
        final Status status = locationSettingsResult.getStatus();
        
        switch (status.getStatusCode()) {
          case LocationSettingsStatusCodes.SUCCESS:
            // All location settings are satisfied. The client can initialize location requests here
            getLocation();
            break;
          case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
            try {
              // Show the dialog by calling startResolutionForResult(),
              // and check the result in onActivityResult().
              status.startResolutionForResult(MyLocationUsingLocationAPI.this,
                                              REQUEST_CHECK_SETTINGS);
              
            } catch (IntentSender.SendIntentException e) {
              // Ignore the error.
            }
            break;
          case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
            break;
        }
      }
    });
    
    
  }
  
  
  /**
   * Method to verify google play services on the device
   */
  
  private boolean checkPlayServices () {
    
    GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
    
    int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);
    
    if (resultCode != ConnectionResult.SUCCESS) {
      if (googleApiAvailability.isUserResolvableError(resultCode)) {
        googleApiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_REQUEST).show();
      } else {
        Toast.makeText(getApplicationContext(), "This device is not supported.", Toast.LENGTH_LONG)
            .show();
        finish();
      }
      return false;
    }
    return true;
  }
  
  
  @Override
  protected void onActivityResult (int requestCode, int resultCode, Intent data) {
    final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
    Log.e("LOCATION", "PERMISSION");
    switch (requestCode) {
      case REQUEST_CHECK_SETTINGS:
        switch (resultCode) {
          case Activity.RESULT_OK:
            // All required changes were successfully made
            getLocation();
            Log.e("location", "1");
            break;
          case Activity.RESULT_CANCELED:
            // The user was asked to change settings, but chose not to
            Log.e("location", "2");
            break;
          default:
            Log.e("location", "3");
            break;
        }
        break;
    }
  }
  
  
  @Override
  protected void onResume () {
    super.onResume();
    checkPlayServices();
  }
  
  /**
   * Google api callback methods
   */
  @Override
  public void onConnectionFailed (ConnectionResult result) {
    Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
  }
  
  @Override
  public void onConnected (Bundle arg0) {
    
    // Once connected with google api, get the location
    getLocation();
  }
  
  @Override
  public void onConnectionSuspended (int arg0) {
    mGoogleApiClient.connect();
  }
  
  
  // Permission check functions
  
  
  @Override
  public void onRequestPermissionsResult (int requestCode,
                                          @NonNull String[] permissions,
                                          @NonNull int[] grantResults) {
    // redirects to utils
    permissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    
  }
  
  
  @Override
  public void PermissionGranted (int request_code) {
    Log.i("PERMISSION", "GRANTED");
    Log.e("PERMISSION", "GRANTED");
    isPermissionGranted = true;
    // getLocation();
  }
  
  @Override
  public void PartialPermissionGranted (int request_code, ArrayList<String> granted_permissions) {
    Log.i("PERMISSION PARTIALLY", "GRANTED");
  }
  
  @Override
  public void PermissionDenied (int request_code) {
    Log.i("PERMISSION", "DENIED");
  }
  
  @Override
  public void NeverAskAgain (int request_code) {
    Log.i("PERMISSION", "NEVER ASK AGAIN");
  }
  
  public void showToast (String message) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }
  
  
}

