package com.example.incentive_spirometer_and_dvt_application.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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
    static final int CONNECT_REQUEST_CODE = 0;
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
    TextView spirometerIdTV;
    EditText spirometerIdEditText;
    EditText inhalationsNumIdEditText;
    EditText lungVolumeEditText;
    ImageView deleteSpirometerButton;
    Button connectSpiroButton;

    // DVT Prevention Device components
    TextView dvtIdTV;
    EditText dvtIdEditText;
    EditText repsNumIdEditText;
    Spinner dvtResistanceSpinner;
    ImageView deleteDvtButton;
    Button connectDVTButton;

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
        spirometerIdTV = findViewById(R.id.spirometerIdTextView);
        spirometerIdEditText = findViewById(R.id.spirometerIdEditText);
        inhalationsNumIdEditText = findViewById(R.id.inhalationsNumIdEditText);
        lungVolumeEditText = findViewById(R.id.lungVolumeEditText);
        dvtIdTV = findViewById(R.id.dvtIdTextView);
        dvtIdEditText = findViewById(R.id.dvtIdEditText);
        repsNumIdEditText = findViewById(R.id.repsNumIdEditText);
        dvtResistanceSpinner = findViewById(R.id.dvtResistanceSpinner);
        deleteSpirometerButton = findViewById(R.id.deleteSpirometerButton);
        deleteDvtButton = findViewById(R.id.deleteDvtButton);

        connectSpiroButton = findViewById(R.id.connectSpiroButton);
        connectDVTButton = findViewById(R.id.connectDVTButton);

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
        patientIdEditText.setText(patient.getPatientId());
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
            spirometerIdTV.setVisibility(View.VISIBLE);
            spirometerIdEditText.setVisibility(View.VISIBLE);
            spirometerIdEditText.setText(String.valueOf(incentiveSpirometer.getUuid()));
            inhalationsNumIdEditText.setText(String.valueOf(incentiveSpirometer.getNumberOfInhalations()));
            lungVolumeEditText.setText(String.valueOf(incentiveSpirometer.getLungVolume()));
            connectSpiroButton.setEnabled(false);
        } else {
            spirometerIdTV.setVisibility(View.GONE);
            spirometerIdEditText.setVisibility(View.GONE);
            spirometerIdEditText.setText(null);
            inhalationsNumIdEditText.setText(null);
            lungVolumeEditText.setText(null);
            connectSpiroButton.setEnabled(true);
        }

        if (dvt != null) {
            dvtIdTV.setVisibility(View.VISIBLE);
            dvtIdEditText.setVisibility(View.VISIBLE);
            dvtIdEditText.setText(String.valueOf(dvt.getUuid()));
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
            connectDVTButton.setEnabled(false);
        } else {
            dvtIdTV.setVisibility(View.GONE);
            dvtIdEditText.setVisibility(View.GONE);
            dvtIdEditText.setText(null);
            repsNumIdEditText.setText(null);
            dvtResistanceSpinner.setSelection(0);
            connectDVTButton.setEnabled(true);
        }

        disablePatientEdit();
    }

    /**
     * Save patient information
     * @return true if able to save patient info with not errors, false otherwise
     */
    public boolean savePatient() {
        // if no errors in user input and the patient id is unique
        if (!hasEmptyInput() && uniquePatientId()) {
            // get info from view components
            Patient patient = getPatientInfo();
            IncentiveSpirometer incentiveSpirometer = getSpirometerInfo();
            Dvt dvt = getDvtInfo();

            if (incentiveSpirometer != null) {
                if (databaseHelper.incentiveSpirometerExists(incentiveSpirometer)) {
                    // make sure device uuid has not already been used
                    if (databaseHelper.getPatientByIncentiveSpriometerUuid(incentiveSpirometer.getUuid()) != patientId) {
                        spirometerIdEditText.setError("Enter a unique id");

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(PatientInfoActivity.this);
                        alertBuilder.setTitle(getString(R.string.not_unique_device_id))
                                .setMessage(getString(R.string.not_unique_device_id_message))
                                .setPositiveButton(getString(R.string.ok), null);
                        alertBuilder.show();

                        return false;
                    } else if (patientId != -1) {
                        databaseHelper.updateIncentiveSpirometer(incentiveSpirometer);
                    }
                } else {
                    databaseHelper.insertIncentiveSpirometer(incentiveSpirometer);
                    databaseHelper.updateIncentiveSpiroForPatient(incentiveSpirometer, patientId);
                }
            }

            if (dvt != null) {
                if (databaseHelper.dvtExists(dvt)) {
                    // make sure device uuid has not already been used
                    if (databaseHelper.getPatientByDvtUuid(dvt.getUuid()) != patientId) {
                        dvtIdEditText.setError("Enter a unique id");

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(PatientInfoActivity.this);
                        alertBuilder.setTitle(getString(R.string.not_unique_device_id))
                                .setMessage(getString(R.string.not_unique_device_id_message))
                                .setPositiveButton(getString(R.string.ok), null);
                        alertBuilder.show();

                        return false;
                    } else if (patientId != -1) {
                        databaseHelper.updateDvt(dvt);
                    }
                } else {
                    databaseHelper.insertDvt(dvt);
                    databaseHelper.updateDvtForPatient(dvt, patientId);
                }
            }

            if (patientId != -1) { // editing existing patient info
                databaseHelper.updatePatient(patient);
            } else { // creating new patient
                patientId = patient.getId();
                boolean result3 = databaseHelper.insertPatient(patient);
                boolean result4 = databaseHelper.insertDoctorPatient(patientId, doctorId);

                if (!result3 || !result4) {
                    Log.d(TAG, "savePatient: SQL Error inserting patient");
                    return false;
                }
            }

            disablePatientEdit(); // change edit texts to non-editable

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
     * Checks that the typed in patient id is unique
     * @return true if unique, false otherwise
     */
    public boolean uniquePatientId() {
        // if patient with the ID doesn't exists in database or not creating a new patient (patientId != -1)
        if(!databaseHelper.patientExists(patientIdEditText.getText().toString()) || patientId != -1) {
            return true;
        }

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(PatientInfoActivity.this);
        alertBuilder.setTitle(getString(R.string.not_unique_id))
                .setMessage(getString(R.string.not_unique_id_message))
                .setPositiveButton(getString(R.string.ok), null);
        alertBuilder.show();

        patientIdEditText.setError("Enter a unique id");

        return false;
    }

    /**
     * Gets patient information from the view
     * @return Patient object
     */
    public Patient getPatientInfo() {
        Patient patient = new Patient();

        patient.setPatientId(patientIdEditText.getText().toString());
        patient.setFirstName(firstNameEditText.getText().toString());
        patient.setLastName(lastNameEditText.getText().toString());
        patient.setHeightFeet(Integer.parseInt(heightFeetEditText.getText().toString()));
        patient.setHeightInches(Double.parseDouble(heightInchesEditText.getText().toString()));
        patient.setWeight(Double.parseDouble(weightPoundsEditText.getText().toString()));
        patient.setAge(Integer.parseInt(ageEditText.getText().toString()));
        patient.setSex(sexSpinner.getSelectedItem().toString());

        if (spirometerIdEditText.getText().toString().length() > 0) {
            patient.setIncentiveSpirometerUuid(spirometerIdEditText.getText().toString());
        }
//        else{
//            patient.setIncentiveSpirometerUuid("none");
//        }

        if (dvtIdEditText.getText().toString().length() > 0) {
            patient.setDvtUuid(dvtIdEditText.getText().toString());
        }
//        else{
//            patient.setDvtUuid("none");
//        }

        return patient;
    }

    /**
     * Gets incentive spirometer information from the view
     * @return IncentiveSpirometer object
     */
    public IncentiveSpirometer getSpirometerInfo() {
        IncentiveSpirometer incentiveSpirometer = new IncentiveSpirometer();

        if (spirometerIdEditText.getText().toString().length() > 0) {
            incentiveSpirometer.setUuid(spirometerIdEditText.getText().toString());
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

            dvt.setUuid(dvtIdEditText.getText().toString());
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
        connectSpiroButton.setVisibility(View.GONE);
        connectDVTButton.setVisibility(View.GONE);
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
            spirometerIdEditText.setEnabled(false);
            connectSpiroButton.setVisibility(View.VISIBLE);
        } else {
            connectSpiroButton.setVisibility(View.GONE);
            deleteSpirometerButton.setVisibility(View.VISIBLE);
        }

        inhalationsNumIdEditText.setEnabled(true);
        lungVolumeEditText.setEnabled(true);

        if (dvtIdEditText.getText().toString().length() == 0) {
            connectDVTButton.setVisibility(View.VISIBLE);
            dvtIdEditText.setEnabled(false);
        } else {
            connectDVTButton.setVisibility(View.GONE);
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
                        IncentiveSpirometer spiro = databaseHelper.getIncentiveSpirometer(patientId);
                        databaseHelper.deleteSpirometerById(spiro.getId(), patientId);

                        // reset device input
                        spirometerIdEditText.setText("");
                        inhalationsNumIdEditText.setText(null);
                        lungVolumeEditText.setText(null);

                        // allow for a new device id
                        spirometerIdTV.setVisibility(View.GONE);
                        spirometerIdEditText.setVisibility(View.GONE);
                        spirometerIdEditText.setEnabled(false);

                        connectSpiroButton.setVisibility(View.VISIBLE);
                        connectSpiroButton.setEnabled(true);
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
                        Dvt dvt = databaseHelper.getDvt(patientId);
                        databaseHelper.deleteDvtById(dvt.getId(), patientId);

                        // reset device input
                        dvtIdEditText.setText("");
                        repsNumIdEditText.setText(null);
                        dvtResistanceSpinner.setSelection(0);

                        // allow for a new device id
                        dvtIdTV.setVisibility(View.GONE);
                        dvtIdEditText.setVisibility(View.GONE);
                        dvtIdEditText.setEnabled(false);

                        connectDVTButton.setVisibility(View.VISIBLE);
                        connectDVTButton.setEnabled(true);

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

                // remove error messages if user cancels editing
                spirometerIdEditText.setError(null);
                dvtIdEditText.setError(null);

                editMenuItem.setVisible(true);
                saveMenuItem.setVisible(false);
                cancelMenuItem.setVisible(false);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void connectDev(View v){
        Button view = (Button) v;
        Intent intent = new Intent(this, ConnectDevice.class);

        if(view.getId() == R.id.connectSpiroButton){
            intent.putExtra("isSpiro", true);
        }
        else if (view.getId() == R.id.connectDVTButton){
            intent.putExtra("isSpiro", false);
        }
        startActivityForResult(intent, CONNECT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: result has been gotten");
        Log.d(TAG, "onActivityResult: " + (data == null));
        if(data != null){
            if(requestCode == CONNECT_REQUEST_CODE && resultCode == RESULT_OK){
                boolean isSpiroResult = data.getBooleanExtra("isSpiro", true);
                String tempId = data.getStringExtra("idThingy");
                Log.d(TAG, "onActivityResult: " + tempId + " " + isSpiroResult);

                if(isSpiroResult && !tempId.equals("none")){
                    spirometerIdTV.setVisibility(View.VISIBLE);
                    spirometerIdEditText.setVisibility(View.VISIBLE);
                    spirometerIdEditText.setText(tempId+"");
                    spirometerIdEditText.setEnabled(false);
                    connectSpiroButton.setEnabled(false);
                }
                else if(!isSpiroResult && !tempId.equals("none")){
                    dvtIdTV.setVisibility(View.VISIBLE);
                    dvtIdEditText.setVisibility(View.VISIBLE);
                    dvtIdEditText.setText(tempId+"");
                    dvtIdEditText.setEnabled(false);
                    connectDVTButton.setEnabled(false);
                }

            }
            else if(requestCode == CONNECT_REQUEST_CODE && resultCode == RESULT_CANCELED){
                GridLayout layout = findViewById(R.id.gridLayout);
                Snackbar.make(layout,"Error: not able to get connection with device to store the ID", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
}
