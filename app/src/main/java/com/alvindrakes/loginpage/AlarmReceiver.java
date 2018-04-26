package com.alvindrakes.loginpage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by super on 3/7/2018.
 */


public class AlarmReceiver extends BroadcastReceiver {
  @Override
  public void onReceive (Context context, Intent intent) {
    //Get time that alarm is supposed to ring
    Log.e("We are in the receiver", "YAY");
    
    String getString = intent.getExtras().getString("extra");
    String initialTime = intent.getExtras().getString("sleep");
    int sleepData = intent.getExtras().getInt("sleepData");
    
    Log.e("What is string", getString);
    
    Intent serviceIntent = new Intent(context, RingtonePlayingService.class);
    
    serviceIntent.putExtra("extra", getString);
    serviceIntent.putExtra("sleep", initialTime);
    serviceIntent.putExtra("sleepData", sleepData);
    //Ring the alarm
    context.startService(serviceIntent);
  }
}
