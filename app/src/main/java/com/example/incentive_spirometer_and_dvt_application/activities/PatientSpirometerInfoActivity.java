/*
Programmed by: Kelsey Lally
Created on: 11/4
Last Update: Kelsey Lally, 11/4

 */


package com.example.incentive_spirometer_and_dvt_application.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.incentive_spirometer_and_dvt_application.R;
import com.example.incentive_spirometer_and_dvt_application.helpers.DatabaseHelper;
import com.example.incentive_spirometer_and_dvt_application.models.IncentiveSpirometerData;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.sql.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class PatientSpirometerInfoActivity extends AppCompatActivity {
    static final String TAG = "PatientSpiroInfoAct";
    private DatabaseHelper databaseHelper;
    private List<IncentiveSpirometerData> spData;
    private ArrayAdapter<IncentiveSpirometerData> arrayAdapter;
    private ListView dataListView;
    private int patientId;
    private int doctorId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_spirometer_info);

        databaseHelper = new DatabaseHelper(this);



        Intent intent = getIntent();
        if (intent != null) {
            patientId = intent.getIntExtra("patientId", -1);
            Log.d(TAG, "onCreate: PATIENTID: " + patientId);
            doctorId = intent.getIntExtra("doctorId", -1);
        }

        createDataList ();
        drawGraph ();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // connection to database currently not working
        spData = new ArrayList<>();
//        spData = databaseHelper.getPatinetSpirometerData(patientId);
//        for (IncentiveSpirometerData i: spData) {
//            Log.d("data", i.toString());
//        }

//

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.spirometer_info_menu, menu);

        // set menu item icon color
//        Drawable drawable = menu.findItem(R.id.addMenuItem).getIcon();
//        drawable = DrawableCompat.wrap(drawable);
//        DrawableCompat.setTint(drawable, ContextCompat.getColor(this,R.color.colorAccent));
//        menu.findItem(R.id.addMenuItem).setIcon(drawable);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.patientDetailMenuItem:
                Intent intent = new Intent(PatientSpirometerInfoActivity.this, PatientInfoActivity.class); // change here
                intent.putExtra("patientId", patientId);
                intent.putExtra("doctorId", doctorId);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createDataList () {
        Log.d(TAG, "createDataList: Patient ID before call: " + patientId);
        spData = databaseHelper.getPatinetSpirometerData(patientId);
        for (IncentiveSpirometerData i: spData) {
            Log.d("data", i.toString());
        }
        //spData = testData();
        dataListView = (ListView) findViewById(R.id.patient_spirometer_table);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, spData);
        dataListView.setAdapter(arrayAdapter);
    }

    private ArrayList<IncentiveSpirometerData> testData () {
        ArrayList<IncentiveSpirometerData> testData = new ArrayList<IncentiveSpirometerData>();
        testData.add(new IncentiveSpirometerData(0, Timestamp.valueOf("2019-11-08 8:00:00"), Timestamp.valueOf("2019-11-08 8:59:59"), 2500, 10, 5));
        testData.add(new IncentiveSpirometerData(0, Timestamp.valueOf("2019-11-08 9:00:00"), Timestamp.valueOf("2019-11-08 9:59:59"), 2500, 10, 5));
        testData.add(new IncentiveSpirometerData(0, Timestamp.valueOf("2019-11-08 10:00:00"), Timestamp.valueOf("2019-11-08 10:59:59"), 2500, 10, 5));
        testData.add(new IncentiveSpirometerData(0, Timestamp.valueOf("2019-11-08 11:00:00"), Timestamp.valueOf("2019-11-08 11:59:59"), 2500, 10, 6));
        testData.add(new IncentiveSpirometerData(0, Timestamp.valueOf("2019-11-08 12:00:00"), Timestamp.valueOf("2019-11-08 12:59:59"), 2500, 10, 8));
        testData.add(new IncentiveSpirometerData(0, Timestamp.valueOf("2019-11-08 13:00:00"), Timestamp.valueOf("2019-11-08 13:59:59"), 2500, 10, 10));
        testData.add(new IncentiveSpirometerData(0, Timestamp.valueOf("2019-11-08 14:00:00"), Timestamp.valueOf("2019-11-08 14:59:59"), 2500, 10, 10));
        testData.add(new IncentiveSpirometerData(0, Timestamp.valueOf("2019-11-08 15:00:00"), Timestamp.valueOf("2019-11-08 15:59:59"), 2500, 10, 10));
        testData.add(new IncentiveSpirometerData(0, Timestamp.valueOf("2019-11-08 16:00:00"), Timestamp.valueOf("2019-11-08 16:59:59"), 2500, 10, 10));
        testData.add(new IncentiveSpirometerData(0, Timestamp.valueOf("2019-11-08 17:00:00"), Timestamp.valueOf("2019-11-08 17:59:59"), 2500, 10, 10));

        return testData;
    }

    private void drawGraph () {
        GraphView graph = (GraphView) findViewById(R.id.patient_spirometer_graph);
        ArrayList<DataPoint> initialGraphData = new ArrayList<>();

        for (IncentiveSpirometerData sp: spData){
            Calendar cs = GregorianCalendar.getInstance();
            cs.setTime(sp.getStartTime());
            initialGraphData.add(new DataPoint(cs.get(Calendar.HOUR_OF_DAY), sp.getInhalationsCompleted()));
        }

        DataPoint[] graphData = new DataPoint[initialGraphData.size()];
        for (int i = 0; i < initialGraphData.size(); i++){
            graphData[i] = initialGraphData.get(i);
        }

        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(graphData);


        graph.addSeries(series);
        series.setSpacing (30);

        graph.setTitle(this.getString(R.string.spirometer_graph_title));

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(12);

        graph.getViewport().setScrollable(true);

        graph.getGridLabelRenderer().setHorizontalAxisTitle("Time");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Breaths Performed");
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);

        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
                @Override
                public String formatLabel(double value, boolean isValueX){
                    if (isValueX){
                        return super.formatLabel(value, isValueX)  + ":00";
                    } else {
                        return super.formatLabel(value, isValueX);
                    }
                }
        });

    }

}
