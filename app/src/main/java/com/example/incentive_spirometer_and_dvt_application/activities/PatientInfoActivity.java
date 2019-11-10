package com.example.incentive_spirometer_and_dvt_application.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
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
    int doctorId;
    EditText patientIdEditText;
    EditText firstNameEditText;
    EditText lastNameEditText;
    EditText heightFeetEditText;
    EditText heightInchesEditText;
    EditText weightPoundsEditText;
    EditText ageEditText;
    Spinner sexSpinner;
    Button saveButton;
    MenuItem editMenuItem;
    MenuItem saveMenuItem;

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

        // back menu item
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent != null) {
            patientId = intent.getIntExtra("patientId", -1);
            doctorId = intent.getIntExtra("doctorId", -1);

            if (patientId != -1) {
                Patient patient = databaseHelper.getPatient(patientId);
                setPatientInfo(patient);
            }
        }
    }

    /**
     * Sets patients information in the view
     * @param patient has the information to set
     */
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
    }

    /**
     * Save patient information
     */
    public void savePatient() {
        if (patientIdEditText.getText().length() == 0) {
            Toast.makeText(this, "Enter valid patient ID", Toast.LENGTH_SHORT).show();
        } else {
            Patient patient = getPatientInfo();
            disablePatientEdit(); // change edit texts to non-editable

            if (patientId != -1) { // editing existing patient info
                int result = databaseHelper.updatePatient(patient);
            } else {
                patientId = patient.getId();
                boolean result = databaseHelper.insertPatient(patient);
                boolean result2 = databaseHelper.insertDoctorPatient(patientId, doctorId);
                if (!result || !result2) {
                    Toast.makeText(this, "SQL Error inserting patient", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Gets patient information from the view
     * @return Patient object
     */
    public Patient getPatientInfo() {
        Patient patient = new Patient();
        patient.setId(Integer.parseInt(patientIdEditText.getText().toString()));
        patient.setFirstName(firstNameEditText.getText().toString());
        patient.setLastName(lastNameEditText.getText().toString());
        patient.setHeightFeet(Integer.parseInt(heightFeetEditText.getText().toString()));
        patient.setHeightInches(Double.parseDouble(heightInchesEditText.getText().toString()));
        patient.setWeight(Double.parseDouble(weightPoundsEditText.getText().toString()));
        patient.setAge(Integer.parseInt(ageEditText.getText().toString()));
        patient.setSex(sexSpinner.getSelectedItem().toString());

        return patient;
    }

    /**
     * Disables editing of patient information
     */
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

    /**
     * Enables editing of patient information
     */
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

        editMenuItem = menu.findItem(R.id.editMenuItem);
        saveMenuItem = menu.findItem(R.id.saveMenuItem);

        if (patientId == -1) {
            editMenuItem.setVisible(false);
            saveMenuItem.setVisible(true);
        }

        // set menu item icon color
        Drawable drawable = menu.findItem(R.id.editMenuItem).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this,R.color.colorAccent));
        menu.findItem(R.id.editMenuItem).setIcon(drawable);

        drawable = menu.findItem(R.id.saveMenuItem).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this,R.color.colorAccent));
        menu.findItem(R.id.saveMenuItem).setIcon(drawable);

        /*drawable = menu.findItem(android.R.id.home).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this,R.color.colorAccent));
        menu.findItem(android.R.id.home).setIcon(drawable);*/

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
                //saveButton.setVisibility(View.VISIBLE);
                editMenuItem.setVisible(false);
                saveMenuItem.setVisible(true);
                return true;
            case R.id.saveMenuItem:
                savePatient();
                editMenuItem.setVisible(true);
                saveMenuItem.setVisible(false);
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
