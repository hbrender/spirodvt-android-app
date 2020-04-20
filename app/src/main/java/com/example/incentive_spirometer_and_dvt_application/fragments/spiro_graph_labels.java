package com.example.incentive_spirometer_and_dvt_application.fragments;

import android.content.Context;
import java.util.concurrent.TimeUnit;
//import android.icu.util.TimeUnit;
import android.util.Log;
import android.widget.TextView;

import com.example.incentive_spirometer_and_dvt_application.R;
import com.example.incentive_spirometer_and_dvt_application.models.IncentiveSpirometer;
import com.example.incentive_spirometer_and_dvt_application.models.IncentiveSpirometerData;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.incentive_spirometer_and_dvt_application.fragments.SpirometerFragment.TAG;

class spiro_graph_labels extends MarkerView {
    private TextView session;
    private TextView breathRate;
    //private TextView start;
    //private TextView end;
    private TextView date;
    private List<IncentiveSpirometerData> data;

    public spiro_graph_labels(Context context, int layoutResource, List<IncentiveSpirometerData> data) {
        super(context, layoutResource);
        // find your layout components
        session = (TextView) findViewById(R.id.session);
        breathRate = (TextView) findViewById(R.id.breaths);
        date = (TextView) findViewById(R.id.date);
        //start = (TextView) findViewById(R.id.start);
        //end = (TextView) findViewById(R.id.end);
        this.data = data;
    }
    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        Log.d(TAG, "refreshContent: PRINTING GRAPH LABEL");
        NumberFormat format2 = new DecimalFormat("#0");
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);

        IncentiveSpirometerData sp;
        try{
            sp = data.get(data.size() - (int) e.getX());
            String formatBreaths = "Breaths: " + sp.getInhalationsCompleted();
            String formatSession = "Session: " + format2.format(e.getX());
            String formatDate = "Date: " + dateFormat.format(sp.getStartTime());
            //String formatStart = "Start Time: " + dateFormat.format(sp.getStartTime());
            //String formatEnd = "End Time:  " + dateFormat.format(sp.getEndTime());

            session.setText(formatSession);
            breathRate.setText(formatBreaths);
            date.setText(formatDate);
            //start.setText(formatStart);
            //end.setText(formatEnd);

            // this will perform necessary layouting
            super.refreshContent(e, highlight);
        } catch (java.lang.ArrayIndexOutOfBoundsException error){
            Log.d(TAG, "refreshContent: out of bounds exception thrown for show label bar click" );
        }

    }

    private MPPointF mOffset;
    @Override
    public MPPointF getOffset() {
        if(mOffset == null) {
            //if ( > data.size() / 2)
            // center the marker horizontally and vertically
            mOffset = new MPPointF(-(getWidth() / 2) - 80, - getHeight());
        }
        return mOffset;
    }


}
