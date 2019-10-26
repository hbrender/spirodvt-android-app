package com.example.incentive_spirometer_and_dvt_application.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.incentive_spirometer_and_dvt_application.R;

import org.w3c.dom.Text;

public class PatientListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);

        Intent intent = getIntent();
        if (intent != null) {
            String user = intent.getStringExtra("username");
            TextView test = (TextView) findViewById(R.id.testIntent);
            test.setText(user);
        }


    }
}
