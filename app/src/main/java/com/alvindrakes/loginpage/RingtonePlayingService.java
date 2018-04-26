package com.alvindrakes.loginpage;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by super on 3/7/2018.
 */

public class RingtonePlayingService extends Service {
  
  // Ringtone variables
  MediaPlayer mediaPlayer;
  boolean isRunning;
  private static long sleepTime;
  private static long awakeTime;
  
  String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
  
  @Nullable
  @Override
  public IBinder onBind (Intent intent) {
    return null;
  }
  
  @Override
  public int onStartCommand (Intent intent, int flags, int startId) {
    
    String state = intent.getExtras().getString("extra");
    
    switch (state) {
      //State when alarm is turned on
      case "alarm on":
        startId = 1;
        sleepTime = Long.parseLong(intent.getExtras().getString("sleep"));
        break;
      //State when alarm is turned off
      case "alarm off":
        startId = 0;
        awakeTime = Calendar.getInstance().getTimeInMillis();
        break;
      default:
        startId = 0;
        break;
    }
    
    //alarm not ringing and alarm on button pressed
    if (!this.isRunning && startId == 1) {
      mediaPlayer = MediaPlayer.create(this, R.raw.sexiest_romantic_mp3);
      mediaPlayer.start();
      
      this.isRunning = true;
      
    }
    //alarm ringing and alarm off button pressed
    else if (this.isRunning && startId == 0) {
      mediaPlayer.stop();
      mediaPlayer.reset();
      
      this.isRunning = false;
      
      //Calculate sleeping hours
      int sleepData = (int) ((awakeTime - sleepTime) / 1000);
      if (sleepData != 0) {
        // if (sleepData > 300){
        StatisticData dataValue = new StatisticData();
        dataValue.setSleepData(intent.getExtras().getInt("sleepData") + sleepData);
        
        //Update firebase
        User.updateSleep(dataValue.getSleepData());
        StatisticData.updateData(dataValue, date, "sleep");
        
        //Increase coins if sleep more than 7 hours
        if (sleepData > 420) User.updateCoin(intent.getExtras().getInt("coin") + 500);
      }
    }
    //alarm not ringing and off button pressed
    else if (!this.isRunning && startId == 0) {
      
      this.isRunning = false;
      
    } else {
      
      this.isRunning = true;
      
    }
    
    
    return START_NOT_STICKY;
  }
  
  @Override
  public void onDestroy () {
    super.onDestroy();
    this.isRunning = false;
  }
}
