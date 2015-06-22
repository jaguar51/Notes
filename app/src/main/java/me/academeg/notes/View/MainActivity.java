package me.academeg.notes.View;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.melnykov.fab.FloatingActionButton;

import java.io.File;
import java.util.HashSet;

import me.academeg.notes.Control.NotesAdapter;
import me.academeg.notes.Model.NotesDatabase;
import me.academeg.notes.Model.NotesDatabaseHelper;
import me.academeg.notes.R;


public class MainActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private static final String PATCH_PHOTOS = Environment.getExternalStorageDirectory().getPath() + "/.notes/";
    private NotesAdapter notesAdapter;
    private NotesDatabase notesDatabase;

    private static final int REQUEST_CODE_EDIT_NOTE = 1;
    private static final int REQUEST_CODE_CREATE_NOTE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // connect to db
        notesDatabase = new NotesDatabase(this);
        notesDatabase.open();

        notesAdapter = new NotesAdapter(this, R.layout.item_note_list, null, 0);
        ListView notesList = (ListView) findViewById(R.id.notesListView);
        notesList.setAdapter(notesAdapter);

        notesList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        notesList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            private HashSet<Integer> selectedItems;
            private int countSelectedItems;

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                if (checked)
                    selectedItems.add((int) id);
                else
                    selectedItems.remove((int) id);

                countSelectedItems += checked ? 1 : -1;

                mode.setTitle(Integer.toString(countSelectedItems));
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                selectedItems = new HashSet<>();
                countSelectedItems=0;
                mode.getMenuInflater().inflate(R.menu.context_main, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (item.getItemId() == R.id.deleteNotes) {
                    for (int idNote : selectedItems) {
                        deletePhotos(idNote);
                        notesDatabase.deleteNote(idNote);
                    }

                    if (countSelectedItems > 0)
                        getSupportLoaderManager().getLoader(0).forceLoad();

                    mode.finish();
                }
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });


        notesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ViewNoteActivity.class);
                intent.putExtra("id", (int) id);
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

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if ((requestCode == REQUEST_CODE_EDIT_NOTE
                    || requestCode == REQUEST_CODE_CREATE_NOTE)
                    && data.getBooleanExtra("changes", true)) {
                getSupportLoaderManager().getLoader(0).forceLoad();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        notesDatabase.close();
        getSupportLoaderManager().destroyLoader(0);
    }


    public void deletePhotos(int noteID) {
        Cursor cursor = notesDatabase.getListPhotos(noteID);

        int idPhotoName = cursor.getColumnIndex(NotesDatabaseHelper.PHOTO_NAME);
        while (cursor.moveToNext()) {
            String fileName = cursor.getString(idPhotoName);
            File deletePhotoFile = new File(PATCH_PHOTOS + fileName);
            deletePhotoFile.delete();
        }
        cursor.close();
    }

//  Async load notes list
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new NoteCursorLoader(this, notesDatabase);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        notesAdapter.changeCursor(null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        notesAdapter.swapCursor(data);
    }


    static class NoteCursorLoader extends CursorLoader {

        private NotesDatabase ndb;

        public NoteCursorLoader(Context context, NotesDatabase notesDatabase) {
            super(context);
            ndb = notesDatabase;
        }

        @Override
        public Cursor loadInBackground() {
            return ndb.getListNotes();
        }
    }

}