package me.academeg.notes.Model;

import android.content.ContentValues;
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
        if (notesDatabaseHelper != null) {
            database.close();
            notesDatabaseHelper.close();
        }
    }

    public Cursor getListNotes() {
        return database.query(
                NotesDatabaseHelper.TABLE_NOTE,
                null, null, null, null, null,
                NotesDatabaseHelper.UID + " DESC"
        );
    }

    public Cursor getListNotes(int noteID) {
        return database.query(
                NotesDatabaseHelper.TABLE_NOTE,
                null,
                NotesDatabaseHelper.UID + " != " + Integer.toString(noteID),
                null, null, null,
                NotesDatabaseHelper.UID + " DESC"
        );
    }

    public void createNote(Note note) {
        ContentValues cv = new ContentValues();
        cv.put(NotesDatabaseHelper.NOTE_TITLE, note.getSubject());
        cv.put(NotesDatabaseHelper.NOTE_TEXT, note.getText());
        database.insert(NotesDatabaseHelper.TABLE_NOTE, null, cv);
    }

    public void updateNote(Note note) {
        ContentValues cv = new ContentValues();
        cv.put(NotesDatabaseHelper.NOTE_TITLE, note.getSubject());
        cv.put(NotesDatabaseHelper.NOTE_TEXT, note.getText());
        database.update(
                NotesDatabaseHelper.TABLE_NOTE,
                cv,
                NotesDatabaseHelper.UID + " = ?",
                new String[] { Integer.toString(note.getId()) }
        );
    }

    public Note getNote(int id) {
        Cursor c = database.query(
                NotesDatabaseHelper.TABLE_NOTE,
                null,
                NotesDatabaseHelper.UID + " = " + Integer.toString(id),
                null, null, null, null
        );

        int idTitle = c.getColumnIndex(NotesDatabaseHelper.NOTE_TITLE);
        int idText = c.getColumnIndex(NotesDatabaseHelper.NOTE_TEXT);
        c.moveToFirst();
        Note res = new Note(id, c.getString(idTitle), c.getString(idText));
        c.close();
        return res;
    }

    public void deleteNote(int id) {
//         delete note
        database.delete(
                NotesDatabaseHelper.TABLE_PHOTO,
                "note" + NotesDatabaseHelper.UID + " = ?",
                new String[]{Integer.toString(id)}
        );

//         delete all photos
        database.delete(
                NotesDatabaseHelper.TABLE_NOTE,
                NotesDatabaseHelper.UID + " = "
                        + Integer.toString(id),
                null
        );
    }

    public Cursor getListPhotos(int noteID) {
        return database.query(
                NotesDatabaseHelper.TABLE_PHOTO, null,
                "note" + NotesDatabaseHelper.UID + " = " + Integer.toString(noteID),
                null, null, null, null
        );
    }

}
