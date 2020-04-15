/**
 * Coded by: Kelsey Lally
 * Description: creates a graph of patient data as pulled from the database. Also shows a scrollable
 * table of the same data
 */
package com.example.incentive_spirometer_and_dvt_application.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.incentive_spirometer_and_dvt_application.R;
import com.example.incentive_spirometer_and_dvt_application.helpers.CSVReader;
import com.example.incentive_spirometer_and_dvt_application.helpers.DatabaseHelper;
import com.example.incentive_spirometer_and_dvt_application.models.IncentiveSpirometer;
import com.example.incentive_spirometer_and_dvt_application.models.IncentiveSpirometerData;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class SpirometerFragment extends Fragment{
    static final String TAG = "PatientSpiroInfoFrag";
    private DatabaseHelper databaseHelper;

    private List<IncentiveSpirometerData> allSpData;
    private List<BarEntry> allBarEntries;

    private Spinner dataWindowSpinner;

    private List<BarEntry> shownEntries;
    private BarChart graph;
    private TextView noSpirometerTextView;
    private GridLayout columnTitlesGridLayout;
    private LinearLayout spirometerInfoSpinnerArea;

    private ListView dataListView;
    private int numOfDaysInt;

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

        CSVReader csvReader = new CSVReader();
        File file = new File(getContext().getFilesDir(), "testcsv2.csv");
        csvReader.readInSpirometerData(file, getContext());

        createDataLists();
        drawGraph();

        checkForNoDevice();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.activity_patient_spirometer_info, container, false);

        dataWindowSpinner = (Spinner) view.findViewById(R.id.dataWindowSpinner);
        ArrayList<String> numOfDays = new ArrayList<>();
        numOfDays.add("1");
        numOfDays.add("2");
        numOfDays.add("3");
        numOfDays.add("4");
        numOfDays.add("5");
        numOfDays.add("6");
        numOfDays.add("7");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_dropdown_item, numOfDays);//this.getContext(), android.R.layout.simple_spinner_dropdown_item, );
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataWindowSpinner.setAdapter(arrayAdapter);
        dataWindowSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String numOfDays = parent.getItemAtPosition(position).toString();
                numOfDaysInt = Integer.parseInt(numOfDays);
                setDataWindow(24 * numOfDaysInt);
                drawGraph();
            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) {
            }
        });

        graph = (BarChart) view.findViewById((R.id.patient_spirometer_graph));
        dataListView = (ListView) view.findViewById(R.id.patient_spirometer_table);

        databaseHelper = new DatabaseHelper(getContext());

        shownEntries = new ArrayList<>();

        noSpirometerTextView = view.findViewById(R.id.noSpirometerTextView);
        columnTitlesGridLayout = view.findViewById(R.id.column_titles);
        spirometerInfoSpinnerArea = view.findViewById(R.id.spirometerInfoSpinnerArea);

        createDataLists();
        drawGraph();

        return view;
    }

    private void checkForNoDevice() {
        IncentiveSpirometer spirometer = databaseHelper.getIncentiveSpirometer(patientId);

        if (spirometer == null) {
            noSpirometerTextView.setVisibility(View.VISIBLE);
            dataListView.setVisibility(View.GONE);
            columnTitlesGridLayout.setVisibility(View.GONE);
            graph.setVisibility(View.GONE);
            spirometerInfoSpinnerArea.setVisibility(View.GONE);
        } else {
            noSpirometerTextView.setVisibility(View.GONE);
            dataListView.setVisibility(View.VISIBLE);
            columnTitlesGridLayout.setVisibility(View.VISIBLE);
            graph.setVisibility(View.VISIBLE);
            spirometerInfoSpinnerArea.setVisibility(View.VISIBLE);
        }
    }

    /*
    gets the data for display from the database, sorts it into the different lists for display
     */
    private void createDataLists() {
        allSpData = new ArrayList<>();
        allBarEntries = new ArrayList<>();

        allSpData = databaseHelper.getPatinetSpirometerData(patientId);

        Collections.sort(allSpData);

        for (int session = 1; session <= allSpData.size(); session++) {
            IncentiveSpirometerData sp = allSpData.get(session - 1);
            allBarEntries.add(new BarEntry(session, new float[] {sp.getInhalationsCompleted(), sp.getNumberOfInhalations() - sp.getInhalationsCompleted()}));
        }

        setDataWindow(24);

        ArrayAdapter<IncentiveSpirometerData> arrayAdapter = new ArrayAdapter<IncentiveSpirometerData>(getContext(),
                R.layout.spirometer_info_list_row, R.id.row_session, allSpData) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Collections.sort(allSpData, Collections.<IncentiveSpirometerData>reverseOrder());
                View view = super.getView(position, convertView, parent);

                TextView session = (TextView) view.findViewById(R.id.row_session);
                TextView start = (TextView) view.findViewById(R.id.row_start);
                TextView end = (TextView) view.findViewById(R.id.row_end);
                TextView lung_volume = (TextView) view.findViewById(R.id.row_lung_volume);
                TextView breaths_completed_ratio = (TextView) view.findViewById(R.id.row_breaths_complete_ratio);

                String breath_ratio_string = allSpData.get(position).getInhalationsCompleted() + " / " + allSpData.get(position).getNumberOfInhalations();

                session.setText(String.format("%s",allSpData.size() - position));
                start.setText(allSpData.get(position).getStringTime("start"));
                end.setText(allSpData.get(position).getStringTime("end"));
                lung_volume.setText(String.format("%s", allSpData.get(position).getLungVolume()));
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
        Collections.sort(allSpData);//, Collections.<IncentiveSpirometerData>reverseOrder());

        for (int session = 1; session <= allSpData.size(); session++) {
            IncentiveSpirometerData sp = allSpData.get(session - 1);
            float inhalation_rate = (float) ((double)sp.getInhalationsCompleted()*3600.0/(double) (TimeUnit.MILLISECONDS.toSeconds(sp.getEndTime().getTime() - sp.getStartTime().getTime())));
            allBarEntries.add(new BarEntry(session, new float[] {inhalation_rate, sp.getNumberOfInhalations() - inhalation_rate}));
        }

        shownEntries.clear();
        Calendar now = new GregorianCalendar();
        for (int session = 1; session <= allSpData.size(); session++) {
            IncentiveSpirometerData sp = allSpData.get(session - 1);
            int timeDiff = (int) (TimeUnit.MILLISECONDS.toHours(now.getTimeInMillis() - sp.getStartTime().getTime()));
            //Log.d(TAG, "setDataWindow: The timediff is " + timeDiff);
            //Log.d(TAG, "setDataWindow: the hours to show is " + hoursToShow);
            if (timeDiff < hoursToShow) {
                shownEntries.add(allBarEntries.get(session - 1));
                //Log.d(TAG, "setDataWindow: SHOWTHIS");
            }
        }
        for (int session = shownEntries.size(); session < numOfDaysInt * 10; session++){
            shownEntries.add(new BarEntry(session, 0));
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
        //yleft.setAxisMaximum(12);

        IMarker marker = new CustomMarkerView(getContext(), R.layout.graph_labels, allSpData);
        graph.setMarker(marker);
        graph.setScaleEnabled(false);

        graph.setData(data);
        graph.invalidate();
    }
}
