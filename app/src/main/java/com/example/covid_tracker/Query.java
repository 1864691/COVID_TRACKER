package com.example.covid_tracker;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.covid_tracker.URLs.URL_INDEX;
import static com.example.covid_tracker.URLs.URL_LOCATION;


public class Query extends AppCompatActivity {
    private TextView date;
    Spinner Locations;
    Button Query;
    Button SelectDate;
    Button Back;
    DatePickerDialog datePickerDialog;
    int year;
    int month;
    int dayOfMonth;
    Calendar calendar;

    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

        date = (TextView) findViewById(R.id.tvSelectedDate);
        Locations = (Spinner) findViewById(R.id.spinLocation);
        Query = (Button) findViewById(R.id.buttonQuery);
        Back = (Button) findViewById(R.id.bBack);
        SelectDate = findViewById(R.id.btnDate);

        HttpUrl.Builder urlBuilder = HttpUrl.parse(URL_LOCATION).newBuilder();
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                Query.this.runOnUiThread(new Runnable() {

                    String responseData = response.body().string();

                    @Override
                    public void run() {
                        getLocations(responseData);
                    }
                });
            }
        });

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        SelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(Query.this, new DatePickerDialog.OnDateSetListener() {
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

        Query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkHttpClient client = new OkHttpClient();
                final Spinner spinLocations = (Spinner) findViewById(R.id.spinLocation);
                final String location_error = "Please select a location";

                String Date = date.getText().toString();
                String location = Locations.getSelectedItem().toString();

                //validations here
                    int selectedItemOfMySpinner = spinLocations.getSelectedItemPosition();
                    String actualPositionOfMySpinner = (String) spinLocations.getItemAtPosition(selectedItemOfMySpinner);

                    if (actualPositionOfMySpinner.isEmpty()) {
                        setSpinnerError(spinLocations, location_error);
                    }

                    if (Date.equals("Selected Date:")) {
                        Toast toast = Toast.makeText(Query.this, "Please select a date", Toast.LENGTH_SHORT);
                        toast.show();
                    }

                    HttpUrl.Builder urlBuilder = HttpUrl.parse(URL_INDEX).newBuilder();
                    urlBuilder.addQueryParameter("location", location);
                    urlBuilder.addQueryParameter("date", Date);

                String url = urlBuilder.build().toString();

                final Request request = new Request.Builder()
                        .url(url)
                        .build();

                client.newCall(request).enqueue(new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, @NotNull final Response response) throws IOException {
                        Query.this.runOnUiThread(new Runnable() {

                            String result = response.body().string();

                            @Override
                            public void run() {
                                try {
                                    get_Index(result);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    public ArrayList<String> getLocations(String json) {
        ArrayList<String> LocationsList = new ArrayList<String>();
        Spinner spinLocation = (Spinner) findViewById(R.id.spinLocation);

        try {
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                LocationsList.add(jsonArray.getJSONObject(i).getString("location"));
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(Query.this, android.R.layout.simple_expandable_list_item_1, LocationsList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinLocation.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return LocationsList;
    }

    //this method uses the get_index to count number of positive and Total in order to calculate percentage and find risk
    public ArrayList<Integer> get_Index(final String json)throws JSONException {
        final ArrayList<Integer> Infected = new ArrayList<Integer>();
        try {
            int Total_count = 0;
            int Positive_count = 0;

            JSONArray all = new JSONArray(json);

            for (int i = 0; i < all.length(); i++) {
                JSONObject item = all.getJSONObject(i);
                final int positive_status = item.getInt("status");
                if (positive_status == 1) {
                    Positive_count += positive_status;
                }
                Total_count +=  1;
            }

            final String All = Integer.toString(Total_count);
            final String infected = Integer.toString(Positive_count);


            //this method should store my Total and positive counts in Shared pref manager
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Query.this, Query_Profile.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    Bundle b = new Bundle();
                    b.putString("total_positive_checkins", infected);
                    b.putString("total_checkins", All);
                    intent.putExtra("data", b);
                    startActivity(intent);
                    finish();
                }
            });
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return Infected;
    }


    private void setSpinnerError(Spinner spinner, String error) {
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



