/**
 * Helper class for SQLite Database
 *
 * Source referenced: https://www.androidhive.info/2013/09/android-sqlite-database-with-multiple-tables/
 * @author Hanna Brender
 * @editor Cole deSilva
 */

package com.example.incentive_spirometer_and_dvt_application.helpers;

import com.example.incentive_spirometer_and_dvt_application.models.Doctor;
import com.example.incentive_spirometer_and_dvt_application.models.Dvt;
import com.example.incentive_spirometer_and_dvt_application.models.DvtData;
import com.example.incentive_spirometer_and_dvt_application.models.IncentiveSpirometer;
import com.example.incentive_spirometer_and_dvt_application.models.IncentiveSpirometerData;
import com.example.incentive_spirometer_and_dvt_application.models.Patient;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * sqlite database structure and applicable functions to manage local database
 *
 * v1.0 4/20/20
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "spirometerDvtApp";
    private SQLiteDatabase defaultDB = null;

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
    public static final String ID = "_id";
    private static final String UUID = "uuid";
    private static final String START_TIMESTAMP = "startTimestamp";
    private static final String END_TIMESTAMP = "endTimestamp";

    // Incentive Spirometer table column names;
    private static final String LUNG_VOLUME = "lungVolume";
    private static final String NUMBER_OF_INHALATIONS = "numberOfInhalations";
    private static final String INHALATIONS_COMPLETED = "inhalationsCompleted";

    // DvtData table column names;
    private static final String RESISTANCE = "resistance";
    private static final String NUMBER_OF_REPS = "numberOfReps";
    private static final String REPS_COMPLETED = "repsCompleted";

    // Doctor table column names
    private static final String USERNAME = "username";

    // Patient table column names
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    private static final String HEIGHT_FEET = "heightFeet";
    private static final String HEIGHT_INCHES = "heightInches";
    private static final String WEIGHT = "weight";
    private static final String AGE = "age";
    private static final String SEX = "sex";
    private static final String INCENTIVE_SPIROMETER_ID = "incentiveSpirometerId";
    private static final String DVT_ID = "dvtId";
    private static final String INCENTIVE_SPIROMETER_UUID = "incentiveSpirometerUuid";
    private static final String DVT_UUID = "dvtUuid";

    // DoctorPatient table column names
    private static final String DOCTOR_ID = "doctorId";
    public static final String PATIENT_ID = "patientId";

    // Login table column names
    private static final String SALT = "salt";
    private static final String HASHED_PASSWORD = "hashedPassword";

    // IncentiveSpirometerData table create statement
    private static final String CREATE_TABLE_INCENTIVE_SPIROMETER = "CREATE TABLE " + TABLE_INCENTIVE_SPIROMETER + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + UUID + " TEXT UNIQUE,"
            + LUNG_VOLUME + " INTEGER,"
            + NUMBER_OF_INHALATIONS + " INTEGER)";

    // Dvt table create statement
    private static final String CREATE_TABLE_DVT = "CREATE TABLE " + TABLE_DVT + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + UUID + " TEXT UNIQUE,"
            + RESISTANCE + " TEXT,"
            + NUMBER_OF_REPS + " INTEGER)";

    // IncentiveSpirometerData table create statement
    private static final String CREATE_TABLE_INCENTIVE_SPIROMETER_DATA = "CREATE TABLE " + TABLE_INCENTIVE_SPIROMETER_DATA + "("
            + ID + " INTEGER,"
            + START_TIMESTAMP + " DATETIME,"
            + END_TIMESTAMP + " DATETIME,"
            + LUNG_VOLUME + " INTEGER,"
            + NUMBER_OF_INHALATIONS + " INTEGER,"
            + INHALATIONS_COMPLETED + " INTEGER,"
            + " PRIMARY KEY(" + ID + ", " + START_TIMESTAMP + "),"
            + " FOREIGN KEY(" + ID + ") REFERENCES " + TABLE_INCENTIVE_SPIROMETER + "(" + ID + "))";

    // DvtData table create statement
    private static final String CREATE_TABLE_DVT_DATA = "CREATE TABLE " + TABLE_DVT_DATA + "("
            + ID + " INTEGER,"
            + START_TIMESTAMP + " DATETIME,"
            + END_TIMESTAMP + " DATETIME,"
            + RESISTANCE + " TEXT,"
            + NUMBER_OF_REPS + " INTEGER,"
            + REPS_COMPLETED + " INTEGER,"
            + " PRIMARY KEY(" + ID + ", " + START_TIMESTAMP + "),"
            + " FOREIGN KEY(" + ID + ") REFERENCES " + TABLE_DVT + "(" + ID + "))";

    // Doctor table create statement
    private static final String CREATE_TABLE_DOCTOR = "CREATE TABLE " + TABLE_DOCTOR + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + USERNAME + " TEXT UNIQUE)";

    // Patient table create statement
    private static final String CREATE_TABLE_PATIENT = "CREATE TABLE " + TABLE_PATIENT + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + PATIENT_ID + " TEXT,"
            + FIRST_NAME + " TEXT,"
            + LAST_NAME + " TEXT,"
            + HEIGHT_FEET + " INTEGER,"
            + HEIGHT_INCHES + " DOUBLE,"
            + WEIGHT + " DOUBLE,"
            + AGE + " INTEGER,"
            + SEX + " TEXT,"
            + INCENTIVE_SPIROMETER_ID + " INTEGER,"
            + DVT_ID + " INTEGER,"
            + INCENTIVE_SPIROMETER_UUID + " TEXT UNIQUE,"
            + DVT_UUID + " TEXT UNIQUE)";

    private static final String CREATE_TABLE_DOCTOR_PATIENT = "CREATE TABLE " + TABLE_DOCTOR_PATIENT + "("
            + DOCTOR_ID + " INTEGER,"
            + PATIENT_ID + " INTEGER,"
            + " PRIMARY KEY(" + DOCTOR_ID + "," + PATIENT_ID + "),"
            + " FOREIGN KEY(" + DOCTOR_ID + ") REFERENCES " + TABLE_DOCTOR + "(" + ID + "),"
            + " FOREIGN KEY(" + PATIENT_ID + ") REFERENCES " + TABLE_PATIENT + "(" + ID + "))";

    private static final String CREATE_TABLE_LOGIN = "CREATE TABLE " + TABLE_LOGIN + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + USERNAME + " TEXT UNIQUE,"
            + SALT + " TEXT,"
            + HASHED_PASSWORD + " TEXT,"
            + "FOREIGN KEY(" + ID + ") REFERENCES " + TABLE_DOCTOR + "(" + ID + "),"
            + "FOREIGN KEY(" + USERNAME + ") REFERENCES " + TABLE_DOCTOR + "(" + USERNAME + "))";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {

        final SQLiteDatabase db;
        if(defaultDB != null) {
            db = defaultDB;
        }
        else {
            db = super.getWritableDatabase();
        }

        return db;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        this.defaultDB = db;
        // creating tables
        db.execSQL(CREATE_TABLE_INCENTIVE_SPIROMETER);
        db.execSQL(CREATE_TABLE_DVT);
        db.execSQL(CREATE_TABLE_INCENTIVE_SPIROMETER_DATA);
        db.execSQL(CREATE_TABLE_DVT_DATA);
        db.execSQL(CREATE_TABLE_DOCTOR);
        db.execSQL(CREATE_TABLE_PATIENT);
        db.execSQL(CREATE_TABLE_DOCTOR_PATIENT);
        db.execSQL(CREATE_TABLE_LOGIN);

        // TEST DATA

        // doctor1 password: 1234
        // doctor2 password: 5678
        db.execSQL("INSERT INTO " + TABLE_LOGIN + " VALUES(1, 'doctor1', '0563267261', '9431a5c2ac7bdecbbeb3b07105428baa109c9682938fbabdcd3ef9c40fea19ec')");
        db.execSQL("INSERT INTO " + TABLE_LOGIN + " VALUES(2, 'doctor2', '7301846435', 'f052a76e75339f7506e39b963e6e2fbb0e3287ed0fada20474ea7e0249238cbd')");

        db.execSQL("INSERT INTO " + TABLE_DOCTOR + " VALUES(1, 'doctor1')");
        db.execSQL("INSERT INTO " + TABLE_DOCTOR + " VALUES(2, 'doctor2')");

        db.execSQL("INSERT INTO " + TABLE_PATIENT + " VALUES(1, '9862WDF7300X781', 'John', 'Johnson', 5, 10, 145, 76, 'Male', 10, 90, '1234', '202122')");
        db.execSQL("INSERT INTO " + TABLE_PATIENT + " VALUES(2, '00AFDHSJ873DJH1', 'Lucy', 'Riley', 5, 7, 0, 270, 'Female', 11, 91, '5678', '232425')");
        db.execSQL("INSERT INTO " + TABLE_PATIENT + " VALUES(3, 'XY92736829HG800', 'Sean', 'Wilson', 6, 3, 190, 59, 'Other', 12, 92, '91011', '262728')");
        db.execSQL("INSERT INTO " + TABLE_PATIENT + " VALUES(4, '1234567890ABCDE', 'Allen', 'Fred', 5, 4, 155, 37, 'Male', 13, 93, '121314', '293031')");
        db.execSQL("INSERT INTO " + TABLE_PATIENT + " VALUES(5, 'XXXXXXXXXXXXXXX', 'Sammy', 'Martinez', 5, 6, 200, 81, 'Female', 14, 94, '151617', '323334')");
        db.execSQL("INSERT INTO " + TABLE_PATIENT + " VALUES(6, 'ZZZZZZZZZZZZZZZ', 'Nicole', 'Meyers', 5, 11, 140, 22, 'Female', 15, 95, '171819', '353637')");

        db.execSQL("INSERT INTO " + TABLE_DOCTOR_PATIENT + " VALUES(1,1)");
        db.execSQL("INSERT INTO " + TABLE_DOCTOR_PATIENT + " VALUES(1,2)");
        db.execSQL("INSERT INTO " + TABLE_DOCTOR_PATIENT + " VALUES(1,3)");
        db.execSQL("INSERT INTO " + TABLE_DOCTOR_PATIENT + " VALUES(1,4)");
        db.execSQL("INSERT INTO " + TABLE_DOCTOR_PATIENT + " VALUES(2,4)");
        db.execSQL("INSERT INTO " + TABLE_DOCTOR_PATIENT + " VALUES(2,5)");
        db.execSQL("INSERT INTO " + TABLE_DOCTOR_PATIENT + " VALUES(2,6)");
        db.execSQL("INSERT INTO " + TABLE_DOCTOR_PATIENT + " VALUES(2,1)");

        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER + " VALUES(10, '1234', 2000, 10)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER + " VALUES(11, '5678', 2500, 10)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER + " VALUES(12, '91011', 1500, 10)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER + " VALUES(13, '121314', 2000, 10)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER + " VALUES(14, '151617', 2000, 10)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER + " VALUES(15, '171819', 2000, 10)");

        db.execSQL("INSERT INTO " + TABLE_DVT + " VALUES(90, '202122', 1, 10)");
        db.execSQL("INSERT INTO " + TABLE_DVT + " VALUES(91, '232425', 2, 10)");
        db.execSQL("INSERT INTO " + TABLE_DVT + " VALUES(92, '262728', 1, 10)");
        db.execSQL("INSERT INTO " + TABLE_DVT + " VALUES(93, '293031', 1, 10)");
        db.execSQL("INSERT INTO " + TABLE_DVT + " VALUES(94, '323334', 1, 10)");
        db.execSQL("INSERT INTO " + TABLE_DVT + " VALUES(95, '353637', 1, 10)");

        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2020-04-13 08:00:00', '2020-04-13 08:59:59', 2000, 10, 5)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2020-04-13 09:00:00', '2020-04-13 09:59:59', 2000, 10, 6)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2020-04-13 10:00:00', '2020-04-13 10:59:59', 2000, 10, 7)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2020-04-13 11:00:00', '2020-04-13 11:59:59', 2000, 10, 8)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2020-04-13 12:00:00', '2020-04-13 12:59:59', 2000, 10, 9)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2020-04-13 13:00:00', '2020-04-13 13:59:59', 2000, 10, 10)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2020-04-13 14:00:00', '2020-04-13 14:59:59', 2000, 10, 10)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2020-04-13 15:00:00', '2020-04-13 15:59:59', 2000, 10, 9)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2020-04-13 16:00:00', '2020-04-13 16:59:59', 2000, 10, 8)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2020-04-13 17:00:00', '2020-04-13 17:59:59', 2000, 10, 7)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2020-04-13 18:00:00', '2020-04-13 18:59:59', 2000, 10, 6)");

        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2020-04-12 08:00:00', '2020-04-12 08:59:59', 2000, 10, 10)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2020-04-12 09:00:00', '2020-04-12 09:59:59', 2000, 10, 10)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2020-04-12 10:00:00', '2020-04-12 10:59:59', 2000, 10, 9)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2020-04-12 11:00:00', '2020-04-12 11:59:59', 2000, 10, 8)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2020-04-12 12:00:00', '2020-04-12 12:59:59', 2000, 10, 7)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2020-04-12 13:00:00', '2020-04-12 13:59:59', 2000, 10, 6)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2020-04-12 14:00:00', '2020-04-12 14:59:59', 2000, 10, 9)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2020-04-12 15:00:00', '2020-04-12 15:59:59', 2000, 10, 10)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2020-04-12 16:00:00', '2020-04-12 16:59:59', 2000, 10, 9)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2020-04-12 17:00:00', '2020-04-12 17:59:59', 2000, 10, 8)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2020-04-12 18:00:00', '2020-04-12 18:59:59', 2000, 10, 7)");

        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2020-04-11 08:00:00', '2020-04-11 08:59:59', 2000, 10, 6)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2020-04-11 09:00:00', '2020-04-11 09:59:59', 2000, 10, 9)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2020-04-11 10:00:00', '2020-04-11 10:59:59', 2000, 10, 10)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2020-04-11 11:00:00', '2020-04-11 11:59:59', 2000, 10, 10)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2020-04-11 12:00:00', '2020-04-11 12:59:59', 2000, 10, 9)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2020-04-11 13:00:00', '2020-04-11 13:59:59', 2000, 10, 6)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2020-04-11 14:00:00', '2020-04-11 14:59:59', 2000, 10, 8)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2020-04-11 15:00:00', '2020-04-11 15:59:59', 2000, 10, 10)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2020-04-11 16:00:00', '2020-04-11 16:59:59', 2000, 10, 9)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2020-04-11 17:00:00', '2020-04-11 17:59:59', 2000, 10, 6)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2020-04-11 18:00:00', '2020-04-11 18:59:59', 2000, 10, 5)");

        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2020-04-13 08:00:00', '2020-04-13 08:59:59', 'Easy', 10, 1)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2020-04-13 09:00:00', '2020-04-13 09:59:59', 'Easy', 10, 1)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2020-04-13 10:00:00', '2020-04-13 10:59:59', 'Easy', 10, 1)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2020-04-13 11:00:00', '2020-04-13 11:59:59', 'Easy', 10, 1)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2020-04-13 12:00:00', '2020-04-13 12:59:59', 'Easy', 10, 1)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2020-04-13 13:00:00', '2020-04-13 13:59:59', 'Easy', 10, 1)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2020-04-13 14:00:00', '2020-04-13 14:59:59', 'Easy', 10, 1)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2020-04-13 15:00:00', '2020-04-13 15:59:59', 'Easy', 10, 1)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2020-04-13 16:00:00', '2020-04-13 16:59:59', 'Easy', 10, 1)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2020-04-13 17:00:00', '2020-04-13 17:59:59', 'Easy', 10, 1)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2020-04-13 18:00:00', '2020-04-13 18:59:59', 'Easy', 10, 1)");

        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2020-04-12 08:00:00', '2020-04-12 08:59:59', 'Easy', 10, 2)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2020-04-12 09:00:00', '2020-04-12 09:59:59', 'Easy', 10, 2)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2020-04-12 10:00:00', '2020-04-12 10:59:59', 'Easy', 10, 2)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2020-04-12 11:00:00', '2020-04-12 11:59:59', 'Easy', 10, 2)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2020-04-12 12:00:00', '2020-04-12 12:59:59', 'Easy', 10, 2)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2020-04-12 13:00:00', '2020-04-12 13:59:59', 'Easy', 10, 2)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2020-04-12 14:00:00', '2020-04-12 14:59:59', 'Easy', 10, 2)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2020-04-12 15:00:00', '2020-04-12 15:59:59', 'Easy', 10, 2)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2020-04-12 16:00:00', '2020-04-12 16:59:59', 'Easy', 10, 2)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2020-04-12 17:00:00', '2020-04-12 17:59:59', 'Easy', 10, 2)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2020-04-12 18:00:00', '2020-04-12 18:59:59', 'Easy', 10, 2)");

        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2020-04-11 08:00:00', '2020-04-11 08:59:59', 'Easy', 10, 3)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2020-04-11 09:00:00', '2020-04-11 09:59:59', 'Easy', 10, 3)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2020-04-11 10:00:00', '2020-04-11 10:59:59', 'Easy', 10, 3)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2020-04-11 11:00:00', '2020-04-11 11:59:59', 'Easy', 10, 3)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2020-04-11 12:00:00', '2020-04-11 12:59:59', 'Easy', 10, 3)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2020-04-11 13:00:00', '2020-04-11 13:59:59', 'Easy', 10, 3)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2020-04-11 14:00:00', '2020-04-11 14:59:59', 'Easy', 10, 3)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2020-04-11 15:00:00', '2020-04-11 15:59:59', 'Easy', 10, 3)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2020-04-11 16:00:00', '2020-04-11 16:59:59', 'Easy', 10, 3)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2020-04-11 17:00:00', '2020-04-11 17:59:59', 'Easy', 10, 3)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2020-04-11 18:00:00', '2020-04-11 18:59:59', 'Easy', 10, 3)");

