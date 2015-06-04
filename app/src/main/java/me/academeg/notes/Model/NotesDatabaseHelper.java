package me.academeg.notes.Model;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NotesDatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "notes.db";

    public static final String UID = "_id";

    //    Note table
    public static final String TABLE_NOTE = "notes_table";
    public static final String NOTE_TITLE = "title";
    public static final String NOTE_TEXT = "text";
    public static final String SQL_CREATE_NOTE_ENTRIES = "CREATE TABLE " + TABLE_NOTE
            + " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NOTE_TITLE +  " VARCHAR(255), "
            + NOTE_TEXT + " TEXT);";

    //    Photo table
    public static final String TABLE_PHOTO = "photos_table";
    public static final String PHOTO_NAME = "name_photo";
    public static final String SQL_CREATE_PHOTO_ENTRIES = "CREATE TABLE " + TABLE_PHOTO
            + " (note" + UID + " INTEGER, " + PHOTO_NAME +  " CHARACTER(20));";

    //    Link table
    public static final String TABLE_LINK = "links_table";
    public static final String FIRST_ID = "first_id";
    public static final String SECOND_ID = "second_id";
    public static final String SQL_CREATE_LINK_ENTRIES = "CREATE TABLE " + TABLE_LINK
            + " (" + FIRST_ID + " INTEGER, " + SECOND_ID +  " INTEGER);";


    public static final String SQL_DELETE_NOTE_ENTRIES = "DROP TABLE IF EXISTS ";


    public NotesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DB", "Create tables");
        db.execSQL(SQL_CREATE_NOTE_ENTRIES);
        db.execSQL(SQL_CREATE_PHOTO_ENTRIES);
        db.execSQL(SQL_CREATE_LINK_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DB", "Delete tables");
        db.execSQL(SQL_DELETE_NOTE_ENTRIES + TABLE_NOTE);
        db.execSQL(SQL_DELETE_NOTE_ENTRIES + TABLE_PHOTO);
        db.execSQL(SQL_DELETE_NOTE_ENTRIES + TABLE_LINK);
        onCreate(db);
    }

}
