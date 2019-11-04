package com.example.incentive_spirometer_and_dvt_application.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.incentive_spirometer_and_dvt_application.R;
import com.example.incentive_spirometer_and_dvt_application.helpers.DatabaseHelper;
import com.example.incentive_spirometer_and_dvt_application.models.Patient;

public class PatientInfoActivity extends AppCompatActivity {
    DatabaseHelper databaseHelper = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);

        // back menu item
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent != null) {
            int patientId = intent.getIntExtra("patientId", -1);
        }
    }

    public void setPatientInfo(int patientId) {

    }

    public void onClickSave(View view) {
        EditText patientIdEditText = (EditText) findViewById(R.id.patientIdEditText);

        if (patientIdEditText.getText().length() == 0) {
            Toast.makeText(this, "Enter valid patient ID", Toast.LENGTH_SHORT).show();
        } else {
            Patient patient = createPatient(view);
            boolean result = databaseHelper.insertPatient(patient);
            if (!result) {
                Toast.makeText(this, "SQL Error inserting patient", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Patient createPatient(View view) {
        EditText patientIdEditText = (EditText) findViewById(R.id.patientIdEditText);
        EditText firstNameEditText = (EditText) findViewById(R.id.firstNameEditText);
        EditText lastNameEditText = (EditText) findViewById(R.id.lastNameEditText);
        EditText heightFeetEditText = (EditText) findViewById(R.id.heightFeetEditText);
        EditText heightInchesEditText = (EditText) findViewById(R.id.heightInchesEditText);
        EditText weightPoundsEditText = (EditText) findViewById(R.id.weightPoundsEditText);
        EditText ageEditText = (EditText) findViewById(R.id.ageEditText);
        Spinner sexSpinner = (Spinner) findViewById(R.id.sexSpinner);

        // create patient object
        Patient patient = new Patient();
        patient.setId(Integer.parseInt(patientIdEditText.getText().toString()));
        patient.setFirstName(firstNameEditText.getText().toString());
        patient.setLastName(lastNameEditText.getText().toString());
        patient.setHeightFeet(Integer.parseInt(heightFeetEditText.getText().toString()));
        patient.setHeightInches(Integer.parseInt(heightInchesEditText.getText().toString()));
        patient.setWeight(Double.parseDouble(weightPoundsEditText.getText().toString()));
        patient.setAge(Integer.parseInt(ageEditText.getText().toString()));
        patient.setSex(sexSpinner.getSelectedItem().toString());

        // change edit texts to non-editable
        patientIdEditText.setEnabled(false);
        firstNameEditText.setEnabled(false);
        lastNameEditText.setEnabled(false);
        heightFeetEditText.setEnabled(false);
        heightInchesEditText.setEnabled(false);
        weightPoundsEditText.setEnabled(false);
        ageEditText.setEnabled(false);
        sexSpinner.setEnabled(false);

        // hide save button
        Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setVisibility(View.GONE);

        return patient;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
