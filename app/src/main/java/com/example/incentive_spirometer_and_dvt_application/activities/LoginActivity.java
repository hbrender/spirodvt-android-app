package com.example.incentive_spirometer_and_dvt_application.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.incentive_spirometer_and_dvt_application.R;
import com.example.incentive_spirometer_and_dvt_application.helpers.Authenticate;
import com.example.incentive_spirometer_and_dvt_application.models.User;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivityTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // setting the users that can login to the app
        // this is just for testing right now dont worry
        Authenticate user1 = new Authenticate("iBjornson", "iamthegreatest");
        String[] user1help = new String[] {user1.getSalt(), user1.getHashedPassword(user1.getSalt())};
        User.users.put("iBjornson", user1help);

        Authenticate user2 = new Authenticate("cDesilva", "whatthefook");
        String[] user2help = new String[] {user2.getSalt(), user2.getHashedPassword(user2.getSalt())};
        User.users.put("cDesilva", user2help);

        Authenticate user3 = new Authenticate("hbrender", "1234");
        String[] user3help = new String[] {user3.getSalt(), user3.getHashedPassword(user3.getSalt())};
        User.users.put("hbrender", user3help);

        Authenticate user4 = new Authenticate("klally", "6789");
        String[] user4help = new String[] {user4.getSalt(), user4.getHashedPassword(user4.getSalt())};
        User.users.put("klally", user4help);



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
        if (User.users.containsKey(username)) {
            String[] info = User.users.get(username);
            if (compareHash(auth, info)) {
                return true;
            }
        }
        return false;
    }

    private boolean compareHash (Authenticate auth, String[] info) {
        // checks the hashed password input against the known users' hashed password
        if(auth.getHashedPassword(info[0]).equals(info[1]) && auth.getHashedPassword(info[0]) != "") {
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
}
