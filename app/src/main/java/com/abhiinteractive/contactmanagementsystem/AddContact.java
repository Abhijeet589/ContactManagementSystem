package com.abhiinteractive.contactmanagementsystem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
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

public class AddContact extends AppCompatActivity {

    EditText nameEditText, phoneEditText, emailEditText, websiteEditText;
    String name, phone, email, website;
    Button addContactBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        nameEditText = findViewById(R.id.add_contact_name_edittext);
        phoneEditText = findViewById(R.id.add_contact_phone_edittext);
        emailEditText = findViewById(R.id.add_contact_email_edittext);
        websiteEditText = findViewById(R.id.add_contact_website_edittext);

        addContactBtn = findViewById(R.id.add_contact_button);
        addContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Get strings from the EditText's
                name = nameEditText.getText().toString();
                phone = phoneEditText.getText().toString();
                email = emailEditText.getText().toString();
                website = websiteEditText.getText().toString();

                //Validation of the fields, either keep them empty incase contact doesn't own a website or so else enter a valid item.
                if (email.equals("") || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(AddContact.this, "Invalid email", Toast.LENGTH_LONG).show();
                } else if (phone.equals("") || !Patterns.PHONE.matcher(phone).matches()) {
                    Toast.makeText(AddContact.this, "Invalid phone", Toast.LENGTH_LONG).show();
                } else if (website.equals("") || !Patterns.WEB_URL.matcher(website).matches()) {
                    Toast.makeText(AddContact.this, "Invalid website", Toast.LENGTH_LONG).show();
                } else {
                    saveToLocalStorage(name, email, phone, website);
                }
            }
        });
    }

    //Save the data to local storage, if the internet is available, also on the server
    public void saveToLocalStorage(String name, String email, String phone, String website) {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

        if (ContactList.checkForNetwork(this)) {
            AddContactTask addContactTask = new AddContactTask(AddContact.this);
            addContactTask.execute(name, email, phone, website);
            dbHelper.saveToLocalDatabase(name, email, phone, website, DBContract.SYNC_SUCCESS, sqLiteDatabase);
        } else {
            dbHelper.saveToLocalDatabase(name, email, phone, website, DBContract.SYNC_FAILED, sqLiteDatabase);
        }
        finish();
    }
}

//Async Task to add contact to the server
class AddContactTask extends AsyncTask<String, Void, String> {

    Context context;
    BroadcastReceiver.PendingResult pendingResult;

    public AddContactTask(Context context) {
        this.context = context;
    }

    public AddContactTask(Context context, BroadcastReceiver.PendingResult pendingResult) {
        this.context = context;
        this.pendingResult = pendingResult;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        //Getting strings from parameters
        String contactName = params[0];
        String contactPhone = params[1];
        String contactEmail = params[2];
        String contactWebsite = params[3];

        String addContactURL = "http://abhiinteractive.000webhostapp.com/add_contact.php";
        try {
            //Setup HttpURLConnection with the server
            URL url = new URL(addContactURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");

            OutputStream OS = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
            String data = URLEncoder.encode("contact_name", "UTF-8") + "=" + URLEncoder.encode(contactName, "UTF-8") + "&" +
                    URLEncoder.encode("contact_phone", "UTF-8") + "=" + URLEncoder.encode(contactPhone, "UTF-8") + "&" +
                    URLEncoder.encode("contact_email", "UTF-8") + "=" + URLEncoder.encode(contactEmail, "UTF-8") + "&" +
                    URLEncoder.encode("contact_website", "UTF-8") + "=" + URLEncoder.encode(contactWebsite, "UTF-8");
            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();

            OS.close();

            InputStream IS = httpURLConnection.getInputStream();
            IS.close();

            httpURLConnection.disconnect();

            return "Contact added";

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return "Failed to add contact";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (s.equals("Contact added")) {
            Toast.makeText(context, "Contact added", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Failed to add contact, try again", Toast.LENGTH_LONG).show();
        }

        if (pendingResult != null) {
            pendingResult.finish();
        }
    }
}