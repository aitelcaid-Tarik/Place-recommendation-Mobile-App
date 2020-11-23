package com.example.map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class Synchroniz extends Activity {

    DB_sqlite db = new DB_sqlite(this);
    EditText editText, editText2;
    TextView textView, textView2;
    Button back, add;
    RatingBar ratingBar;

    String latitude = "", longitude = "", ratingValue = "";

    String HttpURLInsert = "https://aitelcaid.000webhostapp.com/insertData.php";

    static String HttpURLGet = "https://aitelcaid.000webhostapp.com/getData.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synchronize);

        editText = (EditText) findViewById(R.id.editText);
        editText2 = (EditText) findViewById(R.id.editText2);
        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
        back = (Button) findViewById(R.id.back1);
        add = (Button) findViewById(R.id.addPlace);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        latitude = getIntent().getStringExtra("first");
        longitude = getIntent().getStringExtra("second");

        textView.setText(latitude);
        textView2.setText(longitude);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {

                ratingValue = String.valueOf(rating);

            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                insertData(editText.getText().toString(), editText2.getText().toString(), latitude, longitude, ratingValue);

                Toast.makeText(Synchroniz.this, " Data Submit Successfully ", Toast.LENGTH_SHORT).show();


                // boolean result = db.insertData(editText.getText().toString(), editText2.getText().toString(), latitude, longitude, ratingValue);

               // if (result == true) {
                //    Toast.makeText(Synchroniz.this, " Data Submit Successfully ", Toast.LENGTH_SHORT).show();

               // } else
               //     Toast.makeText(Synchroniz.this, "NO", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
                finish();

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
                finish();

            }
        });

    }

    public void insertData(final String name, final String description, final String latitude, final String longitude, final String ratingValue) {

        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {

                String NameHolder = name;
                String des = description;
                String lat = latitude;
                String lon = longitude;
                String rat = ratingValue;

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("name", NameHolder));
                nameValuePairs.add(new BasicNameValuePair("description", des));
                nameValuePairs.add(new BasicNameValuePair("latitude", lat));
                nameValuePairs.add(new BasicNameValuePair("longitude", lon));
                nameValuePairs.add(new BasicNameValuePair("rating", rat));

                try {

                    HttpClient httpClient = new DefaultHttpClient();

                    HttpPost httpPost = new HttpPost(HttpURLInsert);

                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse httpResponse = httpClient.execute(httpPost);

                    HttpEntity httpEntity = httpResponse.getEntity();


                } catch (ClientProtocolException e) {
                    Log.e("error", e.getMessage());

                } catch (IOException e) {
                    Log.e("error", e.getMessage());
                }
                return "Data Inserted Successfully";
            }

            @Override
            protected void onPostExecute(String result) {

                super.onPostExecute(result);

            }
        }.execute(name, description, latitude, longitude, ratingValue);
    }


    public static class GetDataFromServer extends AsyncTask<String, Void, Void> {

        Context context;
        private String jsonResponse;
        DB_sqlite db;


        public GetDataFromServer(Context c) {
            this.context = c;
            db = new DB_sqlite(context);
        }

        protected void onPreExecute() {
        }

        protected Void doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream isResponse = urlConnection.getInputStream();
                BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(isResponse));

                String myLine = "";
                StringBuilder strBuilder = new StringBuilder();
                while ((myLine = responseBuffer.readLine()) != null) {
                    strBuilder.append(myLine);
                }

                jsonResponse = strBuilder.toString();
                Log.e("RESPONSE", jsonResponse);
            } catch (Exception e) {
                Log.e("RESPONSE Error", e.getMessage());
            }
            return null;
        }

        protected void onPostExecute(Void unused) {

            try {


                Gson gson = new Gson();

                Log.e("PostExecute", "content: " + jsonResponse);

                Type listType = new TypeToken<ArrayList<Coordinates>>() {
                }.getType();

                Log.e("PostExecute", "arrayType: " + listType.toString());

                ArrayList<Coordinates> coordinates = gson.fromJson(jsonResponse, listType);

                Log.e("PostExecute", "OutputData: " + coordinates.toString());


                db.drop();

                for (Coordinates c : coordinates) {

                    db.insertData(c.getName(), c.getDescription(), c.getLatitude(), c.getLongitude(), c.getRating());

                }
            } catch (JsonSyntaxException e) {

                Log.e("POST-Execute", e.getMessage());
            }
        }
    }
}



