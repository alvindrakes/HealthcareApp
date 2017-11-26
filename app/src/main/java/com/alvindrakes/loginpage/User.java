package com.alvindrakes.loginpage;

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
  
  public User (String name,
               String email,
               String password,
               int age,
               int weight,
               int height) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.age = age;
    this.weight = weight;
    this.height = height;
    this.steps = 0;
  }
  
  public void setName (String name) {
    this.name = name;
  }
  
  public void setPassword (String password) {
    this.password = password;
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
  
  public void setSteps (int steps) {
    this.steps = steps;
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
}
