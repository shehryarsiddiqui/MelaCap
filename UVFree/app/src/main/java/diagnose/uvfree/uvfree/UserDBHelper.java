package diagnose.uvfree.uvfree;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/* UserDBHelper - An extension of the SQLiteOpenHelper class that handles communication and data
                  transfer into and out of the SQLite database used to track the app's registered
                  users, including who is logged in and password authentication. This directly
                  interfaces and is organized based on the User object.
 */

public class UserDBHelper extends SQLiteOpenHelper {
    // Database version and name
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "UsersDB";

    // Database query keys
    private static final String TABLE_USERS = "users";
    private static final String KEY_USER = "username";
    private static final String KEY_PASS = "password";
    private static final String KEY_APIKEY = "apikey";
    private static final String KEY_REDCAPURL = "redcapurl";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_LOGGEDIN = "loggedin";

    // Constructors
    private static final String[] COLUMNS = {KEY_USER, KEY_PASS, KEY_APIKEY, KEY_REDCAPURL, KEY_NAME, KEY_EMAIL, KEY_LOGGEDIN};

    public UserDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Methods
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE users ( " +
                                            "username TEXT, " +
                                            "password TEXT, " +
                                            "apikey TEXT, " +
                                            "redcapurl TEXT, " +
                                            "name TEXT, " +
                                            "email TEXT, " +
                                            "loggedin INT ) ";

        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP  TABLE IF EXISTS users");
        this.onCreate(db);
    }

    // Add a new user
    public void addUser(User nUser) {
        Log.d("addUser", nUser.toString());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues val = new ContentValues();

        val.put(KEY_USER, nUser.getUsername());
        val.put(KEY_PASS, nUser.getPassword());
        val.put(KEY_APIKEY, nUser.getAPIKEY());
        val.put(KEY_REDCAPURL, nUser.getRedcapURL());
        val.put(KEY_NAME, nUser.getName());
        val.put(KEY_EMAIL, nUser.getEmail());
        val.put(KEY_LOGGEDIN, nUser.getLoggedIn());

        db.insert(TABLE_USERS, null, val);
        db.close();
    }

    // Return a User according to Username (should be unique)
    public User getUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =
                db.query(TABLE_USERS,
                        COLUMNS,
                        " username = ?",
                        new String[]{username},
                        null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        User returnUser = new User();
        returnUser.setUsername(cursor.getString(0));
        returnUser.setPassword(cursor.getString(1));
        returnUser.setAPIKEY(cursor.getString(2));
        returnUser.setRedcapURL(cursor.getString(3));
        returnUser.setName(cursor.getString(4));
        returnUser.setEmail(cursor.getString(5));
        returnUser.setLoggedIn(cursor.getInt(6));

        return returnUser;
    }

    // Return all User objects in the database
    public List<User> getAllUsers() {
        List<User> allUsers = new LinkedList<User>();

        String query = "SELECT  * FROM " + TABLE_USERS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        User nUser = null;

        if (cursor.moveToFirst()) {
            do {
                nUser = new User();
                nUser.setUsername(cursor.getString(0));
                nUser.setPassword(cursor.getString(1));
                nUser.setAPIKEY(cursor.getString(2));
                nUser.setRedcapURL(cursor.getString(3));
                nUser.setName(cursor.getString(4));
                nUser.setEmail(cursor.getString(5));
                nUser.setLoggedIn(cursor.getInt(6));

                allUsers.add(nUser);
            } while (cursor.moveToNext());
        }

        return allUsers;
    }

    // Update a User in the database
    public int updateUser(User uUser) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues val = new ContentValues();
        val.put(KEY_USER, uUser.getUsername());
        val.put(KEY_PASS, uUser.getPassword());
        val.put(KEY_APIKEY, uUser.getAPIKEY());
        val.put(KEY_REDCAPURL, uUser.getRedcapURL());
        val.put(KEY_NAME, uUser.getName());
        val.put(KEY_EMAIL, uUser.getEmail());
        val.put(KEY_LOGGEDIN, uUser.getLoggedIn());

        int i = db.update(TABLE_USERS,
                val,
                KEY_USER + " = ?",
                new String[]{uUser.getUsername()});

        return i;
    }

    // Delete a User from the database
    public void deleteUser(User dUser) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_USERS,
                KEY_USER + " = ?",
                new String[]{dUser.getUsername()});

        db.close();
    }

    public User getActiveUser() {
        List<User> allUsers = getAllUsers();
        User targetUser = new User();

        for (int i = 0; i < allUsers.size(); i++) {
            if (allUsers.get(i).getLoggedIn() == 1) {
                targetUser = allUsers.get(i);
                break;
            }
        }
        return targetUser;
    }
}
