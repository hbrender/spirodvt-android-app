package com.example.incentive_spirometer_and_dvt_application.helpers;

import com.example.incentive_spirometer_and_dvt_application.models.Dvt;
import com.example.incentive_spirometer_and_dvt_application.models.DvtData;
import com.example.incentive_spirometer_and_dvt_application.models.IncentiveSpirometer;
import com.example.incentive_spirometer_and_dvt_application.models.IncentiveSpirometerData;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * file that reads data from a csv and can save the data directly to our local database
 * 
 * v1.0: 04/20/20
 */

public class CSVReader {

    public void readInSpirometerData (File spCsvFile, Context context) {
        try {
            BufferedReader csvReader = new BufferedReader(new FileReader(spCsvFile));
            
            String deviceUuid = csvReader.readLine().split(",")[0];
            int completedReps = Integer.parseInt(csvReader.readLine().split(",")[0]);
            String[] startArray = csvReader.readLine().split(",");
            String[] endArray = csvReader.readLine().split(",");

            Date startDate = convertArrayToDate(startArray);
            Date endDate = convertArrayToDate(endArray);

            DatabaseHelper db = new DatabaseHelper(context);
            IncentiveSpirometer incentiveSpirometer = db.getIncentiveSpirometerByUuid(deviceUuid);
            IncentiveSpirometerData isd = new IncentiveSpirometerData(incentiveSpirometer.getId(), startDate, endDate, incentiveSpirometer.getLungVolume(), incentiveSpirometer.getNumberOfInhalations(), completedReps);

            db.insertIncentiveSpirometerData(isd);

        } catch (java.io.FileNotFoundException e) {
            System.out.println("File not found: " + spCsvFile.getAbsolutePath());
        } catch (java.io.IOException f) {
            System.out.println("error in input file: " + f.getStackTrace().toString());
        }
    }

    public void readInDvtData (File dvtCsvFile, Context context) {
        try {
            BufferedReader csvReader = new BufferedReader(new FileReader(dvtCsvFile));
            
            String deviceUuid = csvReader.readLine().split(",")[0];
            int completedReps = Integer.parseInt(csvReader.readLine().split(",")[0]);
            String[] startArray = csvReader.readLine().split(",");
            String[] endArray = csvReader.readLine().split(",");

            Date startDate = convertArrayToDate(startArray);
            Date endDate = convertArrayToDate(endArray);

            DatabaseHelper db = new DatabaseHelper(context);
            Dvt dvt = db.getDvtByUuid(deviceUuid);
            DvtData dvtd = new DvtData(dvt.getId(), startDate, endDate, dvt.getResistance(), dvt.getNumberOfReps(), completedReps);

            db.insertDvtData(dvtd);

        } catch (java.io.FileNotFoundException e) {
            Log.e(TAG, "readInDvtData: File not found: " + e.getStackTrace().toString());
        } catch (java.io.IOException f) {
            Log.e(TAG, "readInDvtData: error in input file: " + f.getStackTrace().toString());
        }
    }

    /**
     *  convert a compatible array to a Date object (this array is from the csv read in)
     */
    public Date convertArrayToDate(String[] array) {

        String stringDate = addZerosToString(array[0]) + "/" + addZerosToString(array[1]) + "/" + addZerosToString(array[2]) + " " + addZerosToString(array[3]) + ":" + addZerosToString(array[4]);
        Date date = new Date();
        try {
            date = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(stringDate);
        } catch (java.text.ParseException e) {
            Log.e(TAG, "convertArrayToDate: error parsing date: " + e.getStackTrace().toString());
        }
        return date;
    }

    public String addZerosToString (String string) {
        int solution = Integer.parseInt(string);
        return String.format("%02d", solution);
    }
}
