package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class MainActivity extends AppCompatActivity {

    private EditText userField;
    private Button button;
    private TextView info;

    private static final String key = "2debfbb99b40b8b4901578e02d35c162";
    private static String bufferedResult = "error";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userField = findViewById(R.id.user_field);
        button = findViewById(R.id.button);
        info = findViewById(R.id.info);

        button.setOnClickListener(new View.OnClickListener() {
            volatile boolean wait = true;

            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                CountDownLatch latch = new CountDownLatch(1);
                String city = userField.getText().toString().trim();

                if (city.equals("")) {
                    Toast.makeText(MainActivity.this, R.string.error_no_user_input, Toast.LENGTH_LONG).show();
                } else {

//                    Executor executor = Executors.newSingleThreadExecutor();
//
//                    Handler handler = new Handler(Looper.getMainLooper());
//
//
//                    executor.execute(() -> {
//
//                        String firstResult = getDataFromURL("http://api.openweathermap.org/geo/1.0/direct?q=" + city + "&limit=1&appid=" + key);
//
////                        String result = getDataFromURL("https://api.openweathermap.org/data/2.5/weather?lat="+ lat +"&lon=" + lon +"&appid=" + key + "&units=metric");
//                        String result = firstResult;
//
//                        handler.post(() -> info.setText(result));
//                    });

                    new URLData().execute("http://api.openweathermap.org/geo/1.0/direct?q=" + city + "&limit=1&appid=" + key);
                }
            }
        });
    }

    private String getDataFromURL(String urlString) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();
            String line = "";

            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            return buffer.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) connection.disconnect();
            try {
                if (reader != null) reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "null";
    }


    private class URLData extends AsyncTask<String, String, String> {

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            info.setText("Waiting...");
        }

        @Override
        protected String doInBackground(String... strings) {
            JSONObject json = null;

            try {
                json = new JSONArray(getDataFromURL(strings[0])).getJSONObject(0);
                json = new JSONObject(getDataFromURL(
                        "https://api.openweathermap.org/data/2.5/weather?lat=" +
                                (int) json.getDouble("lat") + "&lon=" + (int) json.getDouble("lon") +
                                "&appid=" + key + "&units=metric"));

                return String.valueOf(json.getJSONObject("main").getDouble("temp")) + " °C";
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return "no such city";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            info.setText(result);
        }
    }
}

