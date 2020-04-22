package com.example.incentive_spirometer_and_dvt_application.fragments;

/**
 * code that provides details for the pop up labels that appear when bars on the dvt
 * graph are clicked
 * code originally sourced from: https://github.com/PhilJay/MPAndroidChart
 * modifications made
 *
 * v1.0 4/22/20
 */

import android.content.Context;
import android.widget.TextView;

import com.example.incentive_spirometer_and_dvt_application.R;
import com.example.incentive_spirometer_and_dvt_application.models.DvtData;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class DvtGraphMarkerView extends MarkerView{
    private TextView session;
    private TextView breathRate;
    private TextView date;
    private List<DvtData> data;

        public DvtGraphMarkerView(Context context, int layoutResource, List<DvtData> data) {
            super(context, layoutResource);
            // find layout components
            session = (TextView) findViewById(R.id.session);
            breathRate = (TextView) findViewById(R.id.ex);
            date = (TextView) findViewById(R.id.date);
            this.data = data;
        }
        // callbacks everytime the MarkerView is redrawn, can be used to update the
        // content (user-interface)
        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            NumberFormat format2 = new DecimalFormat("#0");
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);

            DvtData dvtd = data.get((int) e.getX() - 1);
            String formatExercises = "Exercises: " + dvtd.getRepsCompleted();
            String formatSession = "Session: " + format2.format(e.getX());
            String formatDate = "Date: " + dateFormat.format(data.get((int) e.getX() - 1).getStartTime());

            session.setText(formatSession);
            breathRate.setText(formatExercises);
            date.setText(formatDate);

            // this will perform necessary layouting
            super.refreshContent(e, highlight);
        }
        private MPPointF mOffset;
        @Override
        public MPPointF getOffset() {
            if(mOffset == null) {
                // center the marker horizontally and vertically
                mOffset = new MPPointF(-(getWidth() / 2) - 80, -getHeight());
            }
            return mOffset;
        }
    }

