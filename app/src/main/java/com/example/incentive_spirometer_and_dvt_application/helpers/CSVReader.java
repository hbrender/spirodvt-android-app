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

public class CSVReader {

    public void readInSpirometerData (File spCsvFile, Context context) {
        try {
            BufferedReader csvReader = new BufferedReader(new FileReader(spCsvFile));
            Log.d("CSVREADER ERROR: ", "readInSpirometerData: " + spCsvFile);
            int deviceId = Integer.parseInt(csvReader.readLine().split(",")[0]);
            int completedReps = Integer.parseInt(csvReader.readLine().split(",")[0]);
            String[] startArray = csvReader.readLine().split(",");
            String[] endArray = csvReader.readLine().split(",");

            Date startDate = convertArrayToDate(startArray);
            Date endDate = convertArrayToDate(endArray);

            DatabaseHelper db = new DatabaseHelper(context);
            IncentiveSpirometer incentiveSpirometer = db.getIncentiveSpirometerBySpirometerId(deviceId);
            IncentiveSpirometerData isd = new IncentiveSpirometerData(deviceId, startDate, endDate, incentiveSpirometer.getLungVolume(), incentiveSpirometer.getNumberOfInhalations(), completedReps);

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
            int deviceId = Integer.parseInt(csvReader.readLine().split(",")[0]);
            int completedReps = Integer.parseInt(csvReader.readLine().split(",")[0]);
            String[] startArray = csvReader.readLine().split(",");
            String[] endArray = csvReader.readLine().split(",");

            Date startDate = convertArrayToDate(startArray);
            Date endDate = convertArrayToDate(endArray);

            DatabaseHelper db = new DatabaseHelper(context);
            Dvt dvt = db.getDvtByDvtId(deviceId);
            DvtData dvtd = new DvtData(deviceId, startDate, endDate, dvt.getResistance(), dvt.getNumberOfReps(), completedReps);

            db.insertDvtData(dvtd);

        } catch (java.io.FileNotFoundException e) {
            System.out.println("File not found: " + e.getStackTrace().toString());
        } catch (java.io.IOException f) {
            System.out.println("error in input file: " + f.getStackTrace().toString());
        }
    }

    public Date convertArrayToDate(String[] array) {
        String stringDate = array[0] + "/" + array[1] + "/" + array[2] + " " + String.format("%02d", array[3]) + ":" + String.format("%02d", array[4]);
        Date date = new Date();
        try {
            date = new SimpleDateFormat("dd/MM/yyy HH:mm").parse(stringDate);
        } catch (java.text.ParseException e) {
            System.out.println("error parsing date: " + e.getStackTrace().toString());
        }
        return date;
    }
}
