package com.example.incentive_spirometer_and_dvt_application.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
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
    private ListView patientListView;
    private int doctorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);

        databaseHelper = new DatabaseHelper(this);
        patientList = new ArrayList<>();

        createPatientsList();

        Intent intent = getIntent();
        if (intent != null) {
            doctorId = intent.getIntExtra("doctorId", -1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.patient_list_menu, menu);

        // set menu item icon color
        Drawable drawable = menu.findItem(R.id.addMenuItem).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this,R.color.colorAccent));
        menu.findItem(R.id.addMenuItem).setIcon(drawable);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.addMenuItem:
                Intent intent = new Intent(this, PatientInfoActivity.class);
                intent.putExtra("doctorId", doctorId);
                startActivity(intent);
                return true;
            case R.id.signOutMenuItem:
                PatientListActivity.this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createPatientsList() {
        // get list of patients from the database
        patientList = databaseHelper.getAllPatients(doctorId);

        // set adapter for patient list
        patientListView = (ListView) findViewById(R.id.patientListView);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, patientList);
        patientListView.setAdapter(arrayAdapter);

        // click listener for viewing patient information
        patientListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Patient patient = (Patient) parent.getItemAtPosition(position);

                Intent intent = new Intent(PatientListActivity.this, PatientInfoActivity.class);
                intent.putExtra("patientId", patient.getId());
                intent.putExtra("doctorId", doctorId);
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
                                // TODO: delete from DoctorPatient table
                                updatePatientList();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                alertBuilder.show();

                return true;
            }
        });

        // set the listener for entering CAM, user long presses they can select multiple patients
        /*patientListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        patientListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                int numChecked = patientListView.getCheckedItemCount();
                mode.setTitle(numChecked + " selected");
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater menuInflater = getMenuInflater();
                menuInflater.inflate(R.menu.cam_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.deleteMenuItem:
                        // to do delete
                        mode.finish(); // exit cam
                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }
        });*/
    }

    public void updatePatientList() {
        patientList.clear();
        patientList.addAll(databaseHelper.getAllPatients(doctorId));
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePatientList();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        databaseHelper.close();
        super.onDestroy();
    }
}