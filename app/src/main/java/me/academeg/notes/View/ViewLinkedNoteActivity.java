package me.academeg.notes.View;


import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import me.academeg.notes.Control.NotesLinksAdapter;
import me.academeg.notes.Model.NotesDatabase;
import me.academeg.notes.Model.NotesDatabaseHelper;
import me.academeg.notes.R;


public class ViewLinkedNoteActivity extends ActionBarActivity {
    private static final String FILE_NAME_LINKS = "links";

    private NotesDatabase notesDatabase;
    private int noteID;
    private NotesLinksAdapter notesLinksAdapter;
    private HashSet<Integer> thisLinks = new HashSet<>(); // связи для нашей заметки(noteID)
    private ArrayList<Pair<Integer, Integer>> linkNote = new ArrayList<Pair<Integer, Integer>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_linked_note);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        notesDatabase = new NotesDatabase(this);
        notesDatabase.open();

        Intent intent = getIntent();
        noteID = intent.getIntExtra("id", -1);
        readLinksFromFile();

        ListView notesList = (ListView) findViewById(R.id.linkedNotesListView);
        initAdapter();
        notesList.setAdapter(notesLinksAdapter);

        notesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) notesLinksAdapter.getItem(position);
                String titleNote =
                        cursor.getString(cursor.getColumnIndex(NotesDatabaseHelper.NOTE_TITLE));
                String textNote =
                        cursor.getString(cursor.getColumnIndex(NotesDatabaseHelper.NOTE_TEXT));

                AlertDialog.Builder builder = new AlertDialog.Builder(ViewLinkedNoteActivity.this);
                builder.setTitle(titleNote);
                builder.setMessage(textNote);
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }


    @Override
    public void onBackPressed() {
        writeLinksToFile();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initAdapter() {
        Cursor cursor = notesDatabase.getListNotes(noteID);
        notesLinksAdapter = new NotesLinksAdapter(getBaseContext(), R.layout.item_note_list_check,
                cursor, 0, thisLinks);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        notesDatabase.close();
    }

    public void readLinksFromFile() {
        try {
            thisLinks.clear();
            linkNote.clear();

            Scanner inputLink = new Scanner(openFileInput(FILE_NAME_LINKS));
            while (inputLink.hasNext()) {
                int first = inputLink.nextInt();
                int second = inputLink.nextInt();
                //Log.d("testRead", String.valueOf(first) + " " + String.valueOf(second));
                if(first == noteID) {
                    thisLinks.add(second);
                    continue;
                }
                if(second == noteID) {
                    thisLinks.add(first);
                    continue;
                }
                linkNote.add(Pair.create(first, second));
            }
            inputLink.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeLinksToFile() {
        try {
            PrintWriter outputLink = new PrintWriter(openFileOutput(
                    FILE_NAME_LINKS, MODE_PRIVATE));

            for (int i = 0; i < linkNote.size(); i++) {
                outputLink.print(linkNote.get(i).first);
                outputLink.print(" ");
                outputLink.println(linkNote.get(i).second);
            }

            for (int id : thisLinks) {
                outputLink.print(id);
                outputLink.print(' ');
                outputLink.println(noteID);
            }

            outputLink.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
