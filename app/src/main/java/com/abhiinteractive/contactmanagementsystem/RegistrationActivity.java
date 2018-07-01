package com.abhiinteractive.contactmanagementsystem;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class RegistrationActivity extends AppCompatActivity {

    EditText regNameEditText, regUsernameEditText, regPasswordEditText;
    Button registerBtn;
    String name, username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        regNameEditText = findViewById(R.id.register_name_edittext);
        regUsernameEditText = findViewById(R.id.register_username_edittext);
        regPasswordEditText = findViewById(R.id.register_password_edittext);

        registerBtn = findViewById(R.id.register_btn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get the entered strings from the edit text's
                name = regNameEditText.getText().toString();
                username = regUsernameEditText.getText().toString();
                password = regPasswordEditText.getText().toString();

                //Validation for password, atleast 5 character long
                if(password.length()<5){
                    Toast.makeText(RegistrationActivity.this, "Password must be atleast 5 character long", Toast.LENGTH_LONG).show();
                }else {
                    //Start task to enter the registered person's details on server
                    RegistrationTask registrationTask = new RegistrationTask(RegistrationActivity.this);
                    registrationTask.execute(name, username, password);

                    //Close the registration screen
                    finish();
                }
            }
        });
    }
}

class RegistrationTask extends AsyncTask<String, Void, String> {

    Context context;

    public RegistrationTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        String regUrl = "http://abhiinteractive.000webhostapp.com/register.php";
        //Get strings from params
        String name = params[0];
        String username = params[1];
        String password = params[2];

        try {
            URL url = new URL(regUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);

            //Pass the details to the register script on server
            OutputStream OS = httpURLConnection.getOutputStream();

            BufferedWriter bufferedReader = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
            String data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8") + "&" +
                    URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&" +
                    URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
            bufferedReader.write(data);
            bufferedReader.flush();
            bufferedReader.close();

            OS.close();

            InputStream IS = httpURLConnection.getInputStream();
            Log.i("isoutput", IS.toString());
            IS.close();

            return "Registered successfully";

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Registration Failed";
}

    @Override
    protected void onPostExecute(String result) {
        //Need an internet connection to register
        if(!ContactList.checkForNetwork(context)){
            Toast.makeText(context, "You need internet connection", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
        }
    }
}
