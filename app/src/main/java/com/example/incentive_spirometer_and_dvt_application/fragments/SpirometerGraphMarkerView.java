package com.example.incentive_spirometer_and_dvt_application.fragments;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.example.incentive_spirometer_and_dvt_application.R;
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

import static com.example.incentive_spirometer_and_dvt_application.fragments.SpirometerFragment.TAG;

class SpirometerGraphMarkerView extends MarkerView {
    private TextView session;
    private TextView breathRate;
    private TextView date;
    private List<IncentiveSpirometerData> data;

    public SpirometerGraphMarkerView(Context context, int layoutResource, List<IncentiveSpirometerData> data) {
        super(context, layoutResource);
        session = (TextView) findViewById(R.id.session);
        breathRate = (TextView) findViewById(R.id.breaths);
        date = (TextView) findViewById(R.id.date);
        this.data = data;
    }
    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        NumberFormat format2 = new DecimalFormat("#0");
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);

        IncentiveSpirometerData sp;
        try{
            sp = data.get(data.size() - (int) e.getX());
            String formatBreaths = "Breaths: " + sp.getInhalationsCompleted() + " / " + sp.getNumberOfInhalations();
            String formatSession = "Session: " + format2.format(e.getX());
            String formatDate = "Date: " + dateFormat.format(sp.getStartTime());

            session.setText(formatSession);
            breathRate.setText(formatBreaths);
            date.setText(formatDate);

            super.refreshContent(e, highlight);
        } catch (java.lang.ArrayIndexOutOfBoundsException error){
            Log.d(TAG, "refreshContent: out of bounds exception thrown for show label bar click" );
        }

    }

    //offsets the graph label so that it is not cut off on the screen
    private MPPointF mOffset;
    @Override
    public MPPointF getOffset() {
        if(mOffset == null) {

            mOffset = new MPPointF(-(getWidth() / 2) - 80, - getHeight());
        }
        return mOffset;
    }


}
