package me.academeg.notes.View;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import me.academeg.notes.Model.NotesDatabaseHelper;
import me.academeg.notes.R;


public class ViewNoteActivity extends ActionBarActivity {
    //private static final int REQUEST_CODE_LINK_NOTES = 1;
    private static final int REQUEST_TAKE_PHOTO = 2;
    private int noteID;
    private NotesDatabaseHelper notesDatabaseHelper;

    private EditText titleEditText;
    private EditText textEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        titleEditText = (EditText) findViewById(R.id.subjectTxt);
        textEditText = (EditText) findViewById(R.id.noteTxt);

        notesDatabaseHelper = new NotesDatabaseHelper(getApplicationContext());

        Intent intent = getIntent();
        noteID = intent.getIntExtra("id", -1);
        getNote();
    }

    private void getNote() {
        if(noteID == -1)
            return;

        SQLiteDatabase sdb = notesDatabaseHelper.getReadableDatabase();
        Cursor c = sdb.query(
                NotesDatabaseHelper.TABLE_NOTE,
                null,
                NotesDatabaseHelper.UID + "=" + Long.toString(noteID),
                null,
                null,
                null,
                null
        );

        int idTitle = c.getColumnIndex(NotesDatabaseHelper.NOTE_TITLE);
        int idText = c.getColumnIndex(NotesDatabaseHelper.NOTE_TEXT);
        c.moveToNext();

        titleEditText.setText(c.getString(idTitle));
        textEditText.setText(c.getString(idText));

        c.close();
        sdb.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {

            if(requestCode==REQUEST_TAKE_PHOTO) {
                Intent intent = new Intent(ViewNoteActivity.this, ViewPhotosActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        boolean changes = false;

        String titleNote = titleEditText.getText().toString();
        String textNote = textEditText.getText().toString();

        SQLiteDatabase sdb = notesDatabaseHelper.getWritableDatabase();
        if(noteID == -1) {

            if (!titleNote.isEmpty() || !textNote.isEmpty()) {
                ContentValues cv = new ContentValues();
                cv.put(NotesDatabaseHelper.NOTE_TITLE, titleNote);
                cv.put(NotesDatabaseHelper.NOTE_TEXT, textNote);
                sdb.insert(NotesDatabaseHelper.TABLE_NOTE, null, cv);

                changes = true;
            }
            sdb.close();
            notesDatabaseHelper.close();
        }
        else {
            ContentValues cv = new ContentValues();
            cv.put(NotesDatabaseHelper.NOTE_TITLE, titleNote);
            cv.put(NotesDatabaseHelper.NOTE_TEXT, textNote);
            sdb.update(
                    NotesDatabaseHelper.TABLE_NOTE,
                    cv,
                    NotesDatabaseHelper.UID + " = ?",
                    new String[] { Integer.toString(noteID) }
            );
            changes = true;
        }

        intent.putExtra("changes", changes);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

//        if (id == R.id.linkNote) {
//            Intent intent = new Intent(ViewNoteActivity.this, ViewLinkedNoteActivity.class);
//            intent.putExtra("id", noteID);
//            startActivity(intent);
//        }
//
        if (id == R.id.addedPhoto) {
            Intent intent = new Intent(ViewNoteActivity.this, ViewPhotosActivity.class);
            intent.putExtra("id", noteID);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

}