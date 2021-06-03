package com.example.covid_tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CheckIn_Profile extends AppCompatActivity {

    TextView textViewId, textViewUsername, textViewEmail, textViewGender, textViewLocation, textViewStatus, textViewDate;
    Button Back, Continue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in__profile);
        //if the user is not logged in
        //starting the login activity
        if (!SharedPrefManager.getInstance(this).isCheckedIn()) {
            finish();
            startActivity(new Intent(this, CheckIn.class));
        }

//code for displaying users info
        textViewId = (TextView) findViewById(R.id.textViewId);
        textViewUsername = (TextView) findViewById(R.id.textViewUsername);
        textViewEmail = (TextView) findViewById(R.id.textViewEmail);
        textViewGender = (TextView) findViewById(R.id.textViewGender);
        textViewLocation = (TextView) findViewById(R.id.textViewLocation);
        textViewStatus = (TextView) findViewById(R.id.textViewStatus);
        textViewDate = (TextView) findViewById(R.id.textViewDate);
        Back = findViewById(R.id.bBack);
        Continue = findViewById(R.id.bContinue);

        //getting the current user
        final User user = SharedPrefManager.getInstance(this).getUser();
        final UserDetails userdetails = SharedPrefManager.getInstance(this).getUserDetails();
        String STATUS = getCOVID_VALUE();

        //setting the values to the text views
        textViewId.setText(String.valueOf(user.getId()));
        textViewUsername.setText(user.getUsername());
        textViewEmail.setText(user.getEmail());
        textViewGender.setText(user.getGender());
        textViewLocation.setText(userdetails.getLocation());
        textViewStatus.setText(STATUS);
        textViewDate.setText(userdetails.getDate());


        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CheckIn.class));
            }
        });

        Continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), Advice_Page.class));
            }
        });
    }
        public String getCOVID_VALUE() {
        String COVID_STATUS;
        UserDetails userdetails = SharedPrefManager.getInstance(this).getUserDetails();
            if (userdetails.getStatus() == "1") {
                COVID_STATUS = "POSITIVE";
            } else {
                COVID_STATUS = "NEGATIVE";
            }
            return COVID_STATUS;
        }
}




