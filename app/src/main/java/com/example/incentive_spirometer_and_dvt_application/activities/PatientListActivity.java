package com.example.incentive_spirometer_and_dvt_application.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.incentive_spirometer_and_dvt_application.R;

import org.w3c.dom.Text;

public class PatientListActivity extends AppCompatActivity {

    // temporary list of fake patients for display testing
    String[] patientArray = {"Jenny", "Brooke", "James", "Peter", "Mary", "Joseph", "Albert", "Patrick", "Mario", "Harry", "Alice", "Bella", "Fred", "George", "Marshall", "Teegan", "Lauren"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_patient_listview, patientArray);

        ListView listView = (ListView) findViewById(R.id.patientList);
        listView.setAdapter(adapter);

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.addMenuItem:
                // to do
                return true; // we consumed/handled the event
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
