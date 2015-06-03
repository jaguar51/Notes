package me.academeg.notes.Model;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NotesDatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "notes.db";

    public static final String UID = "_id";

    //    Note table
    public static final String TABLE_NOTE = "notes_table";
    public static final String NOTE_TITLE = "title";
    public static final String NOTE_TEXT = "text";
    public static final String SQL_CREATE_NOTE_ENTRIES = "CREATE TABLE " + TABLE_NOTE
            + " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NOTE_TITLE +  " VARCHAR(255), "
            + NOTE_TEXT + " TEXT);";
    public static final String SQL_DELETE_NOTE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NOTE;


    public NotesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DB", "Create tables");
        db.execSQL(SQL_CREATE_NOTE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DB", "Delete tables");
        db.execSQL(SQL_DELETE_NOTE_ENTRIES);
        onCreate(db);
    }

}
