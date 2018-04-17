package com.alvindrakes.loginpage;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Transaction {
  private int coupon_50;
  private int coupon_100;
  private int coupon_200;
  private boolean spacesuit1;
  private boolean spacesuit2;
  private boolean spacesuit3;
  private boolean spacesuit4;
  
  public int getCoupon_50 () {
    return coupon_50;
  }
  
  public void setCoupon_50 (int coupon_50) {
    this.coupon_50 = coupon_50;
  }
  
  public int getCoupon_100 () {
    return coupon_100;
  }
  
  public void setCoupon_100 (int coupon_100) {
    this.coupon_100 = coupon_100;
  }
  
  public int getCoupon_200 () {
    return coupon_200;
  }
  
  public void setCoupon_200 (int coupon_200) {
    this.coupon_200 = coupon_200;
  }
  
  public boolean isSpacesuit1 () {
    return spacesuit1;
  }
  
  public void setSpacesuit1 (boolean spacesuit1) {
    this.spacesuit1 = spacesuit1;
  }
  
  public boolean isSpacesuit2 () {
    return spacesuit2;
  }
  
  public void setSpacesuit2 (boolean spacesuit2) {
    this.spacesuit2 = spacesuit2;
  }
  
  public boolean isSpacesuit3 () {
    return spacesuit3;
  }
  
  public void setSpacesuit3 (boolean spacesuit3) {
    this.spacesuit3 = spacesuit3;
  }
  
  public boolean isSpacesuit4 () {
    return spacesuit4;
  }
  
  public void setSpacesuit4 (boolean spacesuit4) {
    this.spacesuit4 = spacesuit4;
  }
  
  public static void updateData (Transaction transaction) {
  
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
  
    Map<String, Object> childUpdates = new HashMap<>();
    childUpdates.put("/users/" + firebaseUser.getUid() + "/transaction/coupon_50", transaction.getCoupon_50());
    childUpdates.put("/users/" + firebaseUser.getUid() + "/transaction/coupon_100", transaction.getCoupon_100());
    childUpdates.put("/users/" + firebaseUser.getUid() + "/transaction/coupon_200", transaction.getCoupon_200());
    childUpdates.put("/users/" + firebaseUser.getUid() + "/transaction/spacesuit1", transaction.isSpacesuit1());
    childUpdates.put("/users/" + firebaseUser.getUid() + "/transaction/spacesuit2", transaction.isSpacesuit2());
    childUpdates.put("/users/" + firebaseUser.getUid() + "/transaction/spacesuit3", transaction.isSpacesuit3());
    childUpdates.put("/users/" + firebaseUser.getUid() + "/transaction/spacesuit4", transaction.isSpacesuit4());

  
    FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
    
  }
  
}
