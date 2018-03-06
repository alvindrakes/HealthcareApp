package com.alvindrakes.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Signup2 extends SignupPage {

    DatabaseReference database;
    FirebaseAuth auth;

    EditText ageText;
    EditText weightText;
    EditText heightText;
    Button register;

    User user;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.signup2);

        auth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        database = FirebaseDatabase.getInstance().getReference();
        ageText = (EditText) findViewById(R.id.agef);
        weightText = (EditText) findViewById(R.id.weightf);
        heightText = (EditText) findViewById(R.id.heightf);
        register = (Button) findViewById(R.id.Next);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(ageText.getText().toString())) {
                    ageText.setError("Field is empty");
                } else if (TextUtils.isEmpty(weightText.getText().toString())) {
                    weightText.setError("Field is empty");
                } else if (TextUtils.isEmpty(heightText.getText().toString())) {
                    heightText.setError("Field is empty");
                } else {

                    user.setAge(Integer.parseInt(ageText.getText().toString().trim()));
                    user.setWeight(Integer.parseInt(weightText.getText().toString().trim()));
                    user.setHeight(Integer.parseInt(heightText.getText().toString().trim()));

                    createAccount(user);
                }

            }
        });

        FirebaseDatabase.getInstance()
                .getReference()
                .child("users")
                .child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        user = dataSnapshot.getValue(User.class);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void createAccount(User user) {

        if (!validateForm(user)) {
            return;
        }

        User.updateData(user);
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
            .setDisplayName(user.getName())
            .build();
    
        firebaseUser.updateProfile(profileUpdates)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete (@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("Account","Account details updated");
                    }
                }
            });
        
        Toast.makeText(Signup2.this, "", Toast.LENGTH_SHORT).show();
        Intent StartPageIntent = new Intent(Signup2.this, MainActivity.class);
        startActivity(StartPageIntent);
    }

    public boolean validateForm(User user) {

        boolean validate = true;

        if (user.getAge() < 1 || user.getAge() > 100) {
            ageText.setError("Invalid age. Please enter a value between 1 to 100");
            validate = false;
        }

        if (user.getWeight() < 1 || user.getWeight() > 300) {
            weightText.setError("Invalid weight. Please enter a value between 1 to 300");
            validate = false;
        }

        if (user.getHeight() < 100 || user.getHeight() > 250) {
            heightText.setError("Invalid height. Please enter a value between 100 to 250");
            validate = false;
        }

        return validate;
    }

}
