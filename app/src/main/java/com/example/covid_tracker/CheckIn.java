package com.example.covid_tracker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.covid_tracker.URLs.URL_LOCATION;


public class CheckIn extends AppCompatActivity{

    Button CheckIn;
    Button Back;
    Button SelectDate;
    TextView date;
    DatePickerDialog datePickerDialog;
    int year;
    int month;
    int dayOfMonth;
    Calendar calendar;
    EditText editTextUsername, editTextEmail, editTextPassword;
    RadioGroup radioGroupGender;
    OkHttpClient client = new OkHttpClient();
    RadioGroup radioGroupStatus;
    RadioButton positive;
    RadioButton negative;
    Spinner spinLocations;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        //getting user data
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        radioGroupGender = (RadioGroup) findViewById(R.id.radioGender);
        radioGroupStatus = (RadioGroup) findViewById(R.id.radioStatus);
        positive = (RadioButton) findViewById(R.id.rbYES);
        negative = (RadioButton) findViewById(R.id.rbNO);

        //getting datepicker data
        SelectDate = findViewById(R.id.btnDate);
        date = findViewById(R.id.tvSelectedDate);

        //check in and back buttons initialized
        CheckIn = findViewById(R.id.bCheckIn);
        Back = findViewById(R.id.bBack);

        //spinner initialized
        spinLocations = (Spinner) findViewById(R.id.spinLocation);

        // code for populating the spinner location
        HttpUrl.Builder urlBuilder = HttpUrl.parse(URL_LOCATION).newBuilder();
        String url_method = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url_method)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                final String responseData = response.body().string();
                CheckIn.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getLocations(responseData);
                    }
                });
            }

            @Override
            public void onFailure(Call arg0, IOException arg1) {
                arg1.printStackTrace();

            }
        });

//code to get date from date picker and display it in the text view on click select date
        SelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(CheckIn.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        date.setText(year + "-" + (month + 1) + "-" + day);
                    }
                }, year, month, dayOfMonth);
                //this ensures not future dates can be selected
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });


        CheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userCheckIn();
            }
        });

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }


    private void userCheckIn() {
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        final String username = user.getUsername();
        final Spinner spinLocations = (Spinner) findViewById(R.id.spinLocation);
        final int value = getValue();
        final String status = Integer.toString(value);
        final String SelectedLocation = spinLocations.getSelectedItem().toString();
        final String SelectedDate = date.getText().toString(); // this makes the output text view date a string
        final String location_error = "Please select a location";

        //first we will do the validations
        //validate spinner
        int selectedItemOfMySpinner = spinLocations.getSelectedItemPosition();
        String actualPositionOfMySpinner = (String) spinLocations.getItemAtPosition(selectedItemOfMySpinner);

        if (actualPositionOfMySpinner.isEmpty()) {
            setSpinnerError(spinLocations,location_error);
        }

        //Validate Date
        if (SelectedDate.equals("Selected Date:")) {
            Toast toast=Toast.makeText(CheckIn.this,"Please select a date",Toast.LENGTH_SHORT);
            toast.show();
        }

        //if it passes all the validations
        class userCheckIn extends AsyncTask<Void, Void, String> { // void void string ? is this right ?

            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("location", SelectedLocation);
                params.put("status", status);
                params.put("date", SelectedDate);

                //returning the response
                return requestHandler.sendPostRequest(URLs.URL_USER, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //displaying the progress bar while user registers on the server
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //hiding the progressbar after completion
                progressBar.setVisibility(View.GONE);

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        //getting the user from the response
                        JSONObject userJson = obj.getJSONObject("userdetails");

                        //creating a new user object
                        UserDetails userdetails = new UserDetails(
                                userJson.getInt("id"),
                                userJson.getString("username"),
                                userJson.getString("location"),
                                userJson.getString("status"),
                                userJson.getString("date")

                        );

                        //storing the user in shared preferences
                        SharedPrefManager.getInstance(getApplicationContext()).userCheckIn(userdetails);

                        //starting the profile activity (when register button is clicked)
                        finish();
                        startActivity(new Intent(getApplicationContext(), CheckIn_Profile.class));

                    } else {
                        Toast.makeText(getApplicationContext(), "Some error occurred", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        //executing the async task
        userCheckIn usercheckin = new userCheckIn();
        usercheckin.execute();
    }

    //CODE TO GET LOCATIONS ARRAY FROM SQL and populates spinner
    public ArrayList<String> getLocations(String json){
        JSONArray jsonArray = null;
        ArrayList<String> LocationsList = new ArrayList<String>();
        Spinner spinLocation = (Spinner) findViewById(R.id.spinLocation);

        try {
            jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                LocationsList.add(jsonArray.getJSONObject(i).getString("location"));
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(CheckIn.this, android.R.layout.simple_expandable_list_item_1, LocationsList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinLocation.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return LocationsList;
    }


    //method to change the status string to int
    public int getValue(){
        int x;

        if(positive.isChecked()){
            x = 1;
        }
        else{
            x = 0;
        }
        return x;
    }

    private void setSpinnerError(Spinner spinner, String error){
        View selectedView = spinner.getSelectedView();
        if (selectedView != null && selectedView instanceof TextView) {
            spinner.requestFocus();
            TextView selectedTextView = (TextView) selectedView;
            selectedTextView.setError(error);
            selectedTextView.setTextColor(Color.RED);
            selectedTextView.setText(error);
            spinner.performClick();

        }
    }

}


