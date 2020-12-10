package com.anta40.app.customasynctest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.anta40.app.customasynctest.async.NewAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Button btnTest_old, btnTest_new;
    private HTTPGetTask_OldAsyncTask httpGetTask_old;
    private HTTPGetTask_NewAsyncTask httpGetTask_new;

    private int DATA_COUNT = 10;

    private final String BASE_URL = "https://xkcd.com";
    private String TEST_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTest_old = (Button) findViewById(R.id.btnTest_old);
        btnTest_old.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TEST_URL = BASE_URL + "/" + randomRanges(0,50) + "/info.0.json";
                httpGetTask_old = new HTTPGetTask_OldAsyncTask(TEST_URL);
                httpGetTask_old.execute();

            }
        });

        btnTest_new = (Button) findViewById(R.id.btnTest_new);
        btnTest_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TEST_URL = BASE_URL + "/" + randomRanges(0,50) + "/info.0.json";
                httpGetTask_new = new HTTPGetTask_NewAsyncTask(TEST_URL);
                httpGetTask_new.execute();
            }
        });
    }

    class HTTPGetTask_OldAsyncTask extends AsyncTask<String, Integer, String> {

        private String httpResponse;
        private String theURL;

        public HTTPGetTask_OldAsyncTask(String url){
            theURL = url;
        }

        @Override
        protected void onPreExecute() {
            httpResponse = "";
        }

        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(theURL)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                httpResponse = response.body().string();
            }
            catch (IOException ioe){
                httpResponse = "";
                ioe.printStackTrace();
            }
            return httpResponse;
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                JSONObject jsObj = new JSONObject(s);
                showAlert("Old AsyncTask Result", jsObj.getString("alt"));
            }
            catch (JSONException je){
                je.printStackTrace();
            }
        }

    }

    class HTTPGetTask_NewAsyncTask extends NewAsyncTask<String, Integer, String> {

        private String httpResponse;
        private String theURL;

        public HTTPGetTask_NewAsyncTask(String url){
            theURL = url;
        }

        @Override
        protected void onPreExecute() {
            httpResponse = "";
        }

        @Override
        protected String doInBackground(String s) throws Exception {
            //Does some work on background thread
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(theURL)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                httpResponse = response.body().string();
            }
            catch (IOException ioe){
                httpResponse = "";
                ioe.printStackTrace();
            }
            return httpResponse;
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                JSONObject jsObj = new JSONObject(s);
                showAlert("New AsyncTask Result", jsObj.getString("alt"));

            }
            catch (JSONException je){
                je.printStackTrace();
            }

        }

        @Override
        protected void onBackgroundError(Exception e) {
            //Handle any exception that occured while running doInBackground()
            //This is also executed on the UI thread
        }
    }

    @Override
    protected void onDestroy() {
        if (httpGetTask_old != null){
            httpGetTask_old.cancel(true);
        }
        if (httpGetTask_new != null){
            httpGetTask_new.cancel();
        }
        super.onDestroy();
    }

    public void showAlert(String dialogTitle, String dialogMessage){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MainActivity.this);

        alertDialogBuilder.setTitle(dialogTitle);

        alertDialogBuilder
                .setMessage(dialogMessage)
                .setCancelable(false)
                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {

                       dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public int randomRanges(int min, int max) {
        Random random = new Random();
        return random.ints(min, max+1)
                .findFirst()
                .getAsInt();
    }
}