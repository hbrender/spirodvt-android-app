package com.example.incentive_spirometer_and_dvt_application.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.incentive_spirometer_and_dvt_application.R;
import com.example.incentive_spirometer_and_dvt_application.helpers.DatabaseHelper;
import com.example.incentive_spirometer_and_dvt_application.models.Dvt;
import com.example.incentive_spirometer_and_dvt_application.models.IncentiveSpirometer;
import com.example.incentive_spirometer_and_dvt_application.models.Patient;
import com.google.android.material.snackbar.Snackbar;

/**
 * Patient info activity of the application
 * This screen displays information for a patient including name, age, sex, etc. Also this screen
 * displays device data.
 *
 * @author(s) Hanna Brender
 */

public class PatientInfoActivity extends AppCompatActivity {
    static final String TAG = "PatientInfoActivity";
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    int patientId;
    int doctorId;

    // Patient Information components
    EditText patientIdEditText;
    EditText firstNameEditText;
    EditText lastNameEditText;
    EditText heightFeetEditText;
    EditText heightInchesEditText;
    EditText weightPoundsEditText;
    EditText ageEditText;
    Spinner sexSpinner;

    // Incentive Spirometer components
    EditText spirometerIdEditText;
    EditText inhalationsNumIdEditText;
    EditText lungVolumeEditText;
    ImageView deleteSpirometerButton;

    // DVT Prevention Device components
    EditText dvtIdEditText;
    EditText repsNumIdEditText;
    Spinner dvtResistanceSpinner;
    ImageView deleteDvtButton;

    // Menu components
    MenuItem editMenuItem;
    MenuItem saveMenuItem;
    MenuItem cancelMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);

        getSupportActionBar().setDisplayShowTitleEnabled(false); // no title

        patientIdEditText = findViewById(R.id.patientIdEditText);
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        heightFeetEditText = findViewById(R.id.heightFeetEditText);
        heightInchesEditText = findViewById(R.id.heightInchesEditText);
        weightPoundsEditText = findViewById(R.id.weightPoundsEditText);
        ageEditText = findViewById(R.id.ageEditText);
        sexSpinner = findViewById(R.id.sexSpinner);
        spirometerIdEditText = findViewById(R.id.spirometerIdEditText);
        inhalationsNumIdEditText = findViewById(R.id.inhalationsNumIdEditText);
        lungVolumeEditText = findViewById(R.id.lungVolumeEditText);
        dvtIdEditText = findViewById(R.id.dvtIdEditText);
        repsNumIdEditText = findViewById(R.id.repsNumIdEditText);
        dvtResistanceSpinner = findViewById(R.id.dvtResistanceSpinner);
        deleteSpirometerButton = findViewById(R.id.deleteSpirometerButton);
        deleteDvtButton = findViewById(R.id.deleteDvtButton);

        // back menu item
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent != null) {
            patientId = intent.getIntExtra("patientId", -1);
            doctorId = intent.getIntExtra("doctorId", -1);

            // viewing existing patient info rather than creating a new patient
            if (patientId != -1) {
                Patient patient = databaseHelper.getPatient(patientId);
                IncentiveSpirometer incentiveSpirometer = databaseHelper.getIncentiveSpirometer(patientId);
                Dvt dvt = databaseHelper.getDvt(patientId);
                setPatientInfo(patient, incentiveSpirometer, dvt);
            }
        }
    }

    /**
     * Sets patients information in the view
     * @param patient has the information to set
     */
    public void setPatientInfo(Patient patient, IncentiveSpirometer incentiveSpirometer, Dvt dvt) {
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

        if (incentiveSpirometer != null) {
            spirometerIdEditText.setText(String.valueOf(incentiveSpirometer.getId()));
            inhalationsNumIdEditText.setText(String.valueOf(incentiveSpirometer.getNumberOfInhalations()));
            lungVolumeEditText.setText(String.valueOf(incentiveSpirometer.getLungVolume()));
        } else {
            spirometerIdEditText.setText(null);
            inhalationsNumIdEditText.setText(null);
            lungVolumeEditText.setText(null);
        }

        if (dvt != null) {
            dvtIdEditText.setText(String.valueOf(dvt.getId()));
            repsNumIdEditText.setText(String.valueOf(dvt.getNumberOfReps()));

            switch (dvt.getResistance()) {
                case "Easy":
                    dvtResistanceSpinner.setSelection(0);
                    break;
                case "Medium":
                    dvtResistanceSpinner.setSelection(1);
                    break;
                default:
                    dvtResistanceSpinner.setSelection(2);
            }
        } else {
            dvtIdEditText.setText(null);
            repsNumIdEditText.setText(null);
            dvtResistanceSpinner.setSelection(0);
        }

        disablePatientEdit();
    }

    /**
     * Save patient information
     * @return true if able to save patient info with not errors, false otherwise
     */
    public boolean savePatient() {
        // if no errors in user input
        if (!hasEmptyInput()) {
            // get info from view components
            Patient patient = getPatientInfo();
            IncentiveSpirometer incentiveSpirometer = getSpirometerInfo();
            Dvt dvt = getDvtInfo();

            disablePatientEdit(); // change edit texts to non-editable

            if (patientId != -1) { // editing existing patient info
                if (incentiveSpirometer != null) {
                    // check if need to update existing device or insert new device
                    if (!databaseHelper.incentiveSpirometerExists(incentiveSpirometer)) {
                        databaseHelper.insertIncentiveSpirometer(incentiveSpirometer);
                    } else {
                        int result1 = databaseHelper.updateIncentiveSpirometer(incentiveSpirometer);
                    }
                }

                if (dvt != null) {
                    // check if need to update existing device or insert new device
                    if (!databaseHelper.dvtExists(dvt)) {
                        databaseHelper.insertDvt(dvt);
                    } else {
                        int result2 = databaseHelper.updateDvt(dvt);
                    }
                }

                int result3 = databaseHelper.updatePatient(patient);
            } else {
                patientId = patient.getId();
                boolean result1, result2, result3, result4;

                if (incentiveSpirometer != null) {
                    result1 = databaseHelper.insertIncentiveSpirometer(incentiveSpirometer);
                }
                if (dvt != null) {
                    result2 = databaseHelper.insertDvt(dvt);
                }
                result3 = databaseHelper.insertPatient(patient);
                result4 = databaseHelper.insertDoctorPatient(patientId, doctorId);

                if (!result3 || !result4) {
                    Log.d(TAG, "savePatient: SQL Error inserting patient");
                    return false;
                }
            }
            // alert that a patient's info has been saved
            GridLayout gridLayout = findViewById(R.id.gridLayout);
            Snackbar snackbar = Snackbar.make(gridLayout, getString(R.string.patient_saved), Snackbar.LENGTH_LONG);
            snackbar.show();
            return true;
        }
        return false;
    }

    /**
     * Checks if user input for required input is empty and displays appropriate error messages
     * @return true if not empty input, false otherwise
     */
    public boolean hasEmptyInput() {
        boolean hasInputErrors = false;
        if (patientIdEditText.getText().length() == 0) {
            patientIdEditText.setError("Enter a unique id");
            hasInputErrors = true;
        }
        if (firstNameEditText.getText().length() == 0) {
            firstNameEditText.setError("Enter name");
            hasInputErrors = true;
        }
        if (lastNameEditText.getText().length() == 0) {
            lastNameEditText.setError("Enter name");
            hasInputErrors = true;
        }
        if (heightFeetEditText.getText().length() == 0) {
            heightFeetEditText.setError("Enter feet");
            hasInputErrors = true;
        }
        if (heightInchesEditText.getText().length() == 0) {
            heightInchesEditText.setError("Enter inches");
            hasInputErrors = true;
        }
        if (weightPoundsEditText.getText().length() == 0) {
            weightPoundsEditText.setError("Enter weight");
            hasInputErrors = true;
        }
        if (ageEditText.getText().length() == 0) {
            ageEditText.setError("Enter age");
            hasInputErrors = true;
        }

        if (spirometerIdEditText.getText().length() != 0
                || inhalationsNumIdEditText.getText().length() != 0
                || lungVolumeEditText.getText().length() != 0) {

            if (spirometerIdEditText.getText().length() == 0) {
                spirometerIdEditText.setError("Enter a unique id");
                hasInputErrors = true;
            }
            if (inhalationsNumIdEditText.getText().length() == 0) {
                inhalationsNumIdEditText.setError("Enter number");
                hasInputErrors = true;
            }
            if (lungVolumeEditText.getText().length() == 0) {
                lungVolumeEditText.setError("Enter number");
                hasInputErrors = true;
            }
        }

        if (dvtIdEditText.getText().length() != 0
                || repsNumIdEditText.getText().length() != 0) {

            if (dvtIdEditText.getText().length() == 0) {
                dvtIdEditText.setError("Enter a unique id");
                hasInputErrors = true;
            }
            if (repsNumIdEditText.getText().length() == 0) {
                repsNumIdEditText.setError("Enter number");
                hasInputErrors = true;
            }
        }
        return hasInputErrors;
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

        if (spirometerIdEditText.getText().toString().length() > 0) {
            patient.setIncentiveSpirometerId(Integer.parseInt(spirometerIdEditText.getText().toString()));
        }

        if (dvtIdEditText.getText().toString().length() > 0) {
            patient.setDvtId(Integer.parseInt(dvtIdEditText.getText().toString()));
        }

        return patient;
    }

    /**
     * Gets incentive spirometer information from the view
     * @return IncentiveSpirometer object
     */
    public IncentiveSpirometer getSpirometerInfo() {
        IncentiveSpirometer incentiveSpirometer = new IncentiveSpirometer();

        if (spirometerIdEditText.getText().toString().length() > 0) {
            incentiveSpirometer.setId(Integer.parseInt(spirometerIdEditText.getText().toString()));
            incentiveSpirometer.setLungVolume(Integer.parseInt(lungVolumeEditText.getText().toString()));
            incentiveSpirometer.setNumberOfInhalations(Integer.parseInt(inhalationsNumIdEditText.getText().toString()));

            return incentiveSpirometer;
        }

        return null;
    }

    /**
     * Gets DVT information from the view
     * @return Dvt object or null there are user input errors
     */
    public Dvt getDvtInfo() {
        Dvt dvt = new Dvt();

        if (dvtIdEditText.getText().toString().length() > 0) {
            dvt.setId(Integer.parseInt(dvtIdEditText.getText().toString()));
            dvt.setResistance(dvtResistanceSpinner.getSelectedItem().toString());
            dvt.setNumberOfReps(Integer.parseInt(repsNumIdEditText.getText().toString()));

            return dvt;
        }
        return null;
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
        sexSpinner.setEnabled(false);
        spirometerIdEditText.setEnabled(false);
        inhalationsNumIdEditText.setEnabled(false);
        lungVolumeEditText.setEnabled(false);
        dvtIdEditText.setEnabled(false);
        repsNumIdEditText.setEnabled(false);
        dvtResistanceSpinner.setEnabled(false);
        deleteSpirometerButton.setVisibility(View.INVISIBLE);
        deleteDvtButton.setVisibility(View.INVISIBLE);
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

        if (spirometerIdEditText.getText().toString().length() == 0) {
            spirometerIdEditText.setEnabled(true);
        } else {
            deleteSpirometerButton.setVisibility(View.VISIBLE);
        }

        inhalationsNumIdEditText.setEnabled(true);
        lungVolumeEditText.setEnabled(true);

        if (dvtIdEditText.getText().toString().length() == 0) {
            dvtIdEditText.setEnabled(true);
        } else {
            deleteDvtButton.setVisibility(View.VISIBLE);
        }
        repsNumIdEditText.setEnabled(true);
        dvtResistanceSpinner.setEnabled(true);
    }

    public void deleteSpirometerButtonClick(View view) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(PatientInfoActivity.this);
        alertBuilder.setTitle(getString(R.string.delete_spirometer))
                .setMessage(getString(R.string.message_delete_spirometer))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // delete spirometer from database
                        databaseHelper.deleteSpirometerById(Integer.parseInt(spirometerIdEditText.getText().toString()), patientId);

                        // reset device input
                        spirometerIdEditText.setText(null);
                        inhalationsNumIdEditText.setText(null);
                        lungVolumeEditText.setText(null);

                        // allow for a new device id
                        spirometerIdEditText.setEnabled(true);

                        // hide delete button
                        deleteSpirometerButton.setVisibility(View.INVISIBLE);

                        GridLayout gridLayout = findViewById(R.id.gridLayout);
                        Snackbar snackbar = Snackbar.make(gridLayout, getString(R.string.spirometer_deleted), Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                })
                .setNegativeButton(R.string.no, null);
        alertBuilder.show();
    }

    public void deleteDvtButtonClick(View view) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(PatientInfoActivity.this);
        alertBuilder.setTitle(getString(R.string.delete_dvt))
                .setMessage(getString(R.string.message_delete_dvt))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // delete dvt from database
                        databaseHelper.deleteDvtById(Integer.parseInt(dvtIdEditText.getText().toString()), patientId);

                        // reset device input
                        dvtIdEditText.setText(null);
                        repsNumIdEditText.setText(null);
                        dvtResistanceSpinner.setSelection(0);

                        // allow for a new device id
                        dvtIdEditText.setEnabled(true);

                        // hide delete button
                        deleteDvtButton.setVisibility(View.INVISIBLE);

                        GridLayout gridLayout = findViewById(R.id.gridLayout);
                        Snackbar snackbar = Snackbar.make(gridLayout, getString(R.string.dvt_deleted), Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                })
                .setNegativeButton(R.string.no, null);
        alertBuilder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.patient_info_menu, menu);

        editMenuItem = menu.findItem(R.id.editMenuItem);
        saveMenuItem = menu.findItem(R.id.saveMenuItem);
        cancelMenuItem = menu.findItem(R.id.cancelMenuItem);

        if (patientId == -1) {
            editMenuItem.setVisible(false);
            saveMenuItem.setVisible(true);
        }

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
                editMenuItem.setVisible(false);
                saveMenuItem.setVisible(true);
                cancelMenuItem.setVisible(true);
                return true;
            case R.id.saveMenuItem:
                // check if patient info can be saved with no errors
                if (savePatient()) {
                    editMenuItem.setVisible(true);
                    saveMenuItem.setVisible(false);
                    cancelMenuItem.setVisible(false);

                    // update name in toolbar if it was changed in the patient info activity
                    Intent intent = new Intent();
                    setResult(Activity.RESULT_OK, intent);
                }
                return true;
            case R.id.cancelMenuItem:
                // if editing existing patient info, cancel any changes made
                if (patientId != -1) {
                    Patient patient = databaseHelper.getPatient(patientId);
                    IncentiveSpirometer incentiveSpirometer = databaseHelper.getIncentiveSpirometer(patientId);
                    Dvt dvt = databaseHelper.getDvt(patientId);
                    setPatientInfo(patient, incentiveSpirometer, dvt);
                }

                editMenuItem.setVisible(true);
                saveMenuItem.setVisible(false);
                cancelMenuItem.setVisible(false);

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
