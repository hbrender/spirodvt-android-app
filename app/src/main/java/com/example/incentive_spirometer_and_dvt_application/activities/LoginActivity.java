package com.example.incentive_spirometer_and_dvt_application.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.incentive_spirometer_and_dvt_application.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText userInput = (EditText) findViewById(R.id.usernameInput);
        final EditText passInput = (EditText) findViewById(R.id.passwordInput);

        Button loginButt = (Button) findViewById(R.id.loginButton);
        loginButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = userInput.getText().toString();
                String password = passInput.getText().toString();

                if(checkInput(username, password)) {
                    Intent intent = new Intent(LoginActivity.this, PatientListActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(LoginActivity.this, "Please enter valid input", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean checkInput(String username, String password) {
        if(username.equals("") || password.equals("")) {
            return false;
        }
        return true;
    }
}
