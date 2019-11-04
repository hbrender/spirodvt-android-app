package com.example.incentive_spirometer_and_dvt_application.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.incentive_spirometer_and_dvt_application.R;
import com.example.incentive_spirometer_and_dvt_application.helpers.DatabaseHelper;
import com.example.incentive_spirometer_and_dvt_application.models.Patient;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class PatientListActivity extends AppCompatActivity {
    static final String TAG = "PatientListActivityTag";
    private DatabaseHelper databaseHelper;
    private List<Patient> patientList;
    private ArrayAdapter<Patient> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);

        databaseHelper = new DatabaseHelper(this);
        patientList = new ArrayList<>();

        addTestData();
        setPatientListData(this);

        Intent intent = getIntent();
        if (intent != null) {
            String user = intent.getStringExtra("username");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.patient_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void startPatientInfoActivity() {
        Intent intent = new Intent(this, PatientInfoActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.addMenuItem:
                startPatientInfoActivity();
                return true;
            case R.id.signOutMenuItem:
                PatientListActivity.this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setPatientListData(PatientListActivity context) {
        // get list of patients from the database
        patientList = databaseHelper.getAllPatients();

        // set adapter for patient list
        ListView patientListView = (ListView) findViewById(R.id.patientListView);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, patientList);
        patientListView.setAdapter(arrayAdapter);

        // click listener for viewing patient information
        patientListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Patient patient = (Patient) parent.getItemAtPosition(position);

                Intent intent = new Intent(PatientListActivity.this, PatientInfoActivity.class);
                intent.putExtra("patientId", patient.getId());
                startActivity(intent);
            }
        });

        // long click listener for deleting patient
        patientListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final Patient patient = (Patient) parent.getItemAtPosition(position);

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(PatientListActivity.this);
                alertBuilder.setTitle(getString(R.string.delete_patient))
                        .setMessage(getString(R.string.message_delete_patient) + " " + patient.getFirstName() + " " + patient.getLastName() + "?")
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // delete patient from database
                                databaseHelper.deletePatient(patient.getId());
                                setPatientListData(PatientListActivity.this);
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                alertBuilder.show();

                return true;
            }
        });
    }

    private void addTestData() {
        Patient p1 = new Patient(2, "John", "Johnson", 0, 0, 0, 0, "Male", 0, 0);
        Patient p2 = new Patient(3, "Lucy", "Riley", 0, 0, 0, 0, "Female", 0, 0);
        Patient p3 = new Patient(4, "Sean", "Wilson", 0, 0, 0, 0, "Other", 0, 0);
        Patient p4 = new Patient(5, "Sean", "Fred", 0, 0, 0, 0, "Male", 0, 0);
        Patient p5 = new Patient(6, "Sammy", "Martinez", 0, 0, 0, 0, "Female", 0, 0);
        Patient p6 = new Patient(7, "Nicole", "Meyers", 0, 0, 0, 0, "Female", 0, 0);

        databaseHelper.insertPatient(p1);
        databaseHelper.insertPatient(p2);
        databaseHelper.insertPatient(p3);
        databaseHelper.insertPatient(p4);
        databaseHelper.insertPatient(p5);
        databaseHelper.insertPatient(p6);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setPatientListData(this);
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }

}
