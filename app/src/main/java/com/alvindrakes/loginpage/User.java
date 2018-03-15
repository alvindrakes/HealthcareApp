package com.alvindrakes.loginpage;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by super on 11/26/2017.
 */

public class User {
  
  private String name;
  private String email;
  private int age;
  private int totalSteps;
  private int weight;
  private int height;
  private int coin;
  private int sleepToday;
  
  public User () {
  }

  public User(String name, String email) {
    this.name = name;
    this.email = email;
  }
  
  public static void updateCoin (int coin){
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
  
    Map<String, Object> childUpdates = new HashMap<>();
  
    childUpdates.put("/users/" + firebaseUser.getUid() + "/coin/", coin);
  
    FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
  
  }
  
  public void setAge (int age) {
    this.age = age;
  }
  
  public int getAge () {
    return age;
  }
  
  public void setWeight (int weight) {
    this.weight = weight;
  }
  
  public int getWeight () {
    return weight;
  }
  
  public void setHeight (int height) {
    this.height = height;
  }
  
  public int getHeight () {
    return height;
  }
  
  public int getTotalSteps () {
    return totalSteps;
  }
  
  public String getName () {
    return name;
  }
  
  public void setTotalSteps (int totalSteps) {
    this.totalSteps = totalSteps;
  }
  
  public String getEmail () {
    return email;
  }
  
  public int getCoin () {
    return coin;
  }
  
  public void setCoin (int coin) {
    this.coin = coin;
  }
  
  public int getSleepToday () {
    return sleepToday;
  }
  
  public static void updateData (User user) {
    
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    
    Map<String, Object> childUpdates = new HashMap<>();
    childUpdates.put("/users/" + firebaseUser.getUid() + "/name/", user.getName());
    childUpdates.put("/users/" + firebaseUser.getUid() + "/email/", user.getEmail());
    childUpdates.put("/users/" + firebaseUser.getUid() + "/age/", user.getAge());
    childUpdates.put("/users/" + firebaseUser.getUid() + "/weight/", user.getWeight());
    childUpdates.put("/users/" + firebaseUser.getUid() + "/height/", user.getHeight());
    childUpdates.put("/users/" + firebaseUser.getUid() + "/coin/", user.getCoin());
    childUpdates.put("/users/" + firebaseUser.getUid() + "/totalSteps/", user.getTotalSteps());

    FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
  }
  
  public static void updateSleep (int sleepToday) {
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
  
    Map<String, Object> childUpdates = new HashMap<>();
    childUpdates.put("/users/" + firebaseUser.getUid() + "/sleepToday/", sleepToday);
    
    FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
  }
  
}
