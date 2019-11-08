package com.example.incentive_spirometer_and_dvt_application.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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
    static final String TAG = "PatientInfoActivity";
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    int patientId;
    EditText patientIdEditText;
    EditText firstNameEditText;
    EditText lastNameEditText;
    EditText heightFeetEditText;
    EditText heightInchesEditText;
    EditText weightPoundsEditText;
    EditText ageEditText;
    Spinner sexSpinner;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);

        patientIdEditText = (EditText) findViewById(R.id.patientIdEditText);
        firstNameEditText = (EditText) findViewById(R.id.firstNameEditText);
        lastNameEditText = (EditText) findViewById(R.id.lastNameEditText);
        heightFeetEditText = (EditText) findViewById(R.id.heightFeetEditText);
        heightInchesEditText = (EditText) findViewById(R.id.heightInchesEditText);
        weightPoundsEditText = (EditText) findViewById(R.id.weightPoundsEditText);
        ageEditText = (EditText) findViewById(R.id.ageEditText);
        sexSpinner = (Spinner) findViewById(R.id.sexSpinner);
        saveButton = (Button) findViewById(R.id.saveButton);

        // back menu item
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent != null) {
            patientId = intent.getIntExtra("patientId", -1);

            if (patientId != -1) {
                Patient patient = databaseHelper.getPatient(patientId);
                setPatientInfo(patient);
            }
        }
    }

    public void setPatientInfo(Patient patient) {
        patientIdEditText.setText(String.valueOf(patient.getId()));
        firstNameEditText.setText(patient.getFirstName());
        lastNameEditText.setText(patient.getLastName());
        heightFeetEditText.setText(String.valueOf(patient.getHeightFeet()));
        heightInchesEditText.setText(String.valueOf(patient.getHeightInches()));
        weightPoundsEditText.setText(String.valueOf(patient.getWeight()));
        ageEditText.setText(String.valueOf(patient.getAge()));

        switch (patient.getSex()) {
            case "Female":
                sexSpinner.setSelection(0);
                break;
            case "Male":
                sexSpinner.setSelection(1);
                break;
            default:
                sexSpinner.setSelection(2);
                break;
        }

        disablePatientEdit();
        saveButton.setVisibility(View.GONE);
    }

    public void onClickSave(View view) {
        EditText patientIdEditText = (EditText) findViewById(R.id.patientIdEditText);

        if (patientIdEditText.getText().length() == 0) {
            Toast.makeText(this, "Enter valid patient ID", Toast.LENGTH_SHORT).show();
        } else if (patientId != -1) { // editing existing patient info
            Patient patient = savePatient(view);
            int result = databaseHelper.updatePatient(patient);
        } else { // saving info for newly created patient
            Patient patient = savePatient(view);
            patientId = patient.getId();
            boolean result = databaseHelper.insertPatient(patient);
            if (!result) {
                Toast.makeText(this, "SQL Error inserting patient", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Patient savePatient(View view) {
        // create patient object
        Patient patient = new Patient();
        patient.setId(Integer.parseInt(patientIdEditText.getText().toString()));
        patient.setFirstName(firstNameEditText.getText().toString());
        patient.setLastName(lastNameEditText.getText().toString());
        patient.setHeightFeet(Integer.parseInt(heightFeetEditText.getText().toString()));
        patient.setHeightInches(Double.parseDouble(heightInchesEditText.getText().toString()));
        patient.setWeight(Double.parseDouble(weightPoundsEditText.getText().toString()));
        patient.setAge(Integer.parseInt(ageEditText.getText().toString()));
        patient.setSex(sexSpinner.getSelectedItem().toString());

        // change edit texts to non-editable
        disablePatientEdit();

        // hide save button
        saveButton.setVisibility(View.GONE);

        return patient;
    }

    public void disablePatientEdit() {
        patientIdEditText.setEnabled(false);
        firstNameEditText.setEnabled(false);
        lastNameEditText.setEnabled(false);
        heightFeetEditText.setEnabled(false);
        heightInchesEditText.setEnabled(false);
        weightPoundsEditText.setEnabled(false);
        ageEditText.setEnabled(false);
        sexSpinner.setEnabled(false);
    }

    public void enablePatientEdit() {
        //patientIdEditText.setEnabled(true);
        firstNameEditText.setEnabled(true);
        lastNameEditText.setEnabled(true);
        heightFeetEditText.setEnabled(true);
        heightInchesEditText.setEnabled(true);
        weightPoundsEditText.setEnabled(true);
        ageEditText.setEnabled(true);
        sexSpinner.setEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.patient_info_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.editMenuItem:
                enablePatientEdit();
                saveButton.setVisibility(View.VISIBLE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
}
