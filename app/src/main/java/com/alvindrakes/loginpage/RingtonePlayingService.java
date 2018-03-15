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
  
  // int startId;
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
  
    Log.e("in the service","start command");
    
    String state = intent.getExtras().getString("extra");
  
    switch (state) {
      case "alarm on":
        startId = 1;
        sleepTime = Long.parseLong(intent.getExtras().getString("sleep"));
        Log.e("time started", String.valueOf(sleepTime));
        break;
      case "alarm off":
        startId = 0;
        awakeTime = Calendar.getInstance().getTimeInMillis();
        break;
      default:
        startId = 0;
        break;
    }
  
    //alarm not ringing and alarm on button pressed
    if(!this.isRunning && startId == 1){
      mediaPlayer = MediaPlayer.create(this, R.raw.sexiest_romantic_mp3);
      mediaPlayer.start();
      
      this.isRunning = true;
  
    }
    //alarm ringing and alarm off button pressed
    else if (this.isRunning && startId == 0){
      mediaPlayer.stop();
      mediaPlayer.reset();
      
      this.isRunning = false;
      Log.e("Time", String.valueOf(((awakeTime-sleepTime)/1000)));
      int sleepData = (int)((awakeTime-sleepTime)/1000);
      if (sleepData != 0) { //testing purpose
      // if (sleepData > 300){
        StatisticData dataValue = new StatisticData();
        dataValue.setSleepData(intent.getExtras().getInt("sleepData") + sleepData);
        User.updateSleep(dataValue.getSleepData());
        StatisticData.updateData(dataValue, date, "sleep");
      }
    }
    //alarm not ringing and off button pressed
    else if (!this.isRunning && startId == 0){
    
      this.isRunning = false;
  
    } else {
      
      this.isRunning = true;
  
    }
    
  
    return START_NOT_STICKY;
  }
  
  @Override
  public void onDestroy () {
    super.onDestroy();
    Toast.makeText(this, "On destroy called", Toast.LENGTH_SHORT).show();
    this.isRunning = false;
  }
}
