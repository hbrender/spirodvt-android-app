/*
Programmed by: Kelsey Lally
Created on: 11/4
Last Update: Kelsey Lally, 11/4

 */


package com.example.incentive_spirometer_and_dvt_application.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.incentive_spirometer_and_dvt_application.R;
import com.jjoe64.graphview.GraphView;

public class PatientSpirometerInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_spirometer_info);
        GraphView graph = (GraphView) findViewById(R.id.graph);
    }
}
