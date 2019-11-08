package com.example.incentive_spirometer_and_dvt_application.activities;
/**
 * Login activity of the application
 * Users will input their username-password combinations and it will be validated with user data from the database
 * Only authorized users will have access to the rest of the application
 *
 * @author(s) Cole deSilva, Isak Bjornson
 * @editor(s) Hanna Brender
 *
 */

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.incentive_spirometer_and_dvt_application.R;
import com.example.incentive_spirometer_and_dvt_application.helpers.Authenticate;
import com.example.incentive_spirometer_and_dvt_application.helpers.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivityTag";
    DatabaseHelper databaseHelper = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        final EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        Button loginButt = (Button) findViewById(R.id.loginButton);
        loginButt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if(checkInput(username, password)) {

                    Intent intent = new Intent(LoginActivity.this, PatientListActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
                else {

                    Toast.makeText(LoginActivity.this, "Either your username or password is invalid.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean checkInput(String username, String password) {

        // creating an Authenticate object so that the username and password input are set up to be easily compared
        Authenticate auth = new Authenticate(username, password);

        // checks to make sure the username input is equal to the known users' username
        // also checks the hashed password from the input against the users' hashed password;
        if (databaseHelper.isRealUser(username)) {

            String[] queryResults = databaseHelper.getLoginInformation(username);

            if (compareHash(auth, queryResults)) {

                return true;
            }
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