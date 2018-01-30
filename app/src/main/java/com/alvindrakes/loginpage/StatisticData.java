package com.alvindrakes.loginpage;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by super on 1/26/2018.
 */

public class StatisticData {
  private int steps;
  private int heartData;
  
  public int getSteps () {
    return steps;
  }
  
  public void setSteps (int steps) {
    this.steps = steps;
  }
  
  public int getHeartData () {
    return heartData;
  }
  
  public void setHeartData (int heartData) {
    this.heartData = heartData;
  }
  
  public static void updateData (StatisticData data, int day) {
    
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    
    Map<String, Object> childUpdates = new HashMap<>();
    
    childUpdates.put("/users/" + firebaseUser.getUid() + "/steps/" + day + "/", data.getSteps());
    
    childUpdates.put("/users/" + firebaseUser.getUid() + "/heartbeat/" + day + "/", data.getHeartData());
    childUpdates.put("/users/" + firebaseUser.getUid() + "/day/", day+1);
    
    FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
  }
  
  public static void updateCoin (int coin){
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
  
    Map<String, Object> childUpdates = new HashMap<>();
  
    childUpdates.put("/users/" + firebaseUser.getUid() + "/coin/", coin);
  
    FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
  
  }
}