// sample old data
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-08 08:00:00', '2019-11-08 08:59:59', 2000, 10, 3)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-08 09:00:00', '2019-11-08 09:59:59', 2000, 10, 0)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-08 10:00:00', '2019-11-08 10:59:59', 2000, 10, 0)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-08 11:00:00', '2019-11-08 11:59:59', 2000, 10, 0)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-08 12:00:00', '2019-11-08 12:59:59', 2000, 10, 7)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-08 13:00:00', '2019-11-08 13:59:59', 2000, 10, 5)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-08 14:00:00', '2019-11-08 14:59:59', 2000, 10, 4)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-08 15:00:00', '2019-11-08 15:59:59', 2000, 10, 6)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-08 16:00:00', '2019-11-08 16:59:59', 2000, 10, 4)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-08 17:00:00', '2019-11-08 17:59:59', 2000, 10, 5)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-08 18:00:00', '2019-11-08 18:59:59', 2000, 10, 5)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-08 19:00:00', '2019-11-08 19:59:59', 2000, 10, 4)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-08 20:00:00', '2019-11-08 20:59:59', 2000, 10, 3)");
//
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-09 08:00:00', '2019-11-09 08:59:59', 2000, 10, 5)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-09 09:00:00', '2019-11-09 09:59:59', 2000, 10, 7)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-09 10:00:00', '2019-11-09 10:59:59', 2000, 10, 4)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-09 11:00:00', '2019-11-09 11:59:59', 2000, 10, 6)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-09 12:00:00', '2019-11-09 12:59:59', 2000, 10, 5)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-09 13:00:00', '2019-11-09 13:59:59', 2000, 10, 5)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-09 14:00:00', '2019-11-09 14:59:59', 2000, 10, 0)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-09 15:00:00', '2019-11-09 15:59:59', 2000, 10, 0)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-09 16:00:00', '2019-11-09 16:59:59', 2000, 10, 6)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-09 17:00:00', '2019-11-09 17:59:59', 2000, 10, 5)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-09 18:00:00', '2019-11-09 18:59:59', 2000, 10, 4)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-09 19:00:00', '2019-11-09 19:59:59', 2000, 10, 3)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-09 20:00:00', '2019-11-09 20:59:59', 2000, 10, 6)");
//
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-10 08:00:00', '2019-11-10 08:59:59', 2000, 10, 6)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-10 09:00:00', '2019-11-10 09:59:59', 2000, 10, 10)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-10 10:00:00', '2019-11-10 10:59:59', 2000, 10, 10)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-10 11:00:00', '2019-11-10 11:59:59', 2000, 10, 11)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-10 12:00:00', '2019-11-10 12:59:59', 2000, 10, 10)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-10 13:00:00', '2019-11-10 13:59:59', 2000, 10, 10)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-10 14:00:00', '2019-11-10 14:59:59', 2000, 10, 8)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-10 15:00:00', '2019-11-10 15:59:59', 2000, 10, 9)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-10 16:00:00', '2019-11-10 16:59:59', 2000, 10, 10)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-10 17:00:00', '2019-11-10 17:59:59', 2000, 10, 10)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-10 18:00:00', '2019-11-10 18:59:59', 2000, 10, 10)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-10 19:00:00', '2019-11-10 19:59:59', 2000, 10, 9)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-10 20:00:00', '2019-11-10 20:59:59', 2000, 10, 10)");
//
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(11, '2019-11-08 10:58:00', '2019-11-10 11:57:59', 2500, 10, 9)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(11, '2019-11-08 11:58:00', '2019-11-10 12:57:59', 2500, 10, 8)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(11, '2019-11-08 12:58:00', '2019-11-10 13:57:59', 2500, 10, 7)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(11, '2019-11-08 13:58:00', '2019-11-10 14:57:59', 2500, 10, 6)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(12, '2019-11-08 10:58:00', '2019-11-10 11:57:59', 1500, 10, 5)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(12, '2019-11-08 11:58:00', '2019-11-10 12:57:59', 1500, 10, 7)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(12, '2019-11-08 12:58:00', '2019-11-10 13:57:59', 1500, 10, 9)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(12, '2019-11-08 13:58:00', '2019-11-10 14:57:59', 1500, 10, 10)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(12, '2019-11-08 14:58:00', '2019-11-10 15:57:59', 1500, 10, 10)");
//        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(12, '2019-11-08 15:58:00', '2019-11-10 16:57:59', 1500, 10, 10)");
//
//        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-10 10:00:00', '2019-11-10 10:59:59', 'Easy', 10, 5)");
//        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-10 11:00:00', '2019-11-10 11:59:59', 'Easy', 10, 6)");
//        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-10 12:00:00', '2019-11-10 12:59:59', 'Medium', 10, 6)");
//        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-10 13:00:00', '2019-11-10 13:59:59', 'Easy', 10, 7)");
//        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-10 14:00:00', '2019-11-10 14:59:59', 'Medium', 10, 10)");
//        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-10 15:00:00', '2019-11-10 15:59:59', 'Easy', 10, 8)");
//        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-10 16:00:00', '2019-11-10 16:59:59', 'Hard', 10, 8)");
//        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-10 17:00:00', '2019-11-10 17:59:59', 'Easy', 10, 7)");
//        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-10 18:00:00', '2019-11-10 18:59:59', 'Hard', 10, 10)");
//        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-10 19:00:00', '2019-11-10 19:59:59', 'Medium', 10, 6)");
//        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-10 20:00:00', '2019-11-10 20:59:59', 'Easy', 10, 5)");
//
//        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-09 10:00:00', '2019-11-09 10:59:59', 'Easy', 10, 3)");
//        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-09 11:00:00', '2019-11-09 11:59:59', 'Medium', 10, 10)");
//        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-09 12:00:00', '2019-11-09 12:59:59', 'Easy', 10, 6)");
//        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-09 13:00:00', '2019-11-09 13:59:59', 'Easy', 10, 7)");
//        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-09 14:00:00', '2019-11-09 14:59:59', 'Hard', 10, 8)");
//        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-09 15:00:00', '2019-11-09 15:59:59', 'Medium', 10, 9)");
//        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-09 16:00:00', '2019-11-09 16:59:59', 'Medium', 10, 10)");
//        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-09 17:00:00', '2019-11-09 17:59:59', 'Easy', 10, 11)");
//        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-09 18:00:00', '2019-11-09 18:59:59', 'Easy', 10, 8)");
//        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-09 19:00:00', '2019-11-09 19:59:59', 'Hard', 10, 9)");
//        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-09 20:00:00', '2019-11-09 20:59:59', 'Easy', 10, 7)");
//
//        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-08 10:00:00', '2019-11-08 10:59:59', 'Medium', 10, 9)");
//        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-08 11:00:00', '2019-11-08 11:59:59', 'Easy', 10, 10)");
//        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-08 12:00:00', '2019-11-08 12:59:59', 'Easy', 10, 8)");
//        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-08 13:00:00', '2019-11-08 13:59:59', 'Hard', 10, 6)");
//        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-08 14:00:00', '2019-11-08 14:59:59', 'Medium', 10, 9)");
//        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-08 15:00:00', '2019-11-08 15:59:59', 'Medium', 10, 10)");
//        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-08 16:00:00', '2019-11-08 16:59:59', 'Easy', 10, 10)");
//        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-08 17:00:00', '2019-11-08 17:59:59', 'Easy', 10, 9)");
//        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-08 18:00:00', '2019-11-08 18:59:59', 'Easy', 10, 11)");
//        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-08 19:00:00', '2019-11-08 19:59:59', 'Hard', 10, 8)");
//        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-08 20:00:00', '2019-11-08 20:59:59', 'Medium', 10, 10)");
//
//        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(91, '2019-11-10 16:00:00', '2019-11-08 16:59:59', 'Easy', 10, 5)");
//        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(91, '2019-11-10 17:00:00', '2019-11-08 17:59:59', 'Medium', 10, 5)");
//        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(91, '2019-11-10 18:00:00', '2019-11-08 18:59:59', 'Easy', 10, 8)");
//        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(91, '2019-11-10 19:00:00', '2019-11-08 19:59:59', 'Hard', 10, 9)");
//        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(91, '2019-11-10 20:00:00', '2019-11-08 20:59:59', 'Easy', 10, 10)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.defaultDB = db;
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

    // *************************** Patient table CRUD functions ****************************

    /**
     * Insert a new patient
     * @param patient Patient object to insert
     * @return -1 if query fails
     */
    public boolean insertPatient(Patient patient) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(PATIENT_ID, patient.getPatientId());
        values.put(FIRST_NAME, patient.getFirstName());
        values.put(LAST_NAME, patient.getLastName());
        values.put(HEIGHT_FEET, patient.getHeightFeet());
        values.put(HEIGHT_INCHES, patient.getHeightInches());
        values.put(WEIGHT, patient.getWeight());
        values.put(AGE, patient.getAge());
        values.put(SEX, patient.getSex());
        values.put(INCENTIVE_SPIROMETER_ID, patient.getIncentiveSpirometerId());
        values.put(DVT_ID, patient.getDvtId());
        values.put(INCENTIVE_SPIROMETER_UUID, patient.getIncentiveSpirometerUuid());
        values.put(DVT_UUID, patient.getDvtUuid());

        long result = db.insert(TABLE_PATIENT, null, values);

        return result != -1;
    }

    public Cursor getAllPatientsCursor(int doctorId) {
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT p.* FROM "
                + TABLE_PATIENT + " p, " + TABLE_DOCTOR_PATIENT + " dp"
                + " WHERE dp." + DOCTOR_ID + " = ?"
                + " AND dp." + PATIENT_ID + " = p." + ID;

        Log.d(TAG, "getAllPatientsCursor: " + query);

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(doctorId)});

        return cursor;
    }

    /**
     * Get a given patient's data
     * @param patientId patient to read data from
     * @return Patient object with the patient's data
     */
    public Patient getPatient(int patientId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PATIENT + " WHERE " + ID + " = ?";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(patientId)});
        Log.d(TAG, "getPatient: "+ query);

        if (c != null && c.getCount() > 0)
            c.moveToFirst();

        // create patient
        Patient patient = new Patient();
        patient.setId(c.getInt(c.getColumnIndex(ID)));
        patient.setPatientId(c.getString(c.getColumnIndex(PATIENT_ID)));
        patient.setFirstName(c.getString(c.getColumnIndex(FIRST_NAME)));
        patient.setLastName(c.getString(c.getColumnIndex(LAST_NAME)));
        patient.setHeightFeet(c.getInt(c.getColumnIndex(HEIGHT_FEET)));
        patient.setHeightInches(c.getDouble(c.getColumnIndex(HEIGHT_INCHES)));
        patient.setWeight(c.getDouble(c.getColumnIndex(WEIGHT)));
        patient.setAge(c.getInt(c.getColumnIndex(AGE)));
        patient.setSex(c.getString(c.getColumnIndex(SEX)));
        patient.setIncentiveSpirometerId(c.getInt(c.getColumnIndex(INCENTIVE_SPIROMETER_ID)));
        patient.setDvtId(c.getInt(c.getColumnIndex(DVT_ID)));
        patient.setIncentiveSpirometerUuid(c.getString(c.getColumnIndex(INCENTIVE_SPIROMETER_UUID)));
        patient.setDvtUuid(c.getString(c.getColumnIndex(DVT_UUID)));

        return patient;
    }

    public int getIdByPatientId(String patientId){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PATIENT + " WHERE " + PATIENT_ID + " = ?";
        Cursor c = db.rawQuery(query, new String[]{patientId});

        Log.d(TAG, "patientExists: "+ query);
        int id = -1;
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            id = c.getInt(c.getColumnIndex(ID));

            return id;
        }

        return id;
    }

    /**
     * Returns the patient's id who has a given incentive spirometer id
     * @param incentiveSpirometerId
     * @return patient id
     */
    public int getPatientByIncentiveSpriometerId(int incentiveSpirometerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PATIENT + " WHERE " + INCENTIVE_SPIROMETER_ID + " = ?";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(incentiveSpirometerId)});

        Log.d(TAG, "getPatientByIncentiveSpriometerId: "+ query);

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            return c.getInt(c.getColumnIndex(ID));
        }
        return -1;
    }

    /**
     * Returns the patient's id who has a given incentive spirometer id
     * @param incentiveSpirometerUuid
     * @return patient id
     */
    public int getPatientByIncentiveSpriometerUuid(String incentiveSpirometerUuid) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT p.* FROM " + TABLE_PATIENT + " p, " + TABLE_INCENTIVE_SPIROMETER + " s WHERE s." + ID + " = p." + INCENTIVE_SPIROMETER_ID + " AND s." + UUID + " = ?";
        Cursor c = db.rawQuery(query, new String[]{incentiveSpirometerUuid});

        Log.d(TAG, "getPatientByIncentiveSpriometerId: "+ query);

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            return c.getInt(c.getColumnIndex(ID));
        }
        return -1;
    }

    /**
     * Returns the patient's id who has a given dvt id
     * @param dvtUuid
     * @return patient id
     */
    public int getPatientByDvtUuid(String dvtUuid) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT p.* FROM " + TABLE_PATIENT + " p, " + TABLE_DVT + " d WHERE d." + ID + " = p." + DVT_ID + " AND d." + UUID + " = ?";
        Cursor c = db.rawQuery(query, new String[]{dvtUuid});

        Log.d(TAG, "getPatientByDvtId: "+ query);

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            return c.getInt(c.getColumnIndex(ID));
        }
        return -1;
    }

    /**
     * Check if a patient with a given id exists
     * @param patientId patient
     * @return true if patient exists, false otherwise
     */
    public boolean patientExists(String patientId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PATIENT + " WHERE " + PATIENT_ID + " = ?";
        Cursor c = db.rawQuery(query, new String[]{patientId});

        Log.d(TAG, "patientExists: "+ query);

        if (c != null && c.getCount() > 0) {
            return true;
        }

        return false;
    }

    /**
     * Searches for a particular patient using a key word
     * @param searchKey
     * @param doctorId
     * @return a cursor to the DB entries with the specifies users
     */
    public Cursor getPatientListByKeyword(String searchKey, int doctorId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query =  "SELECT p.* FROM "
                + TABLE_PATIENT + " p, " + TABLE_DOCTOR_PATIENT + " dp"
                + " WHERE dp." + DOCTOR_ID + " = ?"
                + " AND dp." + PATIENT_ID + " = p." + ID
                + " AND (p." +  ID + "  LIKE  ?" +
                " OR p." + LAST_NAME + " LIKE ?" +
                " OR p." + FIRST_NAME + " LIKE ? )";

        Log.d(TAG, "getPatientListByKeyword: " + query);

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(doctorId), searchKey + "%", searchKey + "%", searchKey + "%"});

        return cursor;
    }

    /**
     * Returns a list of patients that are old (they have used both device for more than 2 weeks)
     * @param doctorId
     * @return list of patient ids
     */
    public List<Integer> getOldPatients(int doctorId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query1 = "SELECT p." + ID
                + ", (julianday(MAX('now')) - julianday(MAX(a." + END_TIMESTAMP + "))) AS difference"
                + " FROM " + TABLE_PATIENT + " p, " + TABLE_DOCTOR_PATIENT + " dp, "
                + TABLE_INCENTIVE_SPIROMETER_DATA + " a, " + TABLE_INCENTIVE_SPIROMETER_DATA + " b"
                + " WHERE dp." + DOCTOR_ID + " = ?"
                + " AND dp." + PATIENT_ID + " = p." + ID
                + " AND a." + ID + " = b." + ID
                + " AND p." + INCENTIVE_SPIROMETER_ID + " = a." + ID
                + " GROUP BY p." + ID
                + " HAVING difference > 14";

        String query2 = "SELECT p." + ID
                + ", (julianday(MAX('now')) - julianday(MAX(a." + END_TIMESTAMP + "))) AS difference"
                + " FROM " + TABLE_PATIENT + " p, " + TABLE_DOCTOR_PATIENT + " dp, "
                + TABLE_DVT_DATA + " a, " + TABLE_DVT_DATA + " b"
                + " WHERE dp." + DOCTOR_ID + " = ?"
                + " AND dp." + PATIENT_ID + " = p." + ID
                + " AND a." + ID + " = b." + ID
                + " AND p." + DVT_ID + " = a." + ID
                + " GROUP BY p." + ID
                + " HAVING difference > 14";

        String query3 = "SELECT p." + ID
                + " FROM " + TABLE_PATIENT + " p, " + TABLE_DOCTOR_PATIENT + " dp, "
                + TABLE_INCENTIVE_SPIROMETER + " i"
                + " WHERE dp." + DOCTOR_ID + " = ?"
                + " AND dp." + PATIENT_ID + " = p." + ID
                + " AND p." + INCENTIVE_SPIROMETER_ID + " = i." + ID;

        String query4 = "SELECT p." + ID
                + " FROM " + TABLE_PATIENT + " p, " + TABLE_DOCTOR_PATIENT + " dp, "
                + TABLE_DVT + " d"
                + " WHERE dp." + DOCTOR_ID + " = ?"
                + " AND dp." + PATIENT_ID + " = p." + ID
                + " AND p." + DVT_ID + " = d." + ID;

        Log.d(TAG, "getOldPatients: " + query1);
        Log.d(TAG, "getOldPatients: " + query2);
        Log.d(TAG, "getOldPatients: " + query3);
        Log.d(TAG, "getOldPatients: " + query4);

        Cursor c1 = db.rawQuery(query1, new String[]{String.valueOf(doctorId)});
        Cursor c2 = db.rawQuery(query2, new String[]{String.valueOf(doctorId)});
        Cursor c3 = db.rawQuery(query3, new String[]{String.valueOf(doctorId)});
        Cursor c4 = db.rawQuery(query4, new String[]{String.valueOf(doctorId)});

        List<Integer> oldSpiroPatientIds = new ArrayList<Integer>();
        List<Integer> oldDvtPatientIds = new ArrayList<Integer>();
        List<Integer> oldPatientIds = new ArrayList<Integer>();

        // map of patient id to how many devices they are using (either 1 or 2)
        HashMap<Integer, Integer> patientIds = new HashMap<Integer, Integer>();

        if (c1.moveToFirst()) {
            do {
                oldSpiroPatientIds.add(c1.getInt(c1.getColumnIndex(ID)));
            } while (c1.moveToNext());
        }
        if (c2.moveToFirst()) {
            do {
                oldDvtPatientIds.add(c2.getInt(c2.getColumnIndex(ID)));
            } while (c2.moveToNext());
        }
        if (c3.moveToFirst()) {
            do {
                if (!patientIds.containsKey(c3.getInt(c3.getColumnIndex(ID)))) {
                    patientIds.put(c3.getInt(c3.getColumnIndex(ID)), 1);
                } else {
                    patientIds.put(c3.getInt(c3.getColumnIndex(ID)), 2);
                }
            } while (c3.moveToNext());
        }
        if (c4.moveToFirst()) {
            do {
                if (!patientIds.containsKey(c4.getInt(c4.getColumnIndex(ID)))) {
                    patientIds.put(c4.getInt(c4.getColumnIndex(ID)), 1);
                } else {
                    patientIds.put(c4.getInt(c4.getColumnIndex(ID)), 2);
                }
            } while (c4.moveToNext());
        }

        for (Integer id : patientIds.keySet()) {
            if (patientIds.get(id) == 1 && oldSpiroPatientIds.contains(id)) {
                oldPatientIds.add(id);
            } else if (patientIds.get(id) == 1 && oldDvtPatientIds.contains(id)) {
                oldPatientIds.add(id);
            } else if (patientIds.get(id) == 2 && oldSpiroPatientIds.contains(id) && oldDvtPatientIds.contains(id)) {
                oldPatientIds.add(id);
            }
        }

        return oldPatientIds;
    }

    /**
     * Delete a given patient and their devices and their association with their doctor
     * @param patientId the id matching the patient to delete
     */
    public void deletePatientById(int patientId) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query1 = "DELETE FROM "
                + TABLE_INCENTIVE_SPIROMETER_DATA
                + " WHERE " + ID + " = ("
                + "SELECT " + INCENTIVE_SPIROMETER_ID
                + " FROM " + TABLE_PATIENT
                + " WHERE " + ID + " = ?)";
        String query2 = "DELETE FROM "
                + TABLE_INCENTIVE_SPIROMETER
                + " WHERE " + ID + " = ("
                + "SELECT " + INCENTIVE_SPIROMETER_ID
                + " FROM " + TABLE_PATIENT
                + " WHERE " + ID + " = ?)";
        String query3 = "DELETE FROM "
                + TABLE_DVT_DATA
                + " WHERE " + ID + " = ("
                + "SELECT " + DVT_ID
                + " FROM " + TABLE_PATIENT
                + " WHERE " + ID + " = ?)";
        String query4 = "DELETE FROM "
                + TABLE_DVT
                + " WHERE " + ID + " = ("
                + "SELECT " + DVT_ID
                + " FROM " + TABLE_PATIENT
                + " WHERE " + ID + " = ?)";

        Log.d(TAG, "deletePatientById: " + query1);
        Log.d(TAG, "deletePatientById: " + query2);
        Log.d(TAG, "deletePatientById: " + query3);
        Log.d(TAG, "deletePatientById: " + query4);

        db.execSQL(query1, new String[] { String.valueOf(patientId)});
        db.execSQL(query2, new String[] { String.valueOf(patientId)});
        db.execSQL(query3, new String[] { String.valueOf(patientId)});
        db.execSQL(query4, new String[] { String.valueOf(patientId)});
        db.delete(TABLE_DOCTOR_PATIENT, PATIENT_ID + " = ?", new String[] { String.valueOf(patientId)});
        db.delete(TABLE_PATIENT, ID + " = ?", new String[] { String.valueOf(patientId)});
    }

    /**
     * Update a given patients information
     * @param patient
     * @return number of rows updated
     */
    public int updatePatient(Patient patient) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PATIENT_ID, patient.getPatientId());
        values.put(FIRST_NAME, patient.getFirstName());
        values.put(LAST_NAME, patient.getLastName());
        values.put(HEIGHT_FEET, patient.getHeightFeet());
        values.put(HEIGHT_INCHES, patient.getHeightInches());
        values.put(WEIGHT, patient.getWeight());
        values.put(AGE, patient.getAge());
        values.put(SEX, patient.getSex());
        values.put(INCENTIVE_SPIROMETER_ID, patient.getIncentiveSpirometerId());
        values.put(DVT_ID, patient.getDvtId());
        values.put(INCENTIVE_SPIROMETER_UUID, patient.getIncentiveSpirometerUuid());
        values.put(DVT_UUID, patient.getDvtUuid());

        return db.update(TABLE_PATIENT, values, ID + " = ?",
                new String[] { String.valueOf(patient.getId()) });
    }

    // *************************** DoctorPatient table CRUD functions ****************************

    /**
     * Insert new doctor patient association
     * @param patientId patient associated with doctor
     * @param doctorId doctor associated with patient
     * @return true if inserted correctly, false otherwise
     */
    public boolean insertDoctorPatient(int patientId, int doctorId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PATIENT_ID, patientId);
        values.put(DOCTOR_ID, doctorId);

        long result = db.insert(TABLE_DOCTOR_PATIENT, null, values);

        return result != -1; // if result = -1 data doesn't insert
    }

    // *************************** Doctor table CRUD functions ****************************

    /**
     * inserts a doctor(user) into the database
     * @param username
     * @return the new doctor id
     */
    public int insertDoctor(String username){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(USERNAME, username);

        return (int) db.insert(TABLE_DOCTOR, null, values);
    }

    /**
     * inserts a login into the database
     * @param username
     */
    public boolean insertLogin(String username, String salt, String hashedPassword){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(USERNAME, username);
        values.put(SALT, salt);
        values.put(HASHED_PASSWORD, hashedPassword);

        long result = db.insert(TABLE_LOGIN, null, values);

        return result != -1; // if result = -1 data doesn't insert
    }

    public int getDoctorId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + ID + " FROM " + TABLE_DOCTOR
                + " WHERE " + USERNAME + " = ?";
        Cursor c = db.rawQuery(query, new String[]{username});

        Log.d(TAG, "getDoctorId: " + query);

        int doctorId = -1;

        if (c.moveToFirst()) {
            do {
                doctorId = c.getInt(c.getColumnIndex(ID));
            } while (c.moveToNext());
        }
        return doctorId;
    }

    /**
     * Checks if a doctor with a certain username exists
     * @param username
     * @return true if exists, false otherwise
     */
    public boolean doctorExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_DOCTOR + " WHERE " + USERNAME + " = ?";

        Cursor c = db.rawQuery(query, new String[]{username});

        if (c != null && c.getCount() > 0) {
            return true;
        }
        return false;
    }

    // *************************** Login table CRUD functions ****************************

    /**
     * gets the salt and hashed password from the specified username so that authentication can be done
     * @param username
     * @return a string array containing the salt at results[0] and the hashed password at results[1]
     */
    public String[] getLoginInformation(String username) {

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + SALT + ", " + HASHED_PASSWORD + " FROM " + TABLE_LOGIN + " WHERE " + USERNAME + " = ?";
        Cursor c = db.rawQuery(query, new String[]{username});

        String[] results = new String[2];
        if (c != null && c.moveToFirst()) {

            results[0] = c.getString(c.getColumnIndex(SALT));
            results[1] = c.getString(c.getColumnIndex(HASHED_PASSWORD));
        }
        c.close();
        return results;
    }

    /**
     * Checks if the user exists in the login table (if they are a validated user of the application)
     * @param username
     * @return boolean if the username exists in the database or not
     */
    public boolean isRealUser(String username) {

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_LOGIN + " WHERE " + USERNAME + " = ?";

        try {

            Cursor c = db.rawQuery(query, new String[]{username});
            int empty = 0;
            if(c != null && c.moveToFirst()) {

                empty = c.getInt(0 );
            }
            
            if (empty == 0) {

                Log.d(TAG, "isRealUser: is not real user");
                c.close();
                return false;
            }

            Log.d(TAG, "isRealUser: is real user");
            c.close();
            return true;
        }
        catch (SQLiteException e){

            Log.d(TAG, "isRealUser: fatal exception");
            return false;
        }
    }

    // *************************** Incentive Spirometer Data table CRUD functions ****************************

    public String getDeviceUuid(int patientId, boolean isSpiro) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PATIENT
                + " WHERE " + ID + " = ?";
        Cursor c = db.rawQuery(query, new String[]{Integer.toString(patientId)});

        Log.d(TAG, "getDeviceUuid: " + query);

        String deviceUuid = null;

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();

            if(isSpiro){
                deviceUuid = c.getString(c.getColumnIndex(INCENTIVE_SPIROMETER_UUID));
            }
            else{
                deviceUuid = c.getString(c.getColumnIndex(DVT_UUID));
            }

            Log.d(TAG, "getDeviceUuid: " + deviceUuid);
            return deviceUuid;
        }
        return null;

    }

    /**
     * Get a given patient's spirometer data
     * @param patientId patient to read data from
     * @return a list of patient spirometer exercises
     */
    public List<IncentiveSpirometerData> getPatinetSpirometerData(int patientId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT isd.* FROM " + TABLE_PATIENT + " p, " + TABLE_INCENTIVE_SPIROMETER_DATA
                + " isd WHERE p." + INCENTIVE_SPIROMETER_ID + " = isd." + ID
                + " AND p." + ID + " = ?";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(patientId)});

        Log.d(TAG, "getPatinetSpirometerData: " + query);

        List<IncentiveSpirometerData> spirometerData = new ArrayList<>();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

        if (c.moveToFirst()){
            do{
                Date start = null;
                Date end = null;
                try {
                    start = dateFormat.parse(c.getString(c.getColumnIndex(START_TIMESTAMP)));
                    end = dateFormat.parse(c.getString(c.getColumnIndex(END_TIMESTAMP)));
                } catch (java.text.ParseException e) {
                    Log.e(TAG, "getPatinetSpirometerData: ERROR PARSING DATE FROM database");
                }

                IncentiveSpirometerData spData = new IncentiveSpirometerData();
                spData.setId(c.getInt(c.getColumnIndex(ID)));
                spData.setStartTime(start);
                spData.setEndTime(end);
                spData.setLungVolume(c.getInt(c.getColumnIndex(LUNG_VOLUME)));
                spData.setNumberOfInhalations(c.getInt((c.getColumnIndex(NUMBER_OF_INHALATIONS))));
                spData.setInhalationsCompleted(c.getInt(c.getColumnIndex(INHALATIONS_COMPLETED)));

                spirometerData.add(spData);
            } while (c.moveToNext());
        }

        c.close();
        return spirometerData;
    }

    /**
     * Insert a new Spirometer Datapoint
     * @param isd a new datapoint to be added to the incentive spirometer data database
     */
    public void insertIncentiveSpirometerData(IncentiveSpirometerData isd) {
        SQLiteDatabase db = this.getWritableDatabase();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startTime = isd.getStartTime();
        String formattedStartTime = format.format(startTime);
        Date endTime = isd.getEndTime();
        String formattedEndTime = format.format(endTime);


        ContentValues values = new ContentValues();
        values.put(ID, isd.getId());
        values.put(START_TIMESTAMP, formattedStartTime);
        values.put(END_TIMESTAMP, formattedEndTime);
        values.put(LUNG_VOLUME, isd.getLungVolume());
        values.put(INHALATIONS_COMPLETED, isd.getInhalationsCompleted());
        values.put(NUMBER_OF_INHALATIONS, isd.getNumberOfInhalations());

        long result = db.insert(TABLE_INCENTIVE_SPIROMETER_DATA, null, values);
    }


    // *************************** DVT Data table CRUD functions ****************************

    /**
     * Get a given patient's dvt data
     * @param patientId patient to read data from
     * @return a list of patient dvt exercises
     */
    public List<DvtData> getPatinetDvtData(int patientId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT dvtd.* FROM " + TABLE_PATIENT + " p, " + TABLE_DVT_DATA
                + " dvtd WHERE p." + DVT_ID + " = dvtd." + ID
                + " AND p." + ID + " = ?";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(patientId)});

        List<DvtData> dvtData = new ArrayList<>();

        Log.d(TAG, "getPatinetDvtData " + query);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

        if (c.moveToFirst()){
            do{
                Date start = null;
                Date end = null;
                try {
                    start = dateFormat.parse(c.getString(c.getColumnIndex(START_TIMESTAMP)));
                    end = dateFormat.parse(c.getString(c.getColumnIndex(END_TIMESTAMP)));
                } catch (java.text.ParseException e) {
                    Log.e(TAG, "getPatinetDvtData: ERROR PARSING DATE FROM database");
                }

                DvtData dvtDatapoint = new DvtData();
                dvtDatapoint.setId(c.getInt(c.getColumnIndex(ID)));
                dvtDatapoint.setStartTime(start);
                dvtDatapoint.setEndTime(end);
                dvtDatapoint.setResistance(c.getString(c.getColumnIndex(RESISTANCE)));
                dvtDatapoint.setNumberOfReps(c.getInt(c.getColumnIndex(NUMBER_OF_REPS)));
                dvtDatapoint.setRepsCompleted(c.getInt(c.getColumnIndex(REPS_COMPLETED)));


                dvtData.add(dvtDatapoint);
            } while (c.moveToNext());
        }

        c.close();
        return dvtData;
    }

    /**
     * insert a new DVT datapoint
     * @param dvtd the datapoint to be added to the table
     */
    public void insertDvtData (DvtData dvtd) {
        SQLiteDatabase db = this.getWritableDatabase();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startTime = dvtd.getStartTime();
        String formattedStartTime = format.format(startTime);
        Date endTime = dvtd.getEndTime();
        String formattedEndTime = format.format(endTime);

        ContentValues values = new ContentValues();
        values.put(ID, dvtd.getId());
        values.put(START_TIMESTAMP, formattedStartTime);
        values.put(END_TIMESTAMP, formattedEndTime);
        values.put(RESISTANCE, dvtd.getResistance());
        values.put(REPS_COMPLETED, dvtd.getRepsCompleted());
        values.put(NUMBER_OF_REPS, dvtd.getNumberOfReps());

        long result = db.insert(TABLE_DVT_DATA, null, values);

    }

    // *************************** Incentive Spirometer table CRUD functions ****************************

    /**
     * Get a patient's incentive spirometer data
     * @param patientId patient related to the incentive spirometer
     * @return IncentiveSpirometer object with the patient's device data
     */
    public IncentiveSpirometer getIncentiveSpirometer(int patientId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT i.* FROM " + TABLE_PATIENT + " p, " + TABLE_INCENTIVE_SPIROMETER + " i"
                + " WHERE p." + ID + " = ?"
                + " AND p." + INCENTIVE_SPIROMETER_ID + " = i." + ID;

        Log.d(TAG, "getIncentiveSpirometer: "+ query);
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(patientId)});

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();

            // create incentive spirometer
            IncentiveSpirometer incentiveSpirometer = new IncentiveSpirometer();
            incentiveSpirometer.setId(c.getInt(c.getColumnIndex(ID)));
            incentiveSpirometer.setUuid(c.getString(c.getColumnIndex(UUID)));
            incentiveSpirometer.setLungVolume(c.getInt(c.getColumnIndex(LUNG_VOLUME)));
            incentiveSpirometer.setNumberOfInhalations(c.getInt(c.getColumnIndex(NUMBER_OF_INHALATIONS)));
            return incentiveSpirometer;
        }
        return null;
    }

    /**
     * Get incentive spirometer by spirometer Uuid
     * @param spirometerUuid id of the desired spriometer
     * @return data for the desired spirometer
     */
    public IncentiveSpirometer getIncentiveSpirometerByUuid(String spirometerUuid){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT i.* FROM " + TABLE_INCENTIVE_SPIROMETER + " i"
                + " WHERE i." + UUID + " = ?";

        Cursor c = db.rawQuery(query, new String[]{ spirometerUuid });

        Log.d(TAG, "getIncentiveSpirometerBySpirometerId: "+ query);

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();

            // create incentive spirometer
            IncentiveSpirometer incentiveSpirometer = new IncentiveSpirometer();
            incentiveSpirometer.setId(c.getInt(c.getColumnIndex(ID)));
            incentiveSpirometer.setUuid(c.getString(c.getColumnIndex(UUID)));
            incentiveSpirometer.setLungVolume(c.getInt(c.getColumnIndex(LUNG_VOLUME)));
            incentiveSpirometer.setNumberOfInhalations(c.getInt(c.getColumnIndex(NUMBER_OF_INHALATIONS)));

            return incentiveSpirometer;
        }
        return null;
    }

    /**
     * Delete a spirometer device
     * @param spirometerId the device to delete
     * @param patientId the patient that has that device
     */
    public void deleteSpirometerById(int spirometerId, int patientId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.putNull(INCENTIVE_SPIROMETER_ID);
        values.putNull(INCENTIVE_SPIROMETER_UUID);

        int updateResult = db.update(TABLE_PATIENT, values, ID + " = ?", new String[] { String.valueOf(patientId) });
        int deleteDataResult = db.delete(TABLE_INCENTIVE_SPIROMETER_DATA, ID + " = ?", new String[] { String.valueOf(spirometerId)});
        int deleteDeviceResult = db.delete(TABLE_INCENTIVE_SPIROMETER, ID + " = ?", new String[] { String.valueOf(spirometerId)});
    }

    /**
     * Insert a new Incentive Spirometer device
     * @param incentiveSpirometer spirometer object to insert
     * @return -1 if query fails
     */
    public boolean insertIncentiveSpirometer(IncentiveSpirometer incentiveSpirometer) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UUID, incentiveSpirometer.getUuid());
        values.put(NUMBER_OF_INHALATIONS, incentiveSpirometer.getNumberOfInhalations());
        values.put(LUNG_VOLUME, incentiveSpirometer.getLungVolume());

        long result = db.insert(TABLE_INCENTIVE_SPIROMETER, null, values);
        return result != -1;
    }

    /**
     * updates the incentive spirometer assigned to a patient
     * @param incentiveSpirometer
     * @param patientId
     * @return
     */
    public boolean updateIncentiveSpiroForPatient(IncentiveSpirometer incentiveSpirometer, int patientId){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(INCENTIVE_SPIROMETER_ID, incentiveSpirometer.getId());
        values.put(INCENTIVE_SPIROMETER_UUID, incentiveSpirometer.getUuid());

        long result = db.update(TABLE_PATIENT, values, ID + " = ?", new String[] { String.valueOf(patientId) });

        return result != 1;
    }

    /**
     * Checks if a certain incentive spirometer exists
     * @param incentiveSpirometer
     * @return true if exists, false otherwise
     */
    public boolean incentiveSpirometerExists(IncentiveSpirometer incentiveSpirometer) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_INCENTIVE_SPIROMETER + " WHERE " + UUID + " = ?";
        Log.d(TAG, "incentiveSpirometerExists: " + query);

        Cursor c = db.rawQuery(query, new String[]{incentiveSpirometer.getUuid()});

        if (c != null && c.getCount() > 0) {
            return true;
        }
        return false;
    }

    /**
     * Update a given Incentive Spirometer's information
     * @param incentiveSpirometer
     * @return number of rows updated
     */
    public int updateIncentiveSpirometer(IncentiveSpirometer incentiveSpirometer) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NUMBER_OF_INHALATIONS, incentiveSpirometer.getNumberOfInhalations());
        values.put(LUNG_VOLUME, incentiveSpirometer.getLungVolume());

        return db.update(TABLE_INCENTIVE_SPIROMETER, values, ID + " = ?",
                new String[] { String.valueOf(incentiveSpirometer.getId()) });
    }


    // *************************** Dvt table CRUD functions ****************************

    /**
     * Get a patient's DVT data
     * @param patientId patient related to the dvt device
     * @return Dvt object with the patient's device data
     */
    public Dvt getDvt(int patientId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT d.* FROM " + TABLE_PATIENT + " p, " + TABLE_DVT + " d"
                + " WHERE p." + ID + " = ?"
                + " AND p." + DVT_ID + " = d." + ID;

        Cursor c = db.rawQuery(query, new String[]{String.valueOf(patientId)});

        Log.d(TAG, "getDvt: "+ query);

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();

            // create dvt
            Dvt dvt = new Dvt();
            dvt.setId(c.getInt(c.getColumnIndex(ID)));
            dvt.setUuid(c.getString(c.getColumnIndex(UUID)));
            dvt.setResistance(c.getString(c.getColumnIndex(RESISTANCE)));
            dvt.setNumberOfReps(c.getInt(c.getColumnIndex(NUMBER_OF_REPS)));

            return dvt;
        }
        return null;
    }

    /**
     * return the desired data for a given dvt uuid
     * @param uuid id of the dvt device of interest
     * @return the full data of the dvt device of interest
     */
      public Dvt getDvtByUuid(String uuid){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT d.* FROM " + TABLE_DVT + " d"
                + " WHERE d." + UUID + " = ?";

        Cursor c = db.rawQuery(query, new String[]{ uuid });

        Log.d(TAG, "getDvtByUuid: "+ query);

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();

            // create dvt
            Dvt dvt = new Dvt();
            dvt.setId(c.getInt(c.getColumnIndex(ID)));
            dvt.setUuid(c.getString(c.getColumnIndex(UUID)));
            dvt.setResistance(c.getString(c.getColumnIndex(RESISTANCE)));
            dvt.setNumberOfReps(c.getInt(c.getColumnIndex(NUMBER_OF_REPS)));

            return dvt;
        }
        return null;
    }

    /**
     * Insert a new Dvt device
     * @param dvt Dvt object to insert
     * @return -1 if query fails
     */
    public boolean insertDvt(Dvt dvt) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UUID, dvt.getUuid());
        values.put(NUMBER_OF_REPS, dvt.getNumberOfReps());
        values.put(RESISTANCE, dvt.getResistance());

        long result = db.insert(TABLE_DVT, null, values);

        return result != -1;
    }

    /**
     * update the dvt device assigned to a patient
     * @param dvt
     * @param patientId
     * @return
     */
    public boolean updateDvtForPatient(Dvt dvt, int patientId){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DVT_ID, dvt.getId());
        values.put(DVT_UUID, dvt.getUuid());

        long result = db.update(TABLE_PATIENT, values, ID + " = ?", new String[] { String.valueOf(patientId) });

        return result != 1;
    }

    /**
     * Checks if a certain Dvt device exists
     * @param dvt
     * @return true if exists, false otherwise
     */
    public boolean dvtExists(Dvt dvt) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_DVT + " WHERE " + UUID + " = ?";
        Log.d(TAG, "dvtExists: " + query);

        Cursor c = db.rawQuery(query, new String[]{dvt.getUuid()});

        if (c != null && c.getCount() > 0) {
            return true;
        }
        return false;
    }

    /**
     * Update a given DVT device's information
     * @param dvt
     * @return number of rows updated
     */
    public int updateDvt(Dvt dvt) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NUMBER_OF_REPS, dvt.getNumberOfReps());
        values.put(RESISTANCE, dvt.getResistance());

        return db.update(TABLE_DVT, values, ID + " = ?",
                new String[] { String.valueOf(dvt.getId()) });
    }

    /**
     * Delete a DVT device
     * @param dvtId the device to delete
     * @param patientId the patient that has that device
     */
    public void deleteDvtById(int dvtId, int patientId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.putNull(DVT_ID);
        values.putNull(DVT_UUID);

        int updateResult = db.update(TABLE_PATIENT, values, ID + " = ?", new String[] { String.valueOf(patientId) });
        int deleteDataResult = db.delete(TABLE_DVT_DATA, ID + " = ?", new String[] { String.valueOf(dvtId)});
        int deleteDeviceResult = db.delete(TABLE_DVT, ID + " = ?", new String[] { String.valueOf(dvtId)});

    }

    /**
     * finds the desired device (spirometer or dvt device) based on a given Uuid
     * @param uuid
     * @param isSpiro
     * @return
     */
    public int getDeviceIdFromUuid(String uuid, boolean isSpiro){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "";
        if(isSpiro){
            query = "SELECT * FROM " + TABLE_INCENTIVE_SPIROMETER
                    + " WHERE " + UUID + " = ?";
        }
        else {
            query = "SELECT * FROM " + TABLE_DVT
                    + " WHERE " + UUID + " = ?";
        }
        Cursor c = db.rawQuery(query, new String[]{ uuid });

        Log.d(TAG, "getDeviceIdFromUuid: " + query);

        int deviceId = -1;

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();

            deviceId = c.getInt(c.getColumnIndex(ID));
            return deviceId;
        }
        return deviceId;
    }
}

