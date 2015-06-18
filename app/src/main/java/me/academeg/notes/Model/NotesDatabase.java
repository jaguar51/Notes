package me.academeg.notes.Model;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NotesDatabase {

    private final Context mContext;
    private NotesDatabaseHelper notesDatabaseHelper;
    private SQLiteDatabase database;


    public NotesDatabase(Context context) {
        this.mContext = context;
    }

    public void open() {
        notesDatabaseHelper = new NotesDatabaseHelper(mContext);
        database = notesDatabaseHelper.getWritableDatabase();
    }

    public void close() {
        if (notesDatabaseHelper != null)
            notesDatabaseHelper.close();
    }

    public Cursor getListNotes() {
        return database.query(
                NotesDatabaseHelper.TABLE_NOTE,
                null, null, null, null, null,
                NotesDatabaseHelper.UID + " DESC"
        );
    }

    public Note getNote(int id) {
        Cursor c = database.query(
                NotesDatabaseHelper.TABLE_NOTE,
                null,
                NotesDatabaseHelper.UID + "=" + Long.toString(id),
                null, null, null, null
        );

        int idTitle = c.getColumnIndex(NotesDatabaseHelper.NOTE_TITLE);
        int idText = c.getColumnIndex(NotesDatabaseHelper.NOTE_TEXT);
        c.moveToNext();
        c.close();
        return new Note(id, c.getString(idTitle), c.getString(idText));
    }



}
