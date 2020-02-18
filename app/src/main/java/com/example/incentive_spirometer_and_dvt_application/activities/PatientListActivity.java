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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.incentive_spirometer_and_dvt_application.R;
import com.example.incentive_spirometer_and_dvt_application.helpers.DatabaseHelper;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

/**
 * Patient list activity of the application
 * Doctors will be able to see a list of patients they are monitoring. They can selected patients
 * to get more detail, delete multiple patients from the list at a time, and also sign out or add a
 * patient to their list.
 *
 * @author(s) Hanna Brender, Cole deSilva
 */

public class PatientListActivity extends AppCompatActivity {
    static final String TAG = "PatientListActivityTag";
    private DatabaseHelper databaseHelper;
    private SimpleCursorAdapter simpleCursorAdapter;
    private ListView patientListView;
    private int doctorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);

        databaseHelper = new DatabaseHelper(this);

        Intent intent = getIntent();
        if (intent != null) {
            doctorId = intent.getIntExtra("doctorId", -1);
        }

        // remove old patients
        //List<Integer> oldPatients = databaseHelper.getOldPatients(doctorId);
        //for (Integer i : oldPatients) {
        //    databaseHelper.deletePatientById(i);
        //}

        createPatientsList();

        final SearchView searchView = findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // searches the database using the search input
                Cursor results = databaseHelper.getPatientListByKeyword(query, doctorId);

                // if the results are not empty then change the cursor and update
                if (results != null && results.getCount() > 0) {
                    results.moveToFirst();
                    simpleCursorAdapter.changeCursor(results);
                } else { // if the results are empty then use a snackbar to tell user there were no results
                    LinearLayout linearLayout = findViewById(R.id.masterLinearLayout);
                    Snackbar snackbar = Snackbar.make(linearLayout, getString(R.string.invalid_search), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                // revert to full patient list when search bar is closed
                updatePatientListView();
                return false;
            }
        });
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
        // set adapter for patient list
        patientListView = (ListView) findViewById(R.id.patientListView);
        Cursor cursor = databaseHelper.getAllPatientsCursor(doctorId);

        TextView emptyText = (TextView)findViewById(R.id.emptyPatientList);
        patientListView.setEmptyView(emptyText);

        simpleCursorAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_activated_2,
                cursor,
                new String[] {DatabaseHelper.FIRST_NAME},
                new int[] {android.R.id.text1},
                0) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // so that if you search the cursor doesnt default to the get all patients cursor
                Cursor cursor = this.getCursor();
                View view = super.getView(position, convertView, parent);

                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                if (cursor.moveToPosition(position)) {
                    text1.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.LAST_NAME))
                            + ", " + cursor.getString(cursor.getColumnIndex(DatabaseHelper.FIRST_NAME)));
                    text2.setText("ID: " + cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ID)));
                }
                return view;
            }
        };
        patientListView.setAdapter(simpleCursorAdapter);

        // click listener for viewing patient information
        patientListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);

                //Intent intent = new Intent(PatientListActivity.this, PatientSpirometerInfoActivity.class); // change here
                Intent intent = new Intent(PatientListActivity.this, DeviceDataActivity.class);
                intent.putExtra("patientId", cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ID)));
                intent.putExtra("firstName", cursor.getString(cursor.getColumnIndex(DatabaseHelper.FIRST_NAME)));
                intent.putExtra("lastName", cursor.getString(cursor.getColumnIndex(DatabaseHelper.LAST_NAME)));
                intent.putExtra("doctorId", doctorId);
                startActivity(intent);
            }
        });

        // set the listener for entering CAM, user long presses they can select multiple patients
        patientListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
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
                        final long[] checkIds = patientListView.getCheckedItemIds();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(PatientListActivity.this);
                        alertBuilder.setTitle(getString(R.string.delete_patient))
                                .setMessage(getString(R.string.message_delete_patients))
                                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // delete all selected patients
                                        for (long id: checkIds) {
                                            databaseHelper.deletePatientById((int) id);
                                            updatePatientListView();
                                        }
                                    }
                                })
                                .setNegativeButton(R.string.no, null);
                        alertBuilder.show();

                        mode.finish(); // exit cam
                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }
        });
    }

    /**
     * Updates the adapter and list view
     */
    public void updatePatientListView() {
        Cursor cursor = databaseHelper.getAllPatientsCursor(doctorId);
        simpleCursorAdapter.changeCursor(cursor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        updatePatientListView();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        databaseHelper.close();
        super.onDestroy();
    }
}