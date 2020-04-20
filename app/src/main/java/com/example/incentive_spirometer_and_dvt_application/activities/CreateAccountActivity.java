package com.example.incentive_spirometer_and_dvt_application.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.incentive_spirometer_and_dvt_application.R;
import com.example.incentive_spirometer_and_dvt_application.helpers.Authenticate;
import com.example.incentive_spirometer_and_dvt_application.helpers.DatabaseHelper;

/**
 *
 * This is where users can create a new account. (They input a new username and password)
 *
 *  v1.0: 04/20/20
 */

public class CreateAccountActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    EditText userNameEditText;
    EditText newPasswordEditText;
    EditText reenterPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        getSupportActionBar().setDisplayShowTitleEnabled(false); // no title

        userNameEditText = findViewById(R.id.usernameEditText2);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        reenterPasswordEditText = findViewById(R.id.reenterPasswordEditText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.create_account_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.cancelMenuItem2:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Onclick listener for the create account button
    public void createAccountButtonOnClick(View view) {
        String username = userNameEditText.getText().toString();
        String newPassword = newPasswordEditText.getText().toString();
        String reenteredPassword = reenterPasswordEditText.getText().toString();

        if (!isInputEmpty(username, newPassword, reenteredPassword)) {
            if (newPassword.compareTo(reenteredPassword) != 0) {
                reenterPasswordEditText.setError("Password does not match");
            } else if (databaseHelper.doctorExists(username)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(CreateAccountActivity.this);
                alertBuilder.setTitle(getString(R.string.account_already_exists))
                        .setMessage(getString(R.string.account_already_exists_message))
                        .setPositiveButton(getString(R.string.ok), null);
                alertBuilder.show();

                userNameEditText.setError("Enter unique username");
            } else {
                int doctorId = databaseHelper.insertDoctor(username);

                Authenticate auth = new Authenticate(username, newPassword);
                String hashedPassword = auth.getHashedPassword(auth.getSalt());

                databaseHelper.insertLogin(username, auth.getSalt(), hashedPassword);

                Intent intent = new Intent(CreateAccountActivity.this, PatientListActivity.class);
                intent.putExtra("doctorId", doctorId);
                startActivity(intent);
            }
        }
    }

    // checks if any input is empty and alerts user with error message
    // returns true is any input is empty else returns false
    public boolean isInputEmpty(String username, String newPassword, String reenteredPassword) {
        boolean hasEmptyInput = false;

        if (username.length() == 0) {
            userNameEditText.setError("Enter username");
            hasEmptyInput = true;
        }

        if (newPassword.length() == 0) {
            newPasswordEditText.setError("Enter new password");
            hasEmptyInput = true;
        }

        if (reenteredPassword.length() == 0) {
            reenterPasswordEditText.setError("Re-enter password");
            hasEmptyInput = true;
        }

        return hasEmptyInput;
    }

    @Override
    protected void onResume() {
         // remove username and password from previous activity
        userNameEditText.setText("");
        newPasswordEditText.setText("");
        reenterPasswordEditText.setText("");

        super.onResume();
    }

    @Override
    protected void onStop() {
        this.finish();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
}
