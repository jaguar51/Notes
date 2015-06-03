package me.academeg.notes.View;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.melnykov.fab.FloatingActionButton;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import me.academeg.notes.Model.Note;
import me.academeg.notes.Control.NotesAdapter;
import me.academeg.notes.Model.NotesDatabaseHelper;
import me.academeg.notes.R;


public class MainActivity extends ActionBarActivity {

    private NotesAdapter notesAdapter;
    private ArrayList<Note> noteArrayList = new ArrayList<Note>();

    private NotesDatabaseHelper notesDatabase;

    private static final int REQUEST_CODE_EDIT_NOTE = 1;
    private static final int REQUEST_CODE_CREATE_NOTE = 2;

    private static final String FILE_NAME_PHOTOS = "photos";
    private static final String FILE_NAME_LINKS = "links";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notesDatabase = new NotesDatabaseHelper(this);
        getListNotes();

        final ListView notesList = (ListView) findViewById(R.id.notesListView);
        notesAdapter = new NotesAdapter(this, noteArrayList);
        notesList.setAdapter(notesAdapter);
        notesList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        notesList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            private boolean selectedItems[];
            private int countSelectedItems;

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                selectedItems[position]=checked;
                countSelectedItems += checked ? 1 : -1;
                mode.setTitle(Integer.toString(countSelectedItems));
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                selectedItems=new boolean[noteArrayList.size()];
                for(int i=0; i<selectedItems.length; i++) selectedItems[i]=false;
                countSelectedItems=0;
                mode.getMenuInflater().inflate(R.menu.context_main, menu);
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (item.getItemId() == R.id.deleteNotes) {
                    SQLiteDatabase sdb = notesDatabase.getWritableDatabase();
                    for(int index=selectedItems.length-1; index>=0; index--) {
                        if (selectedItems[index]) {
//                            removeLinksFromFile(noteArrayList.get(index).getId());
//                            removePhotosFromFile(noteArrayList.get(index).getId());
                            sdb.delete(
                                    NotesDatabaseHelper.TABLE_NOTE,
                                    NotesDatabaseHelper.UID + " = "
                                    + Integer.toString(noteArrayList.get(index).getId()),
                                    null
                            );
                            noteArrayList.remove(index);
//                            Log.d("Notes", "Note with index " + Integer.toString(index) + "was deleted.");
                        }
                    }
                    if (countSelectedItems > 0)
                        notesAdapter.notifyDataSetChanged();

                    sdb.close();
                    mode.finish();
                }
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });


        notesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ViewNoteActivity.class);
                Note tmpNote = notesAdapter.getItem(position);
                intent.putExtra("id", tmpNote.getId());
                startActivityForResult(intent, REQUEST_CODE_EDIT_NOTE);
            }
        });

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.createNoteButton);
        floatingActionButton.attachToListView(notesList);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ViewNoteActivity.class);
                startActivityForResult(intent, REQUEST_CODE_CREATE_NOTE);
            }
        });
    }

    private void getListNotes() {
        noteArrayList.clear();

        SQLiteDatabase sdb = notesDatabase.getReadableDatabase();
        Cursor cursor = sdb.query(
                NotesDatabaseHelper.TABLE_NOTE,
                null,
                null,
                null,
                null,
                null,
                NotesDatabaseHelper.UID + " DESC"
        );

        int id = cursor.getColumnIndex(NotesDatabaseHelper.UID);
        int idTitle = cursor.getColumnIndex(NotesDatabaseHelper.NOTE_TITLE);
        int idText = cursor.getColumnIndex(NotesDatabaseHelper.NOTE_TEXT);

        while (cursor.moveToNext()) {
            noteArrayList.add(new Note(
                    cursor.getInt(id),
                    cursor.getString(idTitle),
                    cursor.getString(idText))
            );
        }

        cursor.close();
        sdb.close();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if ((requestCode == REQUEST_CODE_EDIT_NOTE
                    || requestCode == REQUEST_CODE_CREATE_NOTE)
                    && data.getBooleanExtra("changes", true)) {
                getListNotes();
                notesAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v,
//                                    ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        menu.add(0, CM_DELETE, 0, R.string.deleteNote);
//    }

//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        if (item.getItemId() == CM_DELETE) {
//            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//            removeLinksFromFile(noteArrayList.get(acmi.position).getId());
//            removePhotosFromFile(noteArrayList.get(acmi.position).getId());
//            noteArrayList.remove(acmi.position);
////            writeNotesToFile();
//            notesAdapter.notifyDataSetChanged();
//            return true;
//        }
//
//        return super.onContextItemSelected(item);
//    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        if (tmpNote != null) {
//            outState.putInt("tmpNoteId", tmpNote.getId());
//        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle instanceState) {
        super.onRestoreInstanceState(instanceState);
//        tmpNote = new Note(instanceState.getInt("tmpNoteId"));
    }


    public void removeLinksFromFile(long noteID) {
        ArrayList<Pair<Long, Long>> linkNote = new ArrayList<Pair<Long, Long>>();

        try {
            linkNote.clear();
            Scanner inputLink = new Scanner(openFileInput(FILE_NAME_LINKS));
            while (inputLink.hasNext()) {
                long first = inputLink.nextLong();
                long second = inputLink.nextLong();
                if (first != noteID && second != noteID)
                    linkNote.add(Pair.create(first, second));
            }
            inputLink.close();

            PrintWriter outputLink = new PrintWriter(openFileOutput(
                    FILE_NAME_LINKS, MODE_PRIVATE));
            for (int i = 0; i < linkNote.size(); i++) {
                outputLink.print(linkNote.get(i).first);
                outputLink.print(" ");
                outputLink.println(linkNote.get(i).second);
            }
            outputLink.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removePhotosFromFile(long noteID) {
        ArrayList<Pair<Long, String>> photoId = new ArrayList<Pair<Long, String>>();

        try {
            Scanner inputPhotos = new Scanner(openFileInput(FILE_NAME_PHOTOS));
            while (inputPhotos.hasNext()) {
                long idNote = inputPhotos.nextLong();
                String idPhoto = inputPhotos.nextLine();
                idPhoto = idPhoto.trim();
                //Log.d("testRead", String.valueOf(idNote) + " " + String.valueOf(idPhoto));
                if(idNote == noteID) {
                    File deletePhotoFile = new File(Environment.getExternalStorageDirectory().getPath() + "/.notes/" + idPhoto);
                    deletePhotoFile.delete();
                    continue;
                }
                photoId.add(Pair.create(idNote, idPhoto));
            }
            inputPhotos.close();

            PrintWriter outputLink = new PrintWriter(openFileOutput(
                    FILE_NAME_PHOTOS, MODE_PRIVATE));

            for (int i = 0; i < photoId.size(); i++) {
                outputLink.print(photoId.get(i).first);
                outputLink.print(" ");
                outputLink.println(photoId.get(i).second);
            }
            outputLink.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }



}