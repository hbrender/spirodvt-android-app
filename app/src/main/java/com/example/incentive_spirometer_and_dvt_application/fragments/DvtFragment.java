package com.example.incentive_spirometer_and_dvt_application.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.incentive_spirometer_and_dvt_application.R;
import com.example.incentive_spirometer_and_dvt_application.helpers.BluetoothThread;
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
import com.google.android.material.snackbar.Snackbar;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


public class DvtFragment extends Fragment{
    static final String TAG = "PatientDvtInfoFragment";
    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_CONNECTED_THREAD_SUCCESS = 5;
    static final int STATE_CONNECTED_THREAD_FAILED = 6;
    static final int STATE_MESSAGE_RECEIVED = 7;

    private static BluetoothThread bluetoothThread;
    private BluetoothAdapter bluetoothAdapter;
    private DatabaseHelper databaseHelper;

    private List<DvtData> allDvtData;
    private List<BarEntry> allBarEntries;

    private Button getDvtSession;
    private Spinner dataWindowSpinner;

    private int numOfDaysInt;

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
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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

        final View view = inflater.inflate(R.layout.activity_patient_dvt_info, container, false);

        getDvtSession = view.findViewById(R.id.getDvtDataButton);
        getDvtSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothDevice[] bondedDevices = getBondedDevices();
                if(bondedDevices == null) {
                    Toast.makeText(getActivity(), "Error: your phone has no paired devices", Toast.LENGTH_LONG).show();
                }
                else {
                    String patientDvtUuid = databaseHelper.getDeviceUuid(patientId, false);
                    if(patientDvtUuid == null){
                        Toast.makeText(getActivity(), "Error: patient has no DVT saved", Toast.LENGTH_LONG).show();
                    }
                    else {
                        BluetoothDevice device = checkDeviceExists(bondedDevices, patientDvtUuid);
                        if(device != null){
                            Log.d(TAG, "onClick: device exists");
                            bluetoothThread = new BluetoothThread(handler);
                            boolean[] spiroOrDvt = {false, true};
                            bluetoothThread.startConnectThread(device, spiroOrDvt);
                        }
                        else {
                            Toast.makeText(getActivity(), "Error: the device is not recognized in the database", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
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

        graph = (BarChart) view.findViewById((R.id.patient_dvt_graph));
        dataListView = (ListView) view.findViewById(R.id.patient_dvt_table);

        databaseHelper = new DatabaseHelper(getContext());

        shownEntries = new ArrayList<>();

        createDataLists();
        drawGraph();

        return view;
    }

    private BluetoothDevice[] getBondedDevices(){
        Set<BluetoothDevice> bt = bluetoothAdapter.getBondedDevices();
        BluetoothDevice[] bondedDevices = new BluetoothDevice[bt.size()];
        int index = 0;
        if(bt.size() > 0){
            for(BluetoothDevice device : bt){
                bondedDevices[index] = device;
                index++;
            }
        }
        else{
            return null;
        }
        return bondedDevices;
    }

    private BluetoothDevice checkDeviceExists(BluetoothDevice[] bondedDevices, String deviceUuid) {
        for(BluetoothDevice device : bondedDevices){
            ParcelUuid[] uuids = device.getUuids();
            String checkAgainstUuid = uuids[0].toString().replaceAll("[-]", "");
            Log.d(TAG, "checkDeviceExists: " + checkAgainstUuid);
            if(deviceUuid.equals(checkAgainstUuid)){
                return device;
            }
        }
        return null;
    }

    /*
    gets the data for display from the database, sorts it into the different lists for display
     */
    private void createDataLists() {
        allDvtData = new ArrayList<>();
        allBarEntries = new ArrayList<>();

        allDvtData = databaseHelper.getPatinetDvtData(patientId);

        Collections.sort(allDvtData);

        // date for use with test data only - will need to be updated to reflect the CURRENT DATE when in real use
        Calendar now = new GregorianCalendar(2019, Calendar.NOVEMBER, 11, 7, 0, 0);

        for (int session = 1; session <= allDvtData.size(); session++) {
            DvtData dvtd = allDvtData.get(session - 1);
            allBarEntries.add(new BarEntry(session, new float[] {dvtd.getRepsCompleted(), dvtd.getNumberOfReps() - dvtd.getRepsCompleted()}));
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
        Calendar now = new GregorianCalendar();
        for (int session = 1; session <= allDvtData.size(); session++) {
            DvtData dvtd = allDvtData.get(session - 1);
            int timeDiff = (int) (TimeUnit.MILLISECONDS.toHours(now.getTimeInMillis() - dvtd.getStartTime().getTime()));
            if (timeDiff < hoursToShow) {
                shownEntries.add(allBarEntries.get(session - 1));
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
        yleft.setAxisMaximum(12);

        IMarker marker = new dvt_graph_labels(getContext(), R.layout.dvt_graph_labels, allDvtData);
        graph.setMarker(marker);
        graph.setScaleEnabled(false);

        graph.setData(data);
        graph.invalidate();
    }

    // creating the handler for dealing with callbacks from the bluetooth threads used
    Handler handler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch(msg.what){
                case STATE_LISTENING:
                    Log.d(TAG, "handleMessage: listening");
                    break;
                case STATE_CONNECTING:
                    Log.d(TAG, "handleMessage: connecting");
                    break;
                case STATE_CONNECTED:
                    Log.d(TAG, "handleMessage: connected");
                    break;
                case STATE_CONNECTION_FAILED:
                    Log.d(TAG, "handleMessage: connection failed :(");
                    Toast.makeText(getActivity(), "Error: connection to device failed", Toast.LENGTH_LONG).show();
                    break;
                case STATE_CONNECTED_THREAD_SUCCESS:
                    // connection was created and we send a prompt with what we want back
                    // either a spriometer ID or DVT ID
                    byte[] bytes;

                    bytes = "data\r\n".getBytes(Charset.defaultCharset());

                    bluetoothThread.sendMessage(bytes);
                    Log.d(TAG, "handleMessage: success in sending message");
                    break;
                case STATE_CONNECTED_THREAD_FAILED:
                    Log.d(TAG, "handleMessage: connected thread failed...closing it promptly");
                    bluetoothThread.endConnectThread();
                    bluetoothThread.endConnectedThread();
                    Toast.makeText(getActivity(), "Error: could not get data from device", Toast.LENGTH_LONG).show();
                    break;
                case STATE_MESSAGE_RECEIVED:
                    // this handles the response from the hardware when we asked to give us a device ID
                    byte[] readBuff = (byte[]) msg.obj;
                    String[] tempdata = new String(readBuff, 0, msg.arg1).split("\\n");

                    try{
                        String data = tempdata[0].trim();
                        bluetoothThread.endConnectThread();
                        bluetoothThread.endConnectedThread();
                        Log.d(TAG, "handleMessage: " + data);
                    }
                    catch(NumberFormatException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            return true;
        }
    });

    /**
     * starts the connected thread with the device we want...this will enable the hardware to send us back data
     * @param socket a bluetooth socket with the connetion to the device we want
     */
    public static void manageConnectedSocket(BluetoothSocket socket){
        bluetoothThread.startConnectedThread(socket);
        Log.d(TAG, "manageConnectedSocket: managing open connect socket");
    }
}


