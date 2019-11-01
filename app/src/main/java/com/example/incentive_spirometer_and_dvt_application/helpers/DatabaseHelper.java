/**
 * Helper class for SQLite Database
 *
 * Source referenced: https://www.androidhive.info/2013/09/android-sqlite-database-with-multiple-tables/
 * @author Hanna Brender
 * @version v1.0 10/23/19
 */

package com.example.incentive_spirometer_and_dvt_application.helpers;

import com.example.incentive_spirometer_and_dvt_application.models.Patient;

import android.content.ContentValues;
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
    private static final String TABLE_LOGIN = "Login";

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

    // Login table column names
    private static final String SALT = "salt";
    private static final String HASHED_PASSWORD = "hashedPassword";

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
            + USERNAME + " TEXT)";

    // Patient table create statement
    private static final String CREATE_TABLE_PATIENT = "CREATE TABLE " + TABLE_PATIENT + "("
            + ID + " INTEGER PRIMARY KEY,"
            + FIRST_NAME + " TEXT,"
            + LAST_NAME + " TEXT,"
            + HEIGHT_FEET + " INTEGER,"
            + HEIGHT_INCHES + " INTEGER,"
            + WEIGHT + " INTEGER,"
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCTOR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PATIENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCTOR_PATIENT);

        // create new tables
        onCreate(db);
    }

    public void insertPatient(Patient patient) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID, patient.getId());
        values.put(FIRST_NAME, patient.getFirstName());
        values.put(LAST_NAME, patient.getLastNames());
        values.put(HEIGHT_FEET, patient.getHeightFeet());
        values.put(HEIGHT_INCHES, patient.getHeightInches());
        values.put(WEIGHT, patient.getWeight());
        values.put(AGE, patient.getAge());
        values.put(SEX, patient.getSex());
        values.put(INCENTIVE_SPIROMETER_ID, java.sql.Types.NULL);
        values.put(DVT_ID, java.sql.Types.NULL);

        db.insert(TABLE_PATIENT, null, values);

        /*int spirometerId = patient.getIncentiveSpirometerId();
        int dvtId = patient.getDvtId();

        if (spirometerId != 0) { // aka null
            String insertSpirometer = "INSERT INTO " + TABLE_INCENTIVE_SPIROMETER
                    + "(" + ID + ") VALUES("
                    + spirometerId + ")";
            db.execSQL(insertSpirometer);
        }

        if (dvtId != 0) { // aka null
            String insertSpirometer = "INSERT INTO " + TABLE_DVT
                    + "(" + ID + ") VALUES("
                    + dvtId + ")";
            db.execSQL(insertSpirometer);
        }

        int patientId = patient.getId();
        String firstName = patient.getFirstName();
        String lastName = patient.getLastNames();
        double heightFeet = patient.getHeightFeet();
        double heightInches = patient.getHeightInches();
        double weight = patient.getWeight();
        int age = patient.getAge();
        String sex = patient.getSex();

        String insertPatient = "INSERT INTO " + TABLE_PATIENT + " VALUES("
                + patientId + ", " + firstName + ", " + lastName + ", " + heightFeet + ","
                + heightInches + ", " + weight + ", " + age + ", " + sex + ", " + spirometerId
                + ", " + dvtId + ")";

        db.execSQL(insertPatient);*/
    }
}
