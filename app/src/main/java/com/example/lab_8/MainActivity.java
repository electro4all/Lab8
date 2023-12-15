package com.example.lab_8;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView txtDetails;
    private EditText inputName, inputEmail;
    private Button btnSave;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setIcon(R.mipmap.ic_launcher);
        }

        txtDetails = findViewById(R.id.txt_user);
        inputName = findViewById(R.id.name);
        inputEmail = findViewById(R.id.email);
        btnSave = findViewById(R.id.btn_save);

        // Add null check for FirebaseDatabase instance
        mFirebaseInstance = FirebaseDatabase.getInstance();
        if (mFirebaseInstance != null) {
            mFirebaseDatabase = mFirebaseInstance.getReference("users");

            mFirebaseInstance.getReference("app_title").setValue("Realtime Database");

            mFirebaseInstance.getReference("app_title").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.e(TAG, "App title updated");
                    String appTitle = dataSnapshot.getValue(String.class);

                    // Check if ActionBar is not null before setting the title
                    ActionBar actionBar = getSupportActionBar();
                    if (actionBar != null) {
                        actionBar.setTitle(appTitle);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Failed to read app title value.", error.toException());
                }
            });
        } else {
            Log.e(TAG, "FirebaseDatabase.getInstance() returned null");
        }


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = inputName.getText().toString();
                String email = inputEmail.getText().toString();
                if (TextUtils.isEmpty(userId)) {
                    createUser(name, email);
                } else {
                    updateUser(name, email);
                }
            }
        });
    }

    private void createUser(String name, String email) {
        if (TextUtils.isEmpty(userId)) {
            userId = mFirebaseDatabase.push().getKey();
        }
        User user = new User(name, email);
        mFirebaseDatabase.child(userId).setValue(user);
        addUserChangeListener();
    }

    private void addUserChangeListener() {
        mFirebaseDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user == null) {
                    Log.e(TAG, "User data is null!");
                    return;
                }
                Log.e(TAG, "User data is changed!" + user.getName() + ", " + user.getEmail());
                txtDetails.setText(user.getName() + ", " + user.getEmail());
                inputEmail.setText("");
                inputName.setText("");
                toggleButton();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });
        toggleButton();
    }

    private void toggleButton() {
        if (TextUtils.isEmpty(userId)) {
            btnSave.setText("Хадгалах");
        } else {
            btnSave.setText("Засах");
        }
    }

    private void updateUser(String name, String email) {
        if (!TextUtils.isEmpty(name)) {
            mFirebaseDatabase.child(userId).child("name").setValue(name);
        }
        if (!TextUtils.isEmpty(email)) {
            mFirebaseDatabase.child(userId).child("email").setValue(email);
        }
    }
}
