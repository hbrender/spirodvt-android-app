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

import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "spirometerDvtApp";
    private static DatabaseHelper staticInstance;
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

    // DoctorPatient table column names
    private static final String DOCTOR_ID = "doctorId";
    private static final String PATIENT_ID = "patientId";

    // Login table column names
    private static final String SALT = "salt";
    private static final String HASHED_PASSWORD = "hashedPassword";

    // IncentiveSpirometerData table create statement
    private static final String CREATE_TABLE_INCENTIVE_SPIROMETER = "CREATE TABLE " + TABLE_INCENTIVE_SPIROMETER + "("
            + ID + " INTEGER PRIMARY KEY, "
            + LUNG_VOLUME + " INTEGER,"
            + NUMBER_OF_INHALATIONS + " INTEGER)";

    // Dvt table create statement
    private static final String CREATE_TABLE_DVT = "CREATE TABLE " + TABLE_DVT + "("
            + ID + " INTEGER PRIMARY KEY, "
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
            + ID + " INTEGER PRIMARY KEY,"
            + USERNAME + " TEXT UNIQUE)";

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
            + " PRIMARY KEY(" + DOCTOR_ID + "," + PATIENT_ID + "),"
            + " FOREIGN KEY(" + DOCTOR_ID + ") REFERENCES " + TABLE_DOCTOR + "(" + ID + "),"
            + " FOREIGN KEY(" + PATIENT_ID + ") REFERENCES " + TABLE_PATIENT + "(" + ID + "))";

    private static final String CREATE_TABLE_LOGIN = "CREATE TABLE " + TABLE_LOGIN + "("
            + ID + " INTEGER PRIMARY KEY,"
            + USERNAME + " TEXT UNIQUE,"
            + SALT + " TEXT,"
            + HASHED_PASSWORD + " TEXT,"
            + "FOREIGN KEY(" + ID + ") REFERENCES " + TABLE_DOCTOR + "(" + ID + "),"
            + "FOREIGN KEY(" + USERNAME + ") REFERENCES " + TABLE_DOCTOR + "(" + USERNAME + "))";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // found out had to implement this so that we wouldnt get the recursive calls to getDatabase
    // http://www.programmersought.com/article/6020366286/ if you want to check it out
    // also why this.defaultDB = db is in both onCreate and onUpgrade.
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
        // creating one user account
        /*Doctor adminUser = new Doctor(1, "admin");
        insertDoctor(adminUser);
        // getting the hashed password for the user
        Authenticate adminAuth = new Authenticate(adminUser.getUsername(), "1234");
        String adminHashedPass = adminAuth.getHashedPassword(adminAuth.getSalt());

        // creating another user account
        Doctor otherUser = new Doctor(2, "user");
        insertDoctor(otherUser);
        // getting hahsed password for the second user;
        Authenticate userAuth = new Authenticate(otherUser.getUsername(), "5678");
        String userHashedPass = userAuth.getHashedPassword(userAuth.getSalt());

        // inserting the users into the login table
        db.execSQL("INSERT INTO " + TABLE_LOGIN + " VALUES(1, 'admin', '" + adminAuth.getSalt() + "', '" +  adminHashedPass + "')");
        db.execSQL("INSERT INTO " + TABLE_LOGIN + " VALUES(2, 'user', '" + userAuth.getSalt() +"', '" +  userHashedPass + "')");*/

        // doctor1 password: 1234
        // doctor2 password: 5678
        db.execSQL("INSERT INTO " + TABLE_LOGIN + " VALUES(1, 'doctor1', '0563267261', '9431a5c2ac7bdecbbeb3b07105428baa109c9682938fbabdcd3ef9c40fea19ec')");
        db.execSQL("INSERT INTO " + TABLE_LOGIN + " VALUES(2, 'doctor2', '7301846435', 'f052a76e75339f7506e39b963e6e2fbb0e3287ed0fada20474ea7e0249238cbd')");

        db.execSQL("INSERT INTO " + TABLE_DOCTOR + " VALUES(1, 'doctor1')");
        db.execSQL("INSERT INTO " + TABLE_DOCTOR + " VALUES(2, 'doctor2')");

        db.execSQL("INSERT INTO " + TABLE_PATIENT + " VALUES(1, 'John', 'Johnson', 5, 10, 145, 76, 'Male', 10, 90)");
        db.execSQL("INSERT INTO " + TABLE_PATIENT + " VALUES(2, 'Lucy', 'Riley', 5, 7, 0, 270, 'Female', 11, 91)");
        db.execSQL("INSERT INTO " + TABLE_PATIENT + " VALUES(3, 'Sean', 'Wilson', 6, 3, 190, 59, 'Other', 12, 92)");
        db.execSQL("INSERT INTO " + TABLE_PATIENT + " VALUES(4, 'Allen', 'Fred', 5, 4, 155, 37, 'Male', 13, 93)");
        db.execSQL("INSERT INTO " + TABLE_PATIENT + " VALUES(5, 'Sammy', 'Martinez', 5, 6, 200, 81, 'Female', 14, 94)");
        db.execSQL("INSERT INTO " + TABLE_PATIENT + " VALUES(6, 'Nicole', 'Meyers', 5, 11, 140, 22, 'Female', 15, 95)");

        db.execSQL("INSERT INTO " + TABLE_DOCTOR_PATIENT + " VALUES(1,1)");
        db.execSQL("INSERT INTO " + TABLE_DOCTOR_PATIENT + " VALUES(1,2)");
        db.execSQL("INSERT INTO " + TABLE_DOCTOR_PATIENT + " VALUES(1,3)");
        db.execSQL("INSERT INTO " + TABLE_DOCTOR_PATIENT + " VALUES(1,4)");
        db.execSQL("INSERT INTO " + TABLE_DOCTOR_PATIENT + " VALUES(2,4)");
        db.execSQL("INSERT INTO " + TABLE_DOCTOR_PATIENT + " VALUES(2,5)");
        db.execSQL("INSERT INTO " + TABLE_DOCTOR_PATIENT + " VALUES(2,6)");
        db.execSQL("INSERT INTO " + TABLE_DOCTOR_PATIENT + " VALUES(2,1)");

        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER + " VALUES(10, 2000, 10)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER + " VALUES(11, 2500, 10)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER + " VALUES(12, 1500, 10)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER + " VALUES(13, 2000, 10)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER + " VALUES(14, 2000, 10)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER + " VALUES(15, 2000, 10)");

        db.execSQL("INSERT INTO " + TABLE_DVT + " VALUES(90, 1, 10)");
        db.execSQL("INSERT INTO " + TABLE_DVT + " VALUES(91, 2, 10)");
        db.execSQL("INSERT INTO " + TABLE_DVT + " VALUES(92, 1, 10)");
        db.execSQL("INSERT INTO " + TABLE_DVT + " VALUES(93, 1, 10)");
        db.execSQL("INSERT INTO " + TABLE_DVT + " VALUES(94, 1, 10)");
        db.execSQL("INSERT INTO " + TABLE_DVT + " VALUES(95, 1, 10)");

        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-8 10:58:00', '2019-11-8 11:57:59', 2000, 10, 10)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-8 11:58:00', '2019-11-8 12:57:59', 2000, 10, 10)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-8 12:58:00', '2019-11-8 13:57:59', 2000, 10, 10)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-8 13:58:00', '2019-11-8 14:57:59', 2000, 10, 10)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(10, '2019-11-8 14:58:00', '2019-11-8 15:57:59', 2000, 10, 10)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(11, '2019-11-8 10:58:00', '2019-11-8 11:57:59', 2500, 10, 9)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(11, '2019-11-8 11:58:00', '2019-11-8 12:57:59', 2500, 10, 8)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(11, '2019-11-8 12:58:00', '2019-11-8 13:57:59', 2500, 10, 7)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(11, '2019-11-8 13:58:00', '2019-11-8 14:57:59', 2500, 10, 6)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(12, '2019-11-8 10:58:00', '2019-11-8 11:57:59', 1500, 10, 5)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(12, '2019-11-8 11:58:00', '2019-11-8 12:57:59', 1500, 10, 7)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(12, '2019-11-8 12:58:00', '2019-11-8 13:57:59', 1500, 10, 9)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(12, '2019-11-8 13:58:00', '2019-11-8 14:57:59', 1500, 10, 10)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(12, '2019-11-8 14:58:00', '2019-11-8 15:57:59', 1500, 10, 10)");
        db.execSQL("INSERT INTO " + TABLE_INCENTIVE_SPIROMETER_DATA + " VALUES(12, '2019-11-8 15:58:00', '2019-11-8 16:57:59', 1500, 10, 10)");

        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-8 10:58:00', '2019-11-8 11:57:59', 1, 10, 10)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-8 11:58:00', '2019-11-8 12:57:59', 1, 10, 10)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-8 12:58:00', '2019-11-8 13:57:59', 1, 10, 10)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-8 13:58:00', '2019-11-8 14:57:59', 2, 10, 10)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-8 14:58:00', '2019-11-8 15:57:59', 2, 10, 10)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(90, '2019-11-8 15:58:00', '2019-11-8 16:57:59', 2, 10, 10)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(91, '2019-11-8 10:58:00', '2019-11-8 11:57:59', 1, 10, 5)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(91, '2019-11-8 11:58:00', '2019-11-8 12:57:59', 1, 10, 5)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(91, '2019-11-8 12:58:00', '2019-11-8 13:57:59', 1, 10, 8)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(91, '2019-11-8 13:58:00', '2019-11-8 14:57:59', 1, 10, 9)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(91, '2019-11-8 14:58:00', '2019-11-8 15:57:59', 1, 10, 10)");
        db.execSQL("INSERT INTO " + TABLE_DVT_DATA + " VALUES(91, '2019-11-8 15:58:00', '2019-11-8 16:57:59', 1, 10, 10)");
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

        return result != -1;
    }

    /**
     * Get all patients that a given doctor has
     * @param doctorId Doctor's ID
     * @return list of patients that the doctor has
     */
    public List<Patient> getAllPatients(int doctorId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT p.* FROM "
                + TABLE_PATIENT + " p, " + TABLE_DOCTOR_PATIENT + " dp"
                + " WHERE dp." + DOCTOR_ID + " = " + doctorId
                + " AND dp." + PATIENT_ID + " = p." + ID;
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

        c.close();
        return patientList;
    }

    public Cursor getAllPatientsCursor(int doctorId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT p.* FROM "
                + TABLE_PATIENT + " p, " + TABLE_DOCTOR_PATIENT + " dp"
                + " WHERE dp." + DOCTOR_ID + " = " + doctorId
                + " AND dp." + PATIENT_ID + " = p." + ID;

        Log.d(TAG, "getAllPatientsCursor: " + query);

        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    /**
     * Get a given patient's data
     * @param patientId patient to read data from
     * @return Patient object with the patient's data
     */
    public Patient getPatient(int patientId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PATIENT + " WHERE " + ID + " = " + patientId;
        Cursor c = db.rawQuery(query, null);

        Log.d(TAG, "getPatient: "+ query);

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

    /**
     * Delete a given patient
     * @param patientId the id matching the patient to delete
     */
    public void deletePatientById(int patientId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG, "deletePatient: " + patientId);
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
        values.put(FIRST_NAME, patient.getFirstName());
        values.put(LAST_NAME, patient.getLastName());
        values.put(HEIGHT_FEET, patient.getHeightFeet());
        values.put(HEIGHT_INCHES, patient.getHeightInches());
        values.put(WEIGHT, patient.getWeight());
        values.put(AGE, patient.getAge());
        values.put(SEX, patient.getSex());
        values.put(INCENTIVE_SPIROMETER_ID, patient.getIncentiveSpirometerId());
        values.put(DVT_ID, patient.getDvtId());

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
     * @param doctor
     * @return result if data was inserted or not *see comment after return statement*
     */
    public boolean insertDoctor(Doctor doctor){

        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(ID, doctor.getId());
            values.put(USERNAME, doctor.getUsername());
            db.insert(TABLE_DOCTOR, null, values);
            db.setTransactionSuccessful();
            return true;
        } catch (SQLiteException e) {
            return false;
            //Error in between database transaction
        } finally {
            db.endTransaction();
        }
    }

    public int getDoctorId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + ID + " FROM " + TABLE_DOCTOR
                + " WHERE " + USERNAME + " = '" + username + "'";
        Cursor c = db.rawQuery(query, null);

        int doctorId = -1;

        if (c.moveToFirst()) {
            do {
                doctorId = c.getInt(c.getColumnIndex(ID));
            } while (c.moveToNext());
        }
        return doctorId;
    }

    // *************************** Login table CRUD functions ****************************

    /**
     * gets the salt and hashed password from the specified username so that authentication can be done
     * @param username
     * @return a string array containing the salt at results[0] and the hashed password at results[1]
     */
    public String[] getLoginInformation(String username) {

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + SALT + ", " + HASHED_PASSWORD + " FROM " + TABLE_LOGIN + " WHERE " + USERNAME + " = '" + username + "';  ";
        Cursor c = db.rawQuery(query, null);

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
        String query = "SELECT COUNT(*) FROM " + TABLE_LOGIN + " WHERE " + USERNAME + " = '" + username + "';";

        try {

            Cursor c = db.rawQuery(query, null);
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

    /**
     * Get a given patient's spirometer data
     * @param patientId patient to read data from
     * @return a list of patient spirometer exercises
     */
    public List<IncentiveSpirometerData> getPatinetSpirometerData(int patientId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Log.d(TAG, "getPatinetSpirometerData: PATIENT ID DATABASE: " + patientId);
        String query = "SELECT isd.* FROM " + TABLE_PATIENT + " p, " + TABLE_INCENTIVE_SPIROMETER_DATA
                + " isd WHERE p." + INCENTIVE_SPIROMETER_ID + " = isd." + ID
                + " AND p." + ID + " = " + patientId;
        Cursor c = db.rawQuery(query, null);

        List<IncentiveSpirometerData> spirometerData = new ArrayList<>();

        Log.d(TAG, "getPatientSpirometer: " + query);

        if (c.moveToFirst()){
            do{
                Log.d(TAG, "getPatinetSpirometerData: THINGS");
                IncentiveSpirometerData spData = new IncentiveSpirometerData();
                spData.setId(c.getInt(c.getColumnIndex(ID)));
                spData.setStartTime(Timestamp.valueOf(c.getString(c.getColumnIndex(START_TIMESTAMP))));
                spData.setEndTime(Timestamp.valueOf(c.getString(c.getColumnIndex(END_TIMESTAMP))));
                spData.setLungVolume(c.getInt(c.getColumnIndex(LUNG_VOLUME)));
                spData.setNumberOfInhalations(c.getInt((c.getColumnIndex(LUNG_VOLUME))));
                spData.setInhalationsCompleted(c.getInt(c.getColumnIndex(INHALATIONS_COMPLETED)));

                spirometerData.add(spData); // you were missing this line
            } while (c.moveToNext());
        }

        c.close();
        return spirometerData;
    }

    public Timestamp convertStringToDate (String st) {
        return Timestamp.valueOf(st);
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
                + " WHERE p." + ID + " = " + patientId
                + " AND p." + INCENTIVE_SPIROMETER_ID + " = i." + ID;

        Cursor c = db.rawQuery(query, null);

        Log.d(TAG, "getIncentiveSpirometer: "+ query);

        if (c != null) {
            c.moveToFirst();

            // create incentive spirometer
            IncentiveSpirometer incentiveSpirometer = new IncentiveSpirometer();
            incentiveSpirometer.setId(c.getInt(c.getColumnIndex(ID)));
            incentiveSpirometer.setLungVolume(c.getInt(c.getColumnIndex(LUNG_VOLUME)));
            incentiveSpirometer.setNumberOfInhalations(c.getInt(c.getColumnIndex(NUMBER_OF_INHALATIONS)));

            return incentiveSpirometer;
        }
        return null;
    }

    // *************************** Incentive Spirometer table CRUD functions ****************************

    /**
     * Get a patient's DVT data
     * @param patientId patient related to the dvt device
     * @return Dvt object with the patient's device data
     */
    public Dvt getDvt(int patientId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT d.* FROM " + TABLE_PATIENT + " p, " + TABLE_DVT + " d"
                + " WHERE p." + ID + " = " + patientId
                + " AND p." + DVT_ID + " = d." + ID;

        Cursor c = db.rawQuery(query, null);

        Log.d(TAG, "getDvt: "+ query);

        if (c != null) {
            c.moveToFirst();

            // create dvt
            Dvt dvt = new Dvt();
            dvt.setId(c.getInt(c.getColumnIndex(ID)));
            dvt.setResistance(c.getString(c.getColumnIndex(RESISTANCE)));
            dvt.setNumberOfReps(c.getInt(c.getColumnIndex(NUMBER_OF_REPS)));

            return dvt;
        }
        return null;
    }


}

