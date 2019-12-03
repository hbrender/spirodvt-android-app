package com.example.incentive_spirometer_and_dvt_application.fragments;

import android.content.Context;
import android.widget.TextView;

import com.example.incentive_spirometer_and_dvt_application.R;
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

class CustomMarkerView extends MarkerView {
    private TextView session;
    private TextView breathRate;
    private TextView start;
    private TextView end;
    private List<IncentiveSpirometerData> data;

    public CustomMarkerView(Context context, int layoutResource, List<IncentiveSpirometerData> data) {
        super(context, layoutResource);
        // find your layout components
        session = (TextView) findViewById(R.id.session);
        breathRate = (TextView) findViewById(R.id.avgbreaths);
        start = (TextView) findViewById(R.id.start);
        end = (TextView) findViewById(R.id.end);
        this.data = data;
    }
    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        NumberFormat formatter = new DecimalFormat("#0.00");
        NumberFormat format2 = new DecimalFormat("#0");
        DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.US);

        String formatBreaths = "Avg. breaths/hour: " + formatter.format(e.getY());
        String formatSession = "Session: " + format2.format(e.getX());
        String formatStart = "Start Time: " + dateFormat.format(data.get((int) e.getX() - 1).getStartTime());
        String formatEnd = "End Time:  " + dateFormat.format(data.get((int) e.getX() - 1).getEndTime());

        session.setText(formatSession);
        breathRate.setText(formatBreaths);
        start.setText(formatStart);
        end.setText(formatEnd);

        // this will perform necessary layouting
        super.refreshContent(e, highlight);
    }
    private MPPointF mOffset;
    @Override
    public MPPointF getOffset() {
        if(mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
        }
        return mOffset;
    }


}