package com.example.incentive_spirometer_and_dvt_application.fragments;

import android.content.Context;
import android.widget.TextView;

import com.example.incentive_spirometer_and_dvt_application.R;
import com.example.incentive_spirometer_and_dvt_application.models.DvtData;
import com.example.incentive_spirometer_and_dvt_application.models.IncentiveSpirometerData;
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
import java.util.concurrent.TimeUnit;

public class dvt_graph_labels extends MarkerView{
    private TextView session;
    private TextView breathRate;
    private TextView start;
    private TextView end;
    private List<DvtData> data;

        public dvt_graph_labels(Context context, int layoutResource, List<DvtData> data) {
            super(context, layoutResource);
            // find your layout components
            session = (TextView) findViewById(R.id.session);
            breathRate = (TextView) findViewById(R.id.avgex);
            start = (TextView) findViewById(R.id.start);
            end = (TextView) findViewById(R.id.end);
            this.data = data;
        }
        // callbacks everytime the MarkerView is redrawn, can be used to update the
        // content (user-interface)
        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            NumberFormat format2 = new DecimalFormat("#0");
            DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.US);

            DvtData dvtd = data.get((int) e.getX() - 1);
            String formatExercises = "Exercises: " + dvtd.getRepsCompleted();
            String formatSession = "Session: " + format2.format(e.getX());
            String formatStart = "Start Time: " + dateFormat.format(data.get((int) e.getX() - 1).getStartTime());
            String formatEnd = "End Time:  " + dateFormat.format(data.get((int) e.getX() - 1).getEndTime());

            session.setText(formatSession);
            breathRate.setText(formatExercises);
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
                mOffset = new MPPointF(-(getWidth() / 2) - 90, -getHeight());
            }
            return mOffset;
        }


    }

