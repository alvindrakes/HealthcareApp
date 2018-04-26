package com.alvindrakes.loginpage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Sounds the alarm clock at the specified time by the TimePicker
 */
public class AlarmReceiver extends BroadcastReceiver {
  
  //Get time that alarm is supposed to ring
  @Override
  public void onReceive (Context context, Intent intent) {
    
    //Obtain variables from SleepTracker page
    String getString = intent.getExtras().getString("extra");
    String initialTime = intent.getExtras().getString("sleep");
    int sleepData = intent.getExtras().getInt("sleepData");
    
    Intent serviceIntent = new Intent(context, RingtonePlayingService.class);
    
    //Send variables to RingtoneService
    serviceIntent.putExtra("extra", getString);
    serviceIntent.putExtra("sleep", initialTime);
    serviceIntent.putExtra("sleepData", sleepData);
    
    //Ring the alarm
    context.startService(serviceIntent);
  }
}
