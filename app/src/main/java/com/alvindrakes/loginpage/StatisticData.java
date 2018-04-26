package com.alvindrakes.loginpage;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by super on 1/26/2018.
 */

//Get user statistical data
public class StatisticData {
  private int steps;
  private int sleepData;
  private int caffeine;
  
  public int getSteps () {
    return steps;
  }
  
  public void setSteps (int steps) {
    this.steps = steps;
  }
  
  public int getSleepData () {
    return sleepData;
  }
  
  public void setSleepData (int sleepData) {
    this.sleepData = sleepData;
  }
  
  public int getCaffeine () {
    return caffeine;
  }
  
  public void setCaffeine (int caffeine) {
    this.caffeine = caffeine;
  }
  
  //Update corresponding data in firebase
  public static void updateData (StatisticData data, String day, String type) {
    
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    
    Map<String, Object> childUpdates = new HashMap<>();
    
    if (type.equals("steps")) {
      childUpdates.put("/users/" + firebaseUser.getUid() + "/data/" + day + "/steps/",
                       data.getSteps());
    } else if (type.equals("sleep")) {
      childUpdates.put("/users/" + firebaseUser.getUid() + "/data/" + day + "/sleep/",
                       data.getSleepData());
    } else if (type.equals("caffeine")) {
      childUpdates.put("/users/" + firebaseUser.getUid() + "/data/" + day + "/caffeine/",
                       data.getCaffeine());
    }
    
    FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
    
  }
  
}
