package com.example.incentive_spirometer_and_dvt_application.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    DatabaseHelper databaseHelper;
    List<String> patientList;
    ArrayAdapter<String> arrayAdapter;
    // temporary list of fake patients for display testing
    //String[] patientArray = {"Jenny", "Brooke", "James", "Peter", "Mary", "Joseph", "Albert", "Patrick", "Mario", "Harry", "Alice", "Bella", "Fred", "George", "Marshall", "Teegan", "Lauren"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);

        databaseHelper = new DatabaseHelper(this);
        patientList = new ArrayList<>();

        addTestData();
        setPatientListData(this);

        //ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_patient_listview, patientArray);

        //ListView listView = (ListView) findViewById(R.id.patientListView);
        //listView.setAdapter(adapter);

        Intent intent = getIntent();
        if (intent != null) {
            String user = intent.getStringExtra("username");
            //TextView test = (TextView) findViewById(R.id.testIntent);
            //test.setText(user);
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
        Cursor cursor = databaseHelper.viewPatients();
        patientList = new ArrayList<>();

        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                // lastname, firstname
                patientList.add(cursor.getString(2) + ", " + cursor.getString(1));
                //patientList.sort();
            }

            arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, patientList);

            ListView patientListView = (ListView) findViewById(R.id.patientListView);
            patientListView.setAdapter(arrayAdapter);
        }
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
}
