package com.example.incentive_spirometer_and_dvt_application.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
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
    }

    public void onClickSave(View view) {
        EditText patientIdEditText = (EditText) findViewById(R.id.patientIdEditText);

        if (patientIdEditText.getText().length() == 0) {
            Toast.makeText(this, "Enter valid patient ID", Toast.LENGTH_SHORT).show();
        } else {
            Patient patient = createPatient(view);
            databaseHelper.insertPatient(patient);
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
        patient.setLastNames(lastNameEditText.getText().toString());
        patient.setHeightFeet(Integer.parseInt(heightFeetEditText.getText().toString()));
        patient.setHeightInches(Integer.parseInt(heightInchesEditText.getText().toString()));
        patient.setWeight(Double.parseDouble(weightPoundsEditText.getText().toString()));
        patient.setAge(Integer.parseInt(ageEditText.getText().toString()));
        patient.setSex(sexSpinner.getSelectedItem().toString());

        return patient;
    }

    public void onClickCancel(View view) {

    }
}
