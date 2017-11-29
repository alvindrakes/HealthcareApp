package com.alvindrakes.loginpage;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by super on 11/26/2017.
 */

public class User {
  
  private String name;
  private String email;
  private String password;
  private int age;
  private int weight;
  private int height;
  private int steps;
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
    this.steps = 0;
    this.coin = 0;
  }
  
  public void setAge (int age) {
    this.age = age;
  }
  
  public void setWeight (int weight) {
    this.weight = weight;
  }
  
  public void setHeight (int height) {
    this.height = height;
  }
  
  public String getName () {
    return name;
  }
  
  public String getEmail () {
    return email;
  }
  
  public String getPassword () {
    return password;
  }
  
  public int getAge () {
    return age;
  }
  
  public int getWeight () {
    return weight;
  }
  
  public int getHeight () {
    return height;
  }
  
  public int getSteps () {
    return steps;
  }
  
  public int getCoin () {
    return coin;
  }
  
  public static void updateData (User user) {
    
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    
    FirebaseDatabase.getInstance()
        .getReference()
        .child("users")
        .child(firebaseUser.getUid())
        .setValue(user);
  }
}
