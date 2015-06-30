package me.academeg.notes.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import me.academeg.notes.Model.Note;
import me.academeg.notes.Model.NotesDatabase;
import me.academeg.notes.R;


public class ViewNoteActivity extends AppCompatActivity {
    private static final int REQUEST_TAKE_PHOTO = 2;
    private int noteID;
    private NotesDatabase notesDatabase;

    private EditText titleEditText;
    private EditText textEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);


        notesDatabase = new NotesDatabase(this);
        notesDatabase.open();

        titleEditText = (EditText) findViewById(R.id.subjectTxt);
        textEditText = (EditText) findViewById(R.id.noteTxt);

        Intent intent = getIntent();
        noteID = intent.getIntExtra("id", -1);
        getNote();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        notesDatabase.close();
    }

    @Override
    public void finish() {
        super.finish();
//      Transition animation out
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
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
        boolean changes = saveNote();
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

        if (id == R.id.linkNote) {
            Intent intent = new Intent(ViewNoteActivity.this, ViewLinkedNoteActivity.class);
            intent.putExtra("id", noteID);
            startActivity(intent);
        }

        if (id == R.id.addedPhoto) {
            Intent intent = new Intent(ViewNoteActivity.this, ViewPhotosActivity.class);
            intent.putExtra("id", noteID);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void getNote() {
        if(noteID == -1)
            return;

        Note tmpNote = notesDatabase.getNote(noteID);

        titleEditText.setText(tmpNote.getSubject());
        textEditText.setText(tmpNote.getText());
    }

    public boolean saveNote() {
        boolean changes = false;

        String titleNote = titleEditText.getText().toString();
        String textNote = textEditText.getText().toString();

        if(noteID == -1) {
            if (!titleNote.isEmpty() || !textNote.isEmpty()) {
                notesDatabase.createNote(new Note(-1, titleNote, textNote));
                changes = true;
            }
        }
        else {
            notesDatabase.updateNote(new Note(noteID, titleNote, textNote));
            changes = true;
        }
        return changes;
    }

}