package com.example.incentive_spirometer_and_dvt_application.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.incentive_spirometer_and_dvt_application.R;
import com.example.incentive_spirometer_and_dvt_application.helpers.DatabaseHelper;
import com.example.incentive_spirometer_and_dvt_application.models.DvtData;
import com.example.incentive_spirometer_and_dvt_application.models.IncentiveSpirometerData;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.IMarker;
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


public class DvtFragment extends Fragment implements View.OnClickListener {
    static final String TAG = "PatientDvtInfoFragment";
    private DatabaseHelper databaseHelper;

    private List<DvtData> allDvtData;
    private List<BarEntry> allBarEntries;

    private Button oneDayButton;
    private Button twoDayButton;
    private Button threeDayButton;

    private BarChart graph;
    private List<BarEntry> shownEntries;

    private ListView dataListView;

    private int patientId;
    private int doctorId;

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
    public void onStart() {
        Log.d(TAG, "onStart: ONSTART");
        super.onStart();
        createDataLists();
        drawGraph();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ONERESUME");
        super.onResume();
        createDataLists();
        drawGraph();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.activity_patient_dvt_info, container, false);

        oneDayButton = (Button) view.findViewById(R.id.one_day_button);
        twoDayButton = (Button) view.findViewById(R.id.two_day_button);
        threeDayButton = (Button) view.findViewById(R.id.three_day_button);
        graph = (BarChart) view.findViewById((R.id.patient_dvt_graph));
        dataListView = (ListView) view.findViewById(R.id.patient_dvt_table);

        oneDayButton.setOnClickListener(this);
        twoDayButton.setOnClickListener(this);
        threeDayButton.setOnClickListener(this);

        databaseHelper = new DatabaseHelper(getContext());

        shownEntries = new ArrayList<>();

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
                setDataWindow(24);
                oneDayButton.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.colorAccent));
                twoDayButton.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.colorPrimaryLight));
                threeDayButton.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.colorPrimaryLight));
                break;
            case R.id.two_day_button:
                Log.d(TAG, "onClick: two day button clicked");
                setDataWindow(48);
                oneDayButton.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.colorPrimaryLight));
                twoDayButton.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.colorAccent));
                threeDayButton.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.colorPrimaryLight));
                break;
            case R.id.three_day_button:
                Log.d(TAG, "onClick: three day button clicked");
                setDataWindow(72);
                oneDayButton.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.colorPrimaryLight));
                twoDayButton.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.colorPrimaryLight));
                threeDayButton.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.colorAccent));
                break;
        }
        drawGraph();
    }

    /*
    gets the data for display from the database, sorts it into the different lists for display
     */
    private void createDataLists() {
        allDvtData = new ArrayList<>();
        allBarEntries = new ArrayList<>();

        allDvtData = databaseHelper.getPatinetDvtData(patientId);

        for (DvtData dvtd: allDvtData) {
            //Log.d(TAG, "createDataLists: DVT data entry:" + dvtd);;
        }

        Collections.sort(allDvtData);

        // date for use with test data only - will need to be updated to reflect the CURRENT DATE when in real use
        Calendar now = new GregorianCalendar(2019, Calendar.NOVEMBER, 11, 7, 0, 0);

        for (int session = 1; session <= allDvtData.size(); session++) {
            DvtData dvtd = allDvtData.get(session - 1);
            float ex_rate = (float) ((double)dvtd.getRepsCompleted()*3600.0/(double) (TimeUnit.MILLISECONDS.toSeconds(dvtd.getEndTime().getTime() - dvtd.getStartTime().getTime())));
            allBarEntries.add(new BarEntry(session, new float[] {ex_rate, dvtd.getNumberOfReps() - dvtd.getRepsCompleted()}));
        }

        setDataWindow(24);

        ArrayAdapter<DvtData> arrayAdapter = new ArrayAdapter<DvtData>(getContext(),
                R.layout.dvt_info_list_row, R.id.row_session, allDvtData) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Collections.sort(allDvtData, Collections.<DvtData>reverseOrder());

                View view = super.getView(position, convertView, parent);

                TextView session = (TextView) view.findViewById(R.id.row_session);
                TextView start = (TextView) view.findViewById(R.id.row_start);
                TextView end = (TextView) view.findViewById(R.id.row_end);
                TextView resistance = (TextView) view.findViewById(R.id.resistance_dvt_table_row);
                TextView breaths_completed_ratio = (TextView) view.findViewById(R.id.row_ex_complete_ratio);

                String breath_ratio_string = allDvtData.get(position).getRepsCompleted() + " / " + allDvtData.get(position).getNumberOfReps();

                session.setText(String.format(String.format("%s",allDvtData.size() - position)));
                start.setText(allDvtData.get(position).getStringTime("start"));
                end.setText(allDvtData.get(position).getStringTime("end"));
                resistance.setText(allDvtData.get(position).getResistance());

                breaths_completed_ratio.setText(breath_ratio_string);

                return view;
            }
        };
        dataListView.setAdapter(arrayAdapter);
    }


    // sets the amount of data that will be shown on the graph
    // hoursToShow: the number of past hours the user would like to see, for example, if the user
    // would like to see data from the past day this number should be 24
    private void setDataWindow(int hoursToShow) {
        Collections.sort(allDvtData);

        for (int session = 1; session <= allDvtData.size(); session++) {
            DvtData dvtd = allDvtData.get(session - 1);
            float ex_rate = (float) ((double)dvtd.getRepsCompleted()*3600.0/(double) (TimeUnit.MILLISECONDS.toSeconds(dvtd.getEndTime().getTime() - dvtd.getStartTime().getTime())));
            allBarEntries.add(new BarEntry(session, new float[] {ex_rate, dvtd.getNumberOfReps() - dvtd.getRepsCompleted()}));
        }

        shownEntries.clear();
        // TODO change to current time once we are making current testing data
        Calendar now = new GregorianCalendar(2019, Calendar.NOVEMBER, 11, 7, 0, 0);
        for (int session = 1; session <= allDvtData.size(); session++) {
            DvtData dvtd = allDvtData.get(session - 1);
            int timeDiff = (int) (TimeUnit.MILLISECONDS.toHours(now.getTimeInMillis() - dvtd.getStartTime().getTime()));
            if (timeDiff < hoursToShow) {
                shownEntries.add(allBarEntries.get(session - 1));
            }
        }
    }

    // draws the features of the graph, including removing the description and legend, setting
    // touch selection to enabled, setting the data set to the graph, and setting all labels
    // visible for the graph and bars on the graph
    private void drawGraph() {
        graph.getDescription().setEnabled(false);
        graph.getLegend().setEnabled(false);
        graph.setTouchEnabled(true);

        BarDataSet set = new BarDataSet(shownEntries, "BarDataSet");
        int completedColor = getResources().getColor(R.color.colorAccent);
        int uncompleteColor = getResources().getColor(R.color.colorEmptyBar);
        set.setColors(completedColor, uncompleteColor);
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

        IMarker marker = new dvt_graph_labels(getContext(), R.layout.dvt_graph_labels, allDvtData);
        graph.setMarker(marker);
        graph.setScaleEnabled(false);

        graph.setData(data);
        graph.invalidate();
    }
}
