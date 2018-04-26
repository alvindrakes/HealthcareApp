package com.alvindrakes.loginpage;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alvindrakes.loginpage.LocationUtil.LocationHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import butterknife.ButterKnife;

/**
 * Location Service helper
 */
public class MyLocationUsingHelper extends Activity
    implements ConnectionCallbacks, OnConnectionFailedListener, OnRequestPermissionsResultCallback {
  
  
  // @BindView(R.id.tvAddress)TextView tvAddress;
  // @BindView(R.id.rlPickLocation)RelativeLayout rlPick;
  
  double latitude;
  double longitude;
  LocationHelper locationHelper;
  private Location mLastLocation;
  
  @Override
  protected void onCreate (Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // setContentView(R.layout.activity_location_service);
    
    locationHelper = new LocationHelper(this);
    locationHelper.checkpermission();
    
    ButterKnife.bind(this);
    
    // check availability of play services
    if (locationHelper.checkPlayServices()) {
      
      // Building the GoogleApi client
      locationHelper.buildGoogleApiClient();
    }
    
    finish();
    
  }
  
  
  public void getAddress () {
    Address locationAddress;
    
    locationAddress = locationHelper.getAddress(latitude, longitude);
    
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
        
        Toast.makeText(MyLocationUsingHelper.this, "Address got it", Toast.LENGTH_SHORT).show();
        
      }
      
    } else {
      showToast("Something went wrong");
    }
  }
  
  
  @Override
  protected void onActivityResult (int requestCode, int resultCode, Intent data) {
    locationHelper.onActivityResult(requestCode, resultCode, data);
  }
  
  
  @Override
  protected void onResume () {
    super.onResume();
    locationHelper.checkPlayServices();
  }
  
  /**
   * Google api callback methods
   */
  @Override
  public void onConnectionFailed (ConnectionResult result) {
    Log.i("Connection failed:", " ConnectionResult.getErrorCode() = " + result.getErrorCode());
  }
  
  @Override
  public void onConnected (Bundle arg0) {
    
    // Once connected with google api, get the location
    mLastLocation = locationHelper.getLocation();
  }
  
  @Override
  public void onConnectionSuspended (int arg0) {
    locationHelper.connectApiClient();
  }
  
  
  // Permission check functions
  @Override
  public void onRequestPermissionsResult (int requestCode,
                                          @NonNull String[] permissions,
                                          @NonNull int[] grantResults) {
    // redirects to utils
    locationHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    
  }
  
  public void showToast (String message) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }
  
  
}
