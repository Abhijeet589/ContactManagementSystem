package com.abhiinteractive.contactmanagementsystem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class LoginActivity extends AppCompatActivity {

    EditText loginUsername, loginPassword;
    String username, password;
    Button loginBtn, registerBtn;
    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialize Google AdMob ads
        MobileAds.initialize(this, "ca-app-pub-6229326724546843~4529783246");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-6229326724546843/2207250770");
        //Load ad
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        //Listener to show the ad as soon as it is loaded
        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mInterstitialAd.show();
            }
        });

        loginUsername = findViewById(R.id.login_username_edittext);
        loginPassword = findViewById(R.id.login_password_edittext);

        loginBtn = findViewById(R.id.login_btn);
        registerBtn = findViewById(R.id.go_to_register_btn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = loginUsername.getText().toString();
                password = loginPassword.getText().toString();

                //Execute the task to validate the login
                LoginTask loginTask = new LoginTask(LoginActivity.this);
                loginTask.execute(username, password);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });
    }
}

class LoginTask extends AsyncTask<String, Void, String> {

    Context context;

    public LoginTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        String loginUrl = "http://abhiinteractive.000webhostapp.com/login.php";

        //Get strings from parameters
        String loginUsername = params[0];
        String loginPassword = params[1];
        try {
            //Setup HttpURL Connection
            URL url = new URL(loginUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream OS = httpURLConnection.getOutputStream();

            //Pass the entered username and password to the PHP script
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
            String data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(loginUsername, "UTF-8") + "&" +
                    URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(loginPassword, "UTF-8");
            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();

            OS.close();


            //Get the response of the PHP Script
            InputStream IS = httpURLConnection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));
            String response = "";
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                response += line;
            }
            bufferedReader.close();
            IS.close();

            httpURLConnection.disconnect();

            return response;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return "Login failed, try again";
    }

    @Override
    protected void onPostExecute(String result) {
        //Based on the response string, show appropriate message. Go to the contact's list if login username and password are correct.
        if (result.equals("Login success")) {
            Toast.makeText(context, "Logged in successfully", Toast.LENGTH_SHORT).show();
            context.startActivity(new Intent(context, ContactList.class));
            ((Activity) context).finish();
        } else if (result.equals("Login failed")) {
            Toast.makeText(context, "Invalid username or password", Toast.LENGTH_LONG).show();
        } else if (!ContactList.checkForNetwork(context)) {
            Toast.makeText(context, "You need internet connection to login", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Failed to log in, try again", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
