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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.incentive_spirometer_and_dvt_application.R;
import com.example.incentive_spirometer_and_dvt_application.helpers.DatabaseHelper;
import com.google.android.material.snackbar.Snackbar;

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

        // this chunk of code makes it so the search button is only enabled when there is text in the edit text
        // otherwise the search button is disabled
        // when the user erases all text in the edit text, the default getallpatients cursor is used to populate the list view
        final Button searchSubmit = (Button) findViewById(R.id.searchSubmit);
        final EditText searchEditText = (EditText) findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchInput = searchEditText.getText().toString().trim();
                searchSubmit.setEnabled(!searchInput.isEmpty());

                if(searchInput.isEmpty()) {
                    updatePatientListView();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        createPatientsList();
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
     * on click listener for the search button
     * @param v the search button
     */
    public void onClick(View v) {
        // only does anything if it's the search button that is pressed
        if (v.getId() == R.id.searchSubmit) {
            // gets the edittext input
            EditText userInput = (EditText) findViewById(R.id.searchEditText);
            String patientLastName = userInput.getText().toString();

            // searches the database using the input
            Cursor results = databaseHelper.searchForPatients(patientLastName, doctorId);

            // if the results are not empty then change the cursor and update
            if (results != null && results.getCount() > 0) {
                results.moveToFirst();
                String name = results.getString(results.getColumnIndex("lastName"));
                simpleCursorAdapter.changeCursor(results);
            } else { // if the results are empty then use a snackbar to tell user there were no results
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.masterLinearLayout);
                Snackbar snackbar = Snackbar.make(linearLayout, getString(R.string.invalid_search), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }
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