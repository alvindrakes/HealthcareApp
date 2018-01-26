package com.alvindrakes.loginpage;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by super on 11/26/2017.
 */

public class User {
  
  private String name;
  private String email;
  private String password;
  private int age;
  private int day;
  private int weight;
  private int height;
  private int coin;
  
  public User () {
  }
  
  public User (String name, String email, String password, int age, int weight, int height) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.age = age;
    this.weight = weight;
    this.height = height;
    this.coin = 0;
    this.day = 1;
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
  
  public int getDay () {
    return day;
  }
  
  public String getName () {
    return name;
  }
  
  public void setDay (int day) {
    this.day = day;
  }
  
  public String getEmail () {
    return email;
  }
  
  public String getPassword () {
    return password;
  }
  
  public int getCoin () {
    return coin;
  }
  
  public void setCoin (int coin) {
    this.coin = coin;
  }
  
  public static void updateData (User user) {
    
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    
    Map<String, Object> userValues = toMap(user);
  
    Map<String, Object> childUpdates = new HashMap<>();
    childUpdates.put("/users/" + firebaseUser.getUid() + "/", userValues);
  
    FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
  }
  
  @Exclude
  private static Map<String, Object> toMap (User user) {
    HashMap<String, Object> result = new HashMap<>();
    result.put("name", user.getName());
    result.put("email", user.getEmail());
    result.put("password", user.getPassword());
    result.put("age", user.getAge());
    result.put("weight", user.getWeight());
    result.put("height", user.getHeight());
    result.put("coin", user.getCoin());
    result.put("day", user.getDay());
    
    return result;
  }
}
