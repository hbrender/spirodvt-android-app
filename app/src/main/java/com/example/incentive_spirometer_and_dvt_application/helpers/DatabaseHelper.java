/**
 * Helper class for SQLite Database
 *
 * Source referenced: https://www.androidhive.info/2013/09/android-sqlite-database-with-multiple-tables/
 * @author Hanna Brender
 * @version v1.0 10/23/19
 */

package com.example.incentive_spirometer_and_dvt_application.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Timestamp;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String LOG = "DatabaseHelper";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "spirometerDvtApp";

    // Table names
    private static final String TABLE_INCENTIVE_SPIROMETER = "IncentiveSpirometer";
    private static final String TABLE_DVT = "DVT";
    private static final String TABLE_DOCTOR = "Doctor";
    private static final String TABLE_PATIENT = "Patient";
    private static final String TABLE_DOCTOR_PATIENT = "DoctorPatient";

    // Common column names
    private static final String ID = "id";
    private static final String TIMESTAMP = "timestamp";

    // Incentive Spirometer table column names;
    private static final String LUNG_VOLUME = "lungVolume";
    private static final String NUMBER_OF_INHALATIONS = "numberOfInhalations";

    // DVT table column names;
    private static final String RESISTANCE = "resistance";
    private static final String NUMBER_OF_REPS = "numberOfReps";

    // Doctor table column names
    private static final String USERNAME = "username";

    // Patient table column names
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String HEIGHT_FEET = "heightFeet";
    private static final String HEIGHT_INCHES = "heightInches";
    private static final String WEIGHT = "weight";
    private static final String AGE = "age";
    private static final String SEX = "sex";
    private static final String INCENTIVE_SPIROMETER_ID = "incentiveSpirometerId";
    private static final String DVT_ID = "dvtId";

    // DoctorPatient table column names
    private static final String DOCTOR_ID = "doctorId";
    private static final String PATIENT_ID = "patientId";

    // IncentiveSpirometer table create statement
    private static final String CREATE_TABLE_INCENTIVE_SPIROMETER = "CREATE TABLE " + TABLE_INCENTIVE_SPIROMETER + "("
            + ID + " INTEGER PRIMARY KEY,"
            + TIMESTAMP + " DATETIME,"
            + LUNG_VOLUME + " INTEGER,"
            + NUMBER_OF_INHALATIONS + " INTEGER)";

    // DVT table create statement
    private static final String CREATE_TABLE_DVT = "CREATE TABLE " + TABLE_DVT + "("
            + ID + " INTEGER PRIMARY KEY,"
            + TIMESTAMP + " DATETIME,"
            + RESISTANCE + " INTEGER,"
            + NUMBER_OF_REPS + " INTEGER)";

    // Doctor table create statement
    private static final String CREATE_TABLE_DOCTOR = "CREATE TABLE " + TABLE_DOCTOR + "("
            + ID + " INTEGER PRIMARY KEY,"
            + USERNAME + "TEXT)";

    // Patient table create statement
    private static final String CREATE_TABLE_PATIENT = "CREATE TABLE " + TABLE_PATIENT + "("
            + ID + " INTEGER PRIMARY KEY,"
            + FIRST_NAME + " TEXT,"
            + LAST_NAME + " TEXT,"
            + HEIGHT_FEET + "INTEGER,"
            + HEIGHT_INCHES + "INTEGER,"
            + WEIGHT + "INTEGER,"
            + AGE + "INTEGER,"
            + SEX + "TEXT,"
            + INCENTIVE_SPIROMETER_ID + "INTEGER,"
            + DVT_ID + "INTEGER,"
            + "FOREIGN KEY(" + INCENTIVE_SPIROMETER_ID + ") REFERENCES "
            + TABLE_INCENTIVE_SPIROMETER + "(" + ID + "))";

    private static final String CREATE_TABLE_DOCTOR_PATIENT = "CREATE TABLE " + TABLE_DOCTOR_PATIENT + "("
            + DOCTOR_ID + " INTEGER,"
            + PATIENT_ID + " INTEGER,"
            + "PRIMARY KEY(" + DOCTOR_ID + "," + PATIENT_ID + "),"
            + "FOREIGN KEY(" + DOCTOR_ID + ") REFERENCES " + TABLE_DOCTOR + "(" + ID + "),"
            + "FOREIGN KEY(" + PATIENT_ID + ") REFERENCES " + TABLE_PATIENT + "(" + ID + "))";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating tables
        db.execSQL(CREATE_TABLE_INCENTIVE_SPIROMETER);
        db.execSQL(CREATE_TABLE_DVT);
        db.execSQL(CREATE_TABLE_DOCTOR);
        db.execSQL(CREATE_TABLE_PATIENT);
        db.execSQL(CREATE_TABLE_DOCTOR_PATIENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INCENTIVE_SPIROMETER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DVT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCTOR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PATIENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCTOR_PATIENT);

        // create new tables
        onCreate(db);
    }
}
