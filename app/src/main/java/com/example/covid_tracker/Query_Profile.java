package com.example.covid_tracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

public class Query_Profile extends AppCompatActivity {

    TextView displayTotal;
    TextView displayInfected;
    TextView percent;
    Button Back;
    Button Continue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query__profile);

        //LinearLayout lView = new LinearLayout(this);

        //display location and date
        displayTotal = (TextView) findViewById(R.id.tvTotal);
        displayInfected = (TextView) findViewById(R.id.tvPositive);
        percent= (TextView) findViewById(R.id.tvPercentage);
        Back = (Button) findViewById(R.id.bBack);
        Continue = (Button) findViewById(R.id.buttonContinue);


        Intent intent = getIntent();
        Bundle b = intent.getBundleExtra("data");

        String Positive =  b.getString("total_positive_checkins");
        String Total = b.getString("total_checkins");

        assert Total != null;
        double totalVis = Double.parseDouble(Total);
        assert Positive != null;
        double positives = Double.parseDouble(Positive);

        double finalPercent = (positives/totalVis)*100;
        String displayFinalPercentage = Double.toString(finalPercent);

        displayTotal.setText(Total);
        displayInfected.setText(Positive);



        if(displayFinalPercentage.equals("NaN")){
            percent.setText("0.0%");
            percent.setTextColor(Color.parseColor("#000000"));
            DialogFragment dialog = new DialogFragmentNoRisk();
            dialog.show(getSupportFragmentManager(), "MyDialogFragmentTag");
        }
        else {
            percent.setText(displayFinalPercentage + "%");
            percent.setTextColor(Color.parseColor("#000000"));
        }

        if((finalPercent>0) && (finalPercent<=10) ){
            DialogFragment dialog = new DialogFragmentLowRisk();
            dialog.show(getSupportFragmentManager(), "MyDialogFragmentTag");
        }
        else if((finalPercent>10) && (finalPercent<=40)){
            DialogFragment dialog = new DialogFragmentAveRisk();
            dialog.show(getSupportFragmentManager(), "MyDialogFragmentTag");
        }
        else if((finalPercent>40) && (finalPercent <=60)){
            DialogFragment dialog = new DialogFragmentHighRisk();
            dialog.show(getSupportFragmentManager(), "MyDialogFragmentTag");
        }
        else if((finalPercent>60) && (finalPercent<=100)){
            DialogFragment dialog = new DialogFragmentExtRisk();
            dialog.show(getSupportFragmentManager(), "MyDialogFragmentTag");
        }
//back button
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), Query.class));
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

    public static class DialogFragmentNoRisk extends DialogFragment {
        @NotNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialog);
            builder.setTitle("RISK ALERT!");
            builder.setMessage("ZERO RISK!");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // You don't have to do anything here if you just
                    // want it dismissed when clicked
                }
            });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
    public static class DialogFragmentLowRisk extends DialogFragment {
        @NotNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialog2);
            builder.setTitle("RISK ALERT!");
            builder.setMessage("LOW RISK!");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // You don't have to do anything here if you just
                    // want it dismissed when clicked
                }
            });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
    public static class DialogFragmentAveRisk extends DialogFragment {
        @NotNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialog3);
            builder.setTitle("RISK ALERT!");
            builder.setMessage("AVERAGE RISK!\nPlease be cautious");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // You don't have to do anything here if you just
                    // want it dismissed when clicked
                }
            });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
    public static class DialogFragmentHighRisk extends DialogFragment {
        @NotNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialog4);
            builder.setTitle("RISK ALERT!");
            builder.setMessage("HIGH RISK!\nPlease try your best to avoid this area");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // You don't have to do anything here if you just
                    // want it dismissed when clicked
                }
            });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
    public static class DialogFragmentExtRisk extends DialogFragment {
       @NotNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),  R.style.MyDialog5);
            builder.setTitle("RISK ALERT!");
            builder.setMessage("EXTREMELY HIGH RISK!\n DO NOT VISIT THIS LOCATION!");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // You don't have to do anything here if you just
                    // want it dismissed when clicked
                }
            });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
}