package com.example.incentive_spirometer_and_dvt_application.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.incentive_spirometer_and_dvt_application.R;
import com.example.incentive_spirometer_and_dvt_application.helpers.Authenticate;
import com.example.incentive_spirometer_and_dvt_application.helpers.DatabaseHelper;

/**
 * Login activity of the application
 * Users will input their username-password combinations and it will be validated with user data from the database
 * Only authorized users will have access to the rest of the application
 *
 * @author(s) Cole deSilva, Isak Bjornson
 * @editor(s) Hanna Brender
 *
 */

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivityTag";
    DatabaseHelper databaseHelper = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setDisplayShowTitleEnabled(false); // no title

        final EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        final EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        Button loginButt = (Button) findViewById(R.id.loginButton);
        loginButt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // check if username and password are valid
                if (checkUsername(username)) {
                    if (checkPassword(username, password)) {
                        Intent intent = new Intent(LoginActivity.this, PatientListActivity.class);
                        int doctorId = databaseHelper.getDoctorId(username);
                        intent.putExtra("doctorId", doctorId);
                        startActivity(intent);
                    } else {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(LoginActivity.this);
                        alertBuilder.setTitle(getString(R.string.title_incorrect_password))
                                .setMessage(getString(R.string.message_incorrect_password))
                                .setPositiveButton(getString(R.string.try_again), null);
                        alertBuilder.show();
                    }
                } else {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(LoginActivity.this);
                    alertBuilder.setTitle(getString(R.string.title_incorrect_username))
                            .setMessage(getString(R.string.message_incorrect_username))
                            .setPositiveButton(getString(R.string.try_again), null);
                    alertBuilder.show();
                }
            }
        });

    }

    private boolean checkUsername(String username) {
        // checks to make sure the username input is equal to the known users' username
        if (databaseHelper.isRealUser(username)) {
            return true;
        }
        return false;
    }

    private boolean checkPassword(String username, String password) {
        // creating an Authenticate object so that the username and password input are set up to be easily compared
        Authenticate auth = new Authenticate(username, password);
        String[] queryResults = databaseHelper.getLoginInformation(username);

        if (compareHash(auth, queryResults)) {
            return true;
        }
        return false;
    }

    private boolean compareHash (Authenticate auth, String[] queryResults) {

        // checks the hashed password input against the known users' hashed password
        if(auth.getHashedPassword(queryResults[0]).equals(queryResults[1]) && auth.getHashedPassword(queryResults[0]) != "") {
            return true;
        }
        else {
            return false;
        }
    }


    @Override
    protected void onStop() {

        EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        // remove username and password from previous activity
        usernameEditText.setText("");
        passwordEditText.setText("");

        super.onStop();
    }

    @Override
    protected void onDestroy() {

        databaseHelper.close();
        super.onDestroy();
    }
}