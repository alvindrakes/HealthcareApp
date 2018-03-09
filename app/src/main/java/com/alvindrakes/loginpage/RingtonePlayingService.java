package com.alvindrakes.loginpage;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by super on 3/7/2018.
 */

public class RingtonePlayingService extends Service {
  
  // int startId;
  MediaPlayer mediaPlayer;
  boolean isRunning;
  
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
        break;
      case "alarm off":
        startId = 0;
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
