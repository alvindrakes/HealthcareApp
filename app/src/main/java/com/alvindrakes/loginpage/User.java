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

  public User(String name, String email) {
    this.name = name;
    this.email = email;
    this.day = 1;
  }

  public User (String name, String email, String password) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.day = 1;
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
    
    Map<String, Object> childUpdates = new HashMap<>();
    childUpdates.put("/users/" + firebaseUser.getUid() + "/name/", user.getName());
    childUpdates.put("/users/" + firebaseUser.getUid() + "/email/", user.getEmail());
    childUpdates.put("/users/" + firebaseUser.getUid() + "/password/", user.getPassword());
    childUpdates.put("/users/" + firebaseUser.getUid() + "/age/", user.getAge());
    childUpdates.put("/users/" + firebaseUser.getUid() + "/weight/", user.getWeight());
    childUpdates.put("/users/" + firebaseUser.getUid() + "/height/", user.getHeight());
    childUpdates.put("/users/" + firebaseUser.getUid() + "/coin/", user.getCoin());
    childUpdates.put("/users/" + firebaseUser.getUid() + "/day/", user.getDay());

    FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
  }
  
}
