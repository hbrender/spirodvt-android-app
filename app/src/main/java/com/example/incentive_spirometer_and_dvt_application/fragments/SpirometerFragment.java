package com.example.incentive_spirometer_and_dvt_application.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.incentive_spirometer_and_dvt_application.R;
import com.example.incentive_spirometer_and_dvt_application.activities.PatientSpirometerInfoActivity;
import com.example.incentive_spirometer_and_dvt_application.helpers.DatabaseHelper;
import com.example.incentive_spirometer_and_dvt_application.models.IncentiveSpirometer;
import com.example.incentive_spirometer_and_dvt_application.models.IncentiveSpirometerData;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class SpirometerFragment extends Fragment implements View.OnClickListener {
    static final String TAG = "PatientSpiroInfoAct";
    private DatabaseHelper databaseHelper;
    private List<IncentiveSpirometerData> allSpData;
    private List<BarEntry> oneDaySpData;
    private List<BarEntry> twoDaySpData;
    private List<BarEntry> threeDaySpData;
    private List<BarEntry> oneWeekSpData;
    private List<BarEntry> barEntryList;
    private ListView dataListView;
    private BarChart graph;
    private int patientId;
    private int doctorId;
    private SpirometerFragment.TimeShown timeShown;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            patientId = bundle.getInt("patientId", -1);
            doctorId = bundle.getInt("doctorId", -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.activity_patient_spirometer_info, container, false);

        Button oneDayButton = (Button) view.findViewById(R.id.one_day_button);
        Button twoDayButton = (Button) view.findViewById(R.id.two_day_button);
        Button threeDayButton = (Button) view.findViewById(R.id.three_day_button);
        Button weekButton = (Button) view.findViewById(R.id.one_week_button);
        graph = (BarChart) view.findViewById((R.id.patient_spirometer_graph));
        dataListView = (ListView) view.findViewById(R.id.patient_spirometer_table);


        oneDayButton.setOnClickListener(this);
        twoDayButton.setOnClickListener(this);
        threeDayButton.setOnClickListener(this);
        weekButton.setOnClickListener(this);

        databaseHelper = new DatabaseHelper(getContext());

        allSpData = new ArrayList<>();
        oneDaySpData = new ArrayList<>();
        twoDaySpData = new ArrayList<>();
        threeDaySpData = new ArrayList<>();
        oneWeekSpData = new ArrayList<>();

        barEntryList = new ArrayList<>();

        timeShown = SpirometerFragment.TimeShown.ONEDAY;

        createDataLists();
        drawGraph();

        return view;
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: BUTTON CLICK");
        switch (view.getId()) {
            case R.id.one_day_button:
                Log.d(TAG, "onClick: one day button clicked");
                timeShown = SpirometerFragment.TimeShown.ONEDAY;
                barEntryList = oneDaySpData;
                break;
            case R.id.two_day_button:
                Log.d(TAG, "onClick: two day button clicked");
                timeShown = SpirometerFragment.TimeShown.TWODAYS;
                barEntryList = twoDaySpData;
                break;
            case R.id.three_day_button:
                Log.d(TAG, "onClick: three day button clicked");
                timeShown = SpirometerFragment.TimeShown.THREEDAYS;
                barEntryList = threeDaySpData;
                break;
            case R.id.one_week_button:
                Log.d(TAG, "onClick: week button clicked");
                timeShown = SpirometerFragment.TimeShown.ONEWEEK;
                barEntryList = oneWeekSpData;
                break;
        }
        drawGraph();
    }

    /*
    gets the data for display from the database, sorts it into the different lists for display
     */
    private void createDataLists() {
        Log.d(TAG, "createDataList: Patient ID before call: " + patientId);
        allSpData = databaseHelper.getPatinetSpirometerData(patientId);
        Collections.sort(allSpData, Collections.<IncentiveSpirometerData>reverseOrder());

        // date for use with test data only - will need to be updated to reflect the CURRENT DATE when in real use
        Calendar now = new GregorianCalendar(2019, 10, 11, 7, 0, 0);
        for (IncentiveSpirometerData sp : allSpData) {
            Calendar cs = GregorianCalendar.getInstance();
            cs.setTime(sp.getStartTime());

            int timeDiff = (int) (TimeUnit.MILLISECONDS.toHours(now.getTimeInMillis() - cs.getTimeInMillis()));
            Log.d(TAG, "createDataLists: TIME DIFF: " + timeDiff);
            //Log.d(TAG, "createDataLists: now: " + now.toString());
            //Log.d(TAG, "createDataLists: time: " + cs.toString());
            int offset = (timeDiff / 24) * 14;
            if (timeDiff <= 24) {
                //Log.d(TAG, "createDataLists: ADDED one day data");
                oneDaySpData.add(new BarEntry(cs.get(Calendar.HOUR_OF_DAY), sp.getInhalationsCompleted()));
                twoDaySpData.add(new BarEntry(cs.get(Calendar.HOUR_OF_DAY), sp.getInhalationsCompleted()));
                threeDaySpData.add(new BarEntry(cs.get(Calendar.HOUR_OF_DAY), sp.getInhalationsCompleted()));
                oneWeekSpData.add(new BarEntry(cs.get(Calendar.HOUR_OF_DAY), sp.getInhalationsCompleted()));
            } else if (timeDiff <= 48) {
                //Log.d(TAG, "createDataLists: ADDED TWO day data");
                twoDaySpData.add(new BarEntry(cs.get(Calendar.HOUR_OF_DAY) + offset, sp.getInhalationsCompleted()));
                threeDaySpData.add(new BarEntry(cs.get(Calendar.HOUR_OF_DAY) + offset, sp.getInhalationsCompleted()));
                oneWeekSpData.add(new BarEntry(cs.get(Calendar.HOUR_OF_DAY) + offset, sp.getInhalationsCompleted()));
            } else if (timeDiff <= 72) {
                //Log.d(TAG, "createDataLists: ADDED thREE day data");
                threeDaySpData.add(new BarEntry(cs.get(Calendar.HOUR_OF_DAY) + offset, sp.getInhalationsCompleted()));
                oneWeekSpData.add(new BarEntry(cs.get(Calendar.HOUR_OF_DAY) + offset, sp.getInhalationsCompleted()));
            } else if (timeDiff <= 168) {
                //Log.d(TAG, "createDataLists: ADDED week data");
                oneWeekSpData.add(new BarEntry(cs.get(Calendar.HOUR_OF_DAY) + offset, sp.getInhalationsCompleted()));
            }
        }
        barEntryList = oneDaySpData;

        ArrayAdapter<IncentiveSpirometerData> arrayAdapter = new ArrayAdapter<IncentiveSpirometerData>(getContext(),
                R.layout.spirometer_info_list_row, R.id.row_date, allSpData) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView date = (TextView) view.findViewById(R.id.row_date);
                TextView time = (TextView) view.findViewById(R.id.row_time);
                TextView breaths = (TextView) view.findViewById(R.id.row_breaths_completed);

                date.setText(allSpData.get(position).getDate(allSpData.get(position).getStartTime()));
                time.setText(allSpData.get(position).getTime(allSpData.get(position).getStartTime()));
                breaths.setText(String.valueOf(allSpData.get(position).getInhalationsCompleted()));
                return view;
            }
        };
        //arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allSpData);
        dataListView.setAdapter(arrayAdapter);
    }


    private void drawGraph() {
        graph.getDescription().setEnabled(false);
        graph.getLegend().setEnabled(false);


        BarDataSet set = new BarDataSet(barEntryList, "BarDataSet");
        BarData data = new BarData(set);

        // will individually label bars in the graph if removed - good for testing bar overlap
        set.setDrawValues(false);

        XAxis x = graph.getXAxis();
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setLabelRotationAngle(-90);
        x.setDrawGridLines(false);
        x.setDrawAxisLine(true);
        // x.setDrawLabels(false);

        x.setValueFormatter(new ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                return super.getBarLabel(barEntry);
            }
        });


        YAxis yleft = graph.getAxisLeft();
        graph.getAxisRight().setEnabled(false);

        yleft.setAxisMinimum(0);
        yleft.setAxisMaximum(12);

        graph.setData(data);
        graph.invalidate();
    }

    enum TimeShown {
        ONEDAY, TWODAYS, THREEDAYS, ONEWEEK;
    }
}
