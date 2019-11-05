/**
 * Helper class for SQLite Database
 *
 * Source referenced: https://www.androidhive.info/2013/09/android-sqlite-database-with-multiple-tables/
 * @author Hanna Brender
 */

package com.example.incentive_spirometer_and_dvt_application.helpers;

import com.example.incentive_spirometer_and_dvt_application.models.Doctor;
import com.example.incentive_spirometer_and_dvt_application.models.Patient;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "spirometerDvtApp";

    // Table names
    private static final String TABLE_INCENTIVE_SPIROMETER = "IncentiveSpirometer";
    private static final String TABLE_DVT = "DVT";
    private static final String TABLE_INCENTIVE_SPIROMETER_DATA = "IncentiveSpirometerData";
    private static final String TABLE_DVT_DATA = "DvtData";
    private static final String TABLE_DOCTOR = "Doctor";
    private static final String TABLE_PATIENT = "Patient";
    private static final String TABLE_DOCTOR_PATIENT = "DoctorPatient";
    private static final String TABLE_LOGIN = "Login";

    // Common column names
    private static final String ID = "id";
    private static final String TIMESTAMP = "timestamp";

    // Incentive Spirometer table column names;
    private static final String LUNG_VOLUME = "lungVolume";
    private static final String NUMBER_OF_INHALATIONS = "numberOfInhalations";

    // DvtData table column names;
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

    // Login table column names
    private static final String SALT = "salt";
    private static final String HASHED_PASSWORD = "hashedPassword";

    // IncentiveSpirometerData table create statement
    private static final String CREATE_TABLE_INCENTIVE_SPIROMETER = "CREATE TABLE " + TABLE_INCENTIVE_SPIROMETER + "("
            + ID + " INTEGER PRIMARY KEY)";

    // Dvt table create statement
    private static final String CREATE_TABLE_DVT = "CREATE TABLE " + TABLE_DVT + "("
            + ID + " INTEGER PRIMARY KEY)";

    // IncentiveSpirometerData table create statement
    private static final String CREATE_TABLE_INCENTIVE_SPIROMETER_DATA = "CREATE TABLE " + TABLE_INCENTIVE_SPIROMETER + "("
            + ID + " INTEGER,"
            + TIMESTAMP + " DATETIME,"
            + LUNG_VOLUME + " INTEGER,"
            + NUMBER_OF_INHALATIONS + " INTEGER,"
            + " PRIMARY KEY(" + ID + ", " + TIMESTAMP + "),"
            + " FOREIGN KEY(" + ID + ") REFERENCES " + TABLE_INCENTIVE_SPIROMETER + "(" + ID + "))";

    // DvtData table create statement
    private static final String CREATE_TABLE_DVT_DATA = "CREATE TABLE " + TABLE_DVT_DATA + "("
            + ID + " INTEGER,"
            + TIMESTAMP + " DATETIME,"
            + RESISTANCE + " INTEGER,"
            + NUMBER_OF_REPS + " INTEGER,"
            + " PRIMARY KEY(" + ID + ", " + TIMESTAMP + "),"
            + " FOREIGN KEY(" + ID + ") REFERENCES " + TABLE_DVT + "(" + ID + "))";

    // Doctor table create statement
    private static final String CREATE_TABLE_DOCTOR = "CREATE TABLE " + TABLE_DOCTOR + "("
            + ID + " INTEGER PRIMARY KEY,"
            + USERNAME + " TEXT)";

    // Patient table create statement
    private static final String CREATE_TABLE_PATIENT = "CREATE TABLE " + TABLE_PATIENT + "("
            + ID + " INTEGER PRIMARY KEY,"
            + FIRST_NAME + " TEXT,"
            + LAST_NAME + " TEXT,"
            + HEIGHT_FEET + " INTEGER,"
            + HEIGHT_INCHES + " DOUBLE,"
            + WEIGHT + " DOUBLE,"
            + AGE + " INTEGER,"
            + SEX + " TEXT,"
            + INCENTIVE_SPIROMETER_ID + " INTEGER,"
            + DVT_ID + " INTEGER)";
            //+ "FOREIGN KEY(" + INCENTIVE_SPIROMETER_ID + ") REFERENCES "
            //+ TABLE_INCENTIVE_SPIROMETER + "(" + ID + "),"
            //+ "FOREIGN KEY(" + DVT_ID + ") REFERENCES "
            //+ TABLE_DVT + "(" + ID + "))";

    private static final String CREATE_TABLE_DOCTOR_PATIENT = "CREATE TABLE " + TABLE_DOCTOR_PATIENT + "("
            + DOCTOR_ID + " INTEGER,"
            + PATIENT_ID + " INTEGER,"
            + "PRIMARY KEY(" + DOCTOR_ID + "," + PATIENT_ID + "),"
            + "FOREIGN KEY(" + DOCTOR_ID + ") REFERENCES " + TABLE_DOCTOR + "(" + ID + "),"
            + "FOREIGN KEY(" + PATIENT_ID + ") REFERENCES " + TABLE_PATIENT + "(" + ID + "))";

    private static final String CREATE_TABLE_LOGIN = "CREATE TABLE " + TABLE_LOGIN + "("
            + ID + " INTEGER,"
            + USERNAME + " TEXT,"
            + SALT + " INTEGER,"
            + HASHED_PASSWORD + " INTEGER,"
            + "FOREIGN KEY(" + USERNAME + ") REFERENCES " + TABLE_DOCTOR + "(" + USERNAME + "))";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating tables
        db.execSQL(CREATE_TABLE_INCENTIVE_SPIROMETER);
        db.execSQL(CREATE_TABLE_DVT);
        db.execSQL(CREATE_TABLE_INCENTIVE_SPIROMETER_DATA);
        db.execSQL(CREATE_TABLE_DVT_DATA);
        db.execSQL(CREATE_TABLE_DOCTOR);
        db.execSQL(CREATE_TABLE_PATIENT);
        db.execSQL(CREATE_TABLE_DOCTOR_PATIENT);
        db.execSQL(CREATE_TABLE_LOGIN);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INCENTIVE_SPIROMETER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DVT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INCENTIVE_SPIROMETER_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DVT_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCTOR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PATIENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCTOR_PATIENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);

        // create new tables
        onCreate(db);
    }

    public boolean insertPatient(Patient patient) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID, patient.getId());
        values.put(FIRST_NAME, patient.getFirstName());
        values.put(LAST_NAME, patient.getLastName());
        values.put(HEIGHT_FEET, patient.getHeightFeet());
        values.put(HEIGHT_INCHES, patient.getHeightInches());
        values.put(WEIGHT, patient.getWeight());
        values.put(AGE, patient.getAge());
        values.put(SEX, patient.getSex());
        values.put(INCENTIVE_SPIROMETER_ID, java.sql.Types.NULL);
        values.put(DVT_ID, Types.NULL);

        long result = db.insert(TABLE_PATIENT, null, values);

        return result != -1; // if result = -1 data doesn't insert
    }

    public boolean insertDoctorPatient(int patientId, int doctorId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PATIENT_ID, patientId);
        values.put(DOCTOR_ID, doctorId);

        long result = db.insert(TABLE_DOCTOR_PATIENT, null, values);

        return result != -1; // if result = -1 data doesn't insert
    }

    public List<Patient> getAllPatients() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PATIENT;
        Cursor c = db.rawQuery(query, null);

        List<Patient> patientList = new ArrayList<>();

        // create list of patients
        if (c.moveToFirst()) {
            do {
                Patient patient = new Patient();
                patient.setId(c.getInt(c.getColumnIndex(ID)));
                patient.setFirstName(c.getString(c.getColumnIndex(FIRST_NAME)));
                patient.setLastName(c.getString(c.getColumnIndex(LAST_NAME)));
                patient.setHeightFeet(c.getInt(c.getColumnIndex(HEIGHT_FEET)));
                patient.setHeightInches(c.getDouble(c.getColumnIndex(HEIGHT_INCHES)));
                patient.setWeight(c.getDouble(c.getColumnIndex(WEIGHT)));
                patient.setAge(c.getInt(c.getColumnIndex(AGE)));
                patient.setSex(c.getString(c.getColumnIndex(SEX)));
                patient.setIncentiveSpirometerId(c.getInt(c.getColumnIndex(INCENTIVE_SPIROMETER_ID)));
                patient.setDvtId(c.getInt(c.getColumnIndex(DVT_ID)));

                patientList.add(patient);
            } while (c.moveToNext());
        }

        return patientList;
    }

    public Patient getPatient(int patientId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PATIENT + " WHERE " + ID + " = " + patientId;
        Cursor c = db.rawQuery(query, null);

        Log.e(TAG, "getPatient: "+ query);

        if (c != null)
            c.moveToFirst();

        // create patient
        Patient patient = new Patient();
        patient.setId(c.getInt(c.getColumnIndex(ID)));
        patient.setFirstName(c.getString(c.getColumnIndex(FIRST_NAME)));
        patient.setLastName(c.getString(c.getColumnIndex(LAST_NAME)));
        patient.setHeightFeet(c.getInt(c.getColumnIndex(HEIGHT_FEET)));
        patient.setHeightInches(c.getDouble(c.getColumnIndex(HEIGHT_INCHES)));
        patient.setWeight(c.getDouble(c.getColumnIndex(WEIGHT)));
        patient.setAge(c.getInt(c.getColumnIndex(AGE)));
        patient.setSex(c.getString(c.getColumnIndex(SEX)));
        patient.setIncentiveSpirometerId(c.getInt(c.getColumnIndex(INCENTIVE_SPIROMETER_ID)));
        patient.setDvtId(c.getInt(c.getColumnIndex(DVT_ID)));

        return patient;
    }

    public void deletePatient(int patientId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG, "deletePatient: " + patientId);
        db.delete(TABLE_PATIENT, ID + " = ?", new String[] { String.valueOf(patientId)});
    }
}

