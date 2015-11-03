package diagnose.uvfree.uvfree;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {
    // Database version and name
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "DiagnosticDB";

    // Database query keys
    private static final String TABLE_PATIENTS = "patients";
    private static final String KEY_NURSE = "nurse";
    private static final String KEY_NURSEUSERNAME = "nurseusername";
    private static final String KEY_NURSEEMAIL = "nurseemail";
    private static final String KEY_PATIENT = "patient";
    private static final String KEY_PATIENTDOB = "patientdob";
    private static final String KEY_PATIENTGENDER = "patientgender";
    private static final String KEY_MOTHERSNAME = "mothersname";
    private static final String KEY_CITYNAME = "cityname";
    private static final String KEY_DATE = "date";
    private static final String KEY_TIME = "time";
    private static final String KEY_UNIQUEID = "uniqueid";
    private static final String KEY_UNIQUEPATIENTID = "uniquepatientid";
    private static final String KEY_GPSLAT = "gpslatitude";
    private static final String KEY_GPSLNG = "gpslongitude";
    private static final String KEY_FAMHISTORY = "famhistory";
    private static final String KEY_PERSONALHISTORY = "personalhistory";

    private static final String[] COLUMNS = {KEY_NURSE, KEY_NURSEUSERNAME, KEY_NURSEEMAIL, KEY_PATIENT, KEY_PATIENTDOB, KEY_PATIENTGENDER, KEY_MOTHERSNAME, KEY_CITYNAME, KEY_DATE, KEY_TIME, KEY_UNIQUEID, KEY_UNIQUEPATIENTID, KEY_GPSLAT, KEY_GPSLNG, KEY_FAMHISTORY, KEY_PERSONALHISTORY};

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_DIAG_TABLE = "CREATE TABLE patients ( " +
                "nurse TEXT, " +
                "nurseusername TEXT, " +
                "nurseemail TEXT, " +
                "patient TEXT, " +
                "patientdob TEXT, " +
                "patientgender TEXT, " +
                "mothersname TEXT, " +
                "cityname TEXT, " +
                "date TEXT, " +
                "time TEXT, " +
                "uniqueid TEXT, " +
                "uniquepatientid TEXT, " +
                "gpslatitude DOUBLE, " +
                "gpslongitude DOUBLE, " +
                "famhistory INT, " +
                "personalhistory INT )";

        // Create table for diagnostic instances
        db.execSQL(CREATE_DIAG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS patients");
        this.onCreate(db);
    }

    // Add a "patients" table instance in "DiagnosticDB" database
    public void addDiagInstance(DiagInstance dInst) {
        Log.d("addDiagInstance", dInst.toString());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues val = new ContentValues();

        val.put(KEY_NURSE, dInst.getNurse());
        val.put(KEY_NURSEUSERNAME, dInst.getNurseUsername());
        val.put(KEY_NURSEEMAIL, dInst.getNurseEmail());
        val.put(KEY_PATIENT, dInst.getPatient());
        val.put(KEY_PATIENTDOB, dInst.getPatientDOB());
        val.put(KEY_PATIENTGENDER, dInst.getPatientGender());
        val.put(KEY_MOTHERSNAME, dInst.getMothersName());
        val.put(KEY_CITYNAME, dInst.getCityName());
        val.put(KEY_DATE, dInst.getDate());
        val.put(KEY_TIME, dInst.getTime());
        val.put(KEY_UNIQUEID, dInst.getUID());
        val.put(KEY_UNIQUEPATIENTID, dInst.getPatientUID());
        val.put(KEY_GPSLAT, dInst.getLatitude());
        val.put(KEY_GPSLNG, dInst.getLongitude());
        val.put(KEY_FAMHISTORY, dInst.getFamHistory());
        val.put(KEY_PERSONALHISTORY, dInst.getPersonalHistory());

        db.insert(TABLE_PATIENTS, null, val);

        db.close();
    }

    // Return a DiagInstance according to UniqueID
    public DiagInstance getDiagInstance(String uid) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =
                db.query(TABLE_PATIENTS,
                        COLUMNS,
                        " uniqueid = ?",
                        new String[]{uid},
                        null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        DiagInstance dInst = new DiagInstance();
        dInst.setNurse(cursor.getString(0));
        dInst.setNurseUsername(cursor.getString(1));
        dInst.setNurseEmail(cursor.getString(2));
        dInst.setPatient(cursor.getString(3));
        dInst.setPatientDOB(cursor.getString(4));
        dInst.setPatientGender(cursor.getString(5));
        dInst.setMothersName(cursor.getString(6));
        dInst.setCityName(cursor.getString(7));
        dInst.setDate(cursor.getString(8));
        dInst.setTime(cursor.getString(9));
        dInst.setUID(cursor.getString(10));
        dInst.setPatientUID(cursor.getString(11));
        dInst.setLatitude(cursor.getDouble(12));
        dInst.setLongitude(cursor.getDouble(13));
        dInst.setFamHistory(cursor.getInt(14));
        dInst.setPersonalHistory(cursor.getInt(15));

        return dInst;
    }

    // Return all diagInstances that are in the database
    public List<DiagInstance> getAllDiagInstances() {
        List<DiagInstance> diagInstances = new LinkedList<DiagInstance>();

        String query = "SELECT  * FROM " + TABLE_PATIENTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        DiagInstance dInst = null;
        if (cursor.moveToFirst()) {
            do {
                dInst = new DiagInstance();
                dInst.setNurse(cursor.getString(0));
                dInst.setNurseUsername(cursor.getString(1));
                dInst.setNurseEmail(cursor.getString(2));
                dInst.setPatient(cursor.getString(3));
                dInst.setPatientDOB(cursor.getString(4));
                dInst.setPatientGender(cursor.getString(5));
                dInst.setMothersName(cursor.getString(6));
                dInst.setCityName(cursor.getString(7));
                dInst.setDate(cursor.getString(8));
                dInst.setTime(cursor.getString(9));
                dInst.setUID(cursor.getString(10));
                dInst.setPatientUID(cursor.getString(11));
                dInst.setLatitude(cursor.getDouble(12));
                dInst.setLongitude(cursor.getDouble(13));
                dInst.setFamHistory(cursor.getInt(14));
                dInst.setPersonalHistory(cursor.getInt(15));

                diagInstances.add(dInst);
            } while (cursor.moveToNext());
        }

        return diagInstances;
    }

    // Update a DiagInstance in the database
    public int updateDiagInstance(DiagInstance dInst) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues val = new ContentValues();
        val.put(KEY_NURSE, dInst.getNurse());
        val.put(KEY_NURSEUSERNAME, dInst.getNurseUsername());
        val.put(KEY_NURSEEMAIL, dInst.getNurseEmail());
        val.put(KEY_PATIENT, dInst.getPatient());
        val.put(KEY_PATIENTDOB, dInst.getPatientDOB());
        val.put(KEY_PATIENTGENDER, dInst.getPatientGender());
        val.put(KEY_MOTHERSNAME, dInst.getMothersName());
        val.put(KEY_CITYNAME, dInst.getCityName());
        val.put(KEY_DATE, dInst.getDate());
        val.put(KEY_TIME, dInst.getTime());
        val.put(KEY_UNIQUEPATIENTID, dInst.getPatientUID());
        val.put(KEY_GPSLAT, dInst.getLatitude());
        val.put(KEY_GPSLNG, dInst.getLongitude());
        val.put(KEY_FAMHISTORY, dInst.getFamHistory());
        val.put(KEY_PERSONALHISTORY, dInst.getPersonalHistory());

        int i = db.update(TABLE_PATIENTS,
                val,
                KEY_UNIQUEID + " = ?",
                new String[]{dInst.getUID()});

        return i;
    }

    // Delete a DiagInstance in the database
    public void deleteDiagInstance(DiagInstance dInst) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_PATIENTS,
                KEY_UNIQUEID + " = ?",
                new String[]{dInst.getUID()});

        db.close();
    }
}
