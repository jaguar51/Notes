package me.academeg.notes;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    private ListView notesList;
    private ArrayList<Note> notes = new ArrayList<Note>();
    private NotesAdapter notesAdapter;

    private final int REQUEST_CODE_EDIT_NOTE = 1;
    private final int REQUEST_CODE_CREATE_NOTE = 2;

    private final int CM_DELET = 1;

    private Note tmpNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notes.add(new Note(1,"1", "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod\ntempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam,\nquis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo\nconsequat. Duis aute irure dolor in reprehenderit in voluptate velit esse\ncillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non\nproident, sunt in culpa qui officia deserunt mollit anim id est laborum."));
        notes.add(new Note(2,"2", "Test 2"));
        notes.add(new Note(3,"3", "Test 3"));
        notes.add(new Note(4,"5", "Test 5"));
        notes.add(new Note(5,"4", "Test 4"));


        notesList = (ListView) findViewById(R.id.notesListView);
        notesAdapter = new NotesAdapter(this, notes);
        registerForContextMenu(notesList);
        notesList.setAdapter(notesAdapter);

        notesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ViewNoteActivity.class);
                tmpNote = notesAdapter.getItem(position);
                intent.putExtra("subject", tmpNote.getSubject());
                intent.putExtra("text", tmpNote.getText());
                startActivityForResult(intent, REQUEST_CODE_EDIT_NOTE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_EDIT_NOTE:
                    tmpNote.setText(data.getStringExtra("text"));
                    tmpNote.setSubject(data.getStringExtra("subject"));
                    Log.d("myLog", "text="+tmpNote.getText()+"  id="+tmpNote.getId());
                    for (int i = 0; i < notes.size(); i++) {
                        if(notes.get(i).getId() == tmpNote.getId()) {
                            notes.get(i).setSubject(tmpNote.getSubject());
                            notes.get(i).setText(tmpNote.getText());
                            break;
                        }
                    }
                    notesAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELET, 0, R.string.deleteNote);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELET) {
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            notes.remove(acmi.position);
            notesAdapter.notifyDataSetChanged();
            return true;
        }
        return super.onContextItemSelected(item);
    }

}
