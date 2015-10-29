package diagnose.uvfree.uvfree;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class PhotoDBHelper extends SQLiteOpenHelper {
    // Database version and name
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "PhotoDB";

    // Database Query Keys
    private static final String TABLE_PHOTOS = "photos";
    private static final String KEY_IDENT = "identifier";
    private static final String KEY_PARENTUID = "parentuid";
    private static final String KEY_BODYLOC = "bodyloc";
    private static final String KEY_IMAGE1 = "firstimage";
    private static final String KEY_IMAGE1POST = "firstimagepost";
    private static final String KEY_IMAGE2 = "secondimage";
    private static final String KEY_IMAGE2POST = "secondimagepost";
    private static final String KEY_TIMEOFLESION = "timeoflesion";
    private static final String KEY_NURSECOMMENTS = "nursecomments";

    // Constructors
    private static final String[] COLUMNS = {KEY_IDENT, KEY_PARENTUID, KEY_BODYLOC, KEY_IMAGE1, KEY_IMAGE1POST, KEY_IMAGE2, KEY_IMAGE2POST, KEY_TIMEOFLESION, KEY_NURSECOMMENTS};

    public PhotoDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Methods
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PHOTOS_TABLE = "CREATE TABLE photos ( " +
                "identifier TEXT, " +
                "parentuid TEXT, " +
                "bodyloc TEXT, " +
                "firstimage TEXT, " +
                "firstimagepost TEXT, " +
                "secondimage TEXT, " +
                "secondimagepost TEXT, " +
                "timeoflesion INT, " +
                "nursecomments TEXT ) ";

        db.execSQL(CREATE_PHOTOS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP  TABLE IF EXISTS photos");
        this.onCreate(db);
    }

    // Add a new photo entry
    public void addPhoto(AbnormPhoto nPhoto) {
        Log.d("addPhoto", nPhoto.toString());

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues val = new ContentValues();

        val.put(KEY_IDENT, nPhoto.getParentUID()+nPhoto.getBodyLocation());
        val.put(KEY_PARENTUID, nPhoto.getParentUID());
        val.put(KEY_BODYLOC, nPhoto.getBodyLocation());
        val.put(KEY_IMAGE1, nPhoto.getImage1());
        val.put(KEY_IMAGE1POST, nPhoto.getImage1post());
        val.put(KEY_IMAGE2, nPhoto.getImage2());
        val.put(KEY_IMAGE2POST, nPhoto.getImage2post());
        val.put(KEY_TIMEOFLESION, nPhoto.getTimeofLesion());
        val.put(KEY_NURSECOMMENTS, nPhoto.getNurseComments());

        db.insert(TABLE_PHOTOS, null, val);
        db.close();
    }

    // Return all photos in the database
    public List<AbnormPhoto> getAllPhotos() {
        List<AbnormPhoto> allPhotos = new LinkedList<AbnormPhoto>();

        String query = "SELECT  * FROM " + TABLE_PHOTOS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        AbnormPhoto nPhoto = null;

        if(cursor.moveToFirst()) {
            do {
                nPhoto = new AbnormPhoto();
                nPhoto.setParentUID(cursor.getString(1));
                nPhoto.setBodyLocation(cursor.getString(2));
                nPhoto.setImage1(cursor.getString(3));
                nPhoto.setImage1post(cursor.getString(4));
                nPhoto.setImage2(cursor.getString(5));
                nPhoto.setImage2post(cursor.getString(6));
                nPhoto.setTimeOfLesion(cursor.getInt(7));
                nPhoto.setNurseComments(cursor.getString(8));

                allPhotos.add(nPhoto);
            } while(cursor.moveToNext());
        }

        return allPhotos;
    }

    // Return all AbnormPhoto objects associated with given parentUID
    public List<AbnormPhoto> getPhoto(String parentUID) {
        List<AbnormPhoto> allPhotos = this.getAllPhotos();

        List<AbnormPhoto> rPhoto = new LinkedList<AbnormPhoto>();

        for(int i = 0; i < allPhotos.size(); i++ ) {
            if(allPhotos.get(i).getParentUID().equals(parentUID)) {
                rPhoto.add(allPhotos.get(i));
            }
        }

        return rPhoto;
    }

    // Update a Photo in the database
    public int updatePhoto(AbnormPhoto uPhoto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues val = new ContentValues();

        val.put(KEY_IDENT, uPhoto.getParentUID()+uPhoto.getBodyLocation());
        val.put(KEY_PARENTUID, uPhoto.getParentUID());
        val.put(KEY_BODYLOC, uPhoto.getBodyLocation());
        val.put(KEY_IMAGE1, uPhoto.getImage1());
        val.put(KEY_IMAGE1POST, uPhoto.getImage1post());
        val.put(KEY_IMAGE2, uPhoto.getImage2());
        val.put(KEY_IMAGE2POST, uPhoto.getImage2post());
        val.put(KEY_TIMEOFLESION, uPhoto.getTimeofLesion());
        val.put(KEY_NURSECOMMENTS, uPhoto.getNurseComments());

        int i = db.update(TABLE_PHOTOS,
                val,
                KEY_IDENT + " = ?",
                new String[]{ uPhoto.getParentUID()+uPhoto.getBodyLocation()});

        return i;
    }

    // Delete a Photo from the database
    public void deletePhoto(AbnormPhoto dPhoto) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_PHOTOS,
                KEY_IDENT + " = ?",
                new String[]{dPhoto.getParentUID()+dPhoto.getBodyLocation()});

        db.close();
    }

}
