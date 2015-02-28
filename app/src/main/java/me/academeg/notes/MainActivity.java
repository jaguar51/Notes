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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class MainActivity extends ActionBarActivity {

    private ListView notesList;
    private ArrayList<Note> notes = new ArrayList<Note>();
    private NotesAdapter notesAdapter;

    private final int REQUEST_CODE_EDIT_NOTE = 1;
    private final int REQUEST_CODE_CREATE_NOTE = 2;

    private final int CM_DELET = 1;

    private final String FILE_NAME = "notes";

    private Note tmpNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*notes.add(new Note(1,"1", "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod\ntempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam,\nquis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo\nconsequat. Duis aute irure dolor in reprehenderit in voluptate velit esse\ncillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non\nproident, sunt in culpa qui officia deserunt mollit anim id est laborum."));
        notes.add(new Note(2,"2", "Test 2"));
        notes.add(new Note(3,"3", "Test 3"));
        notes.add(new Note(4,"5", "Test 5"));
        notes.add(new Note(5,"4", "Test 4"));*/

        readNotesFromFile();

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
                    for (int i = 0; i < notes.size(); i++) {
                        if(notes.get(i).getId() == tmpNote.getId()) {
                            if (tmpNote.getText().isEmpty() && tmpNote.getSubject().isEmpty()) {
                                notes.remove(i);
                                notesAdapter.notifyDataSetChanged();
                                break;
                            }
                            notes.get(i).setSubject(tmpNote.getSubject());
                            notes.get(i).setText(tmpNote.getText());
                            break;
                        }
                    }
                    writeNotesToFile();
                    notesAdapter.notifyDataSetChanged();
                    break;

                case REQUEST_CODE_CREATE_NOTE:
                    tmpNote.setSubject(data.getStringExtra("subject"));
                    tmpNote.setText(data.getStringExtra("text"));
                    if (!tmpNote.getText().isEmpty() || !tmpNote.getSubject().isEmpty()) {
                        notes.add(tmpNote);
                        writeNotesToFile();
                        notesAdapter.notifyDataSetChanged();
                    }
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

        if (id == R.id.createNote) {
            long maxId = 0;
            for (int i = 0; i < notes.size(); i++)
                if (notes.get(i).getId() > maxId)
                    maxId = notes.get(i).getId();
            maxId += 1;
            Log.d("myLog", String.valueOf(maxId));
            tmpNote = new Note(maxId);
            Intent intent = new Intent(MainActivity.this, ViewNoteActivity.class);
            startActivityForResult(intent, REQUEST_CODE_CREATE_NOTE);
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
            writeNotesToFile();
            notesAdapter.notifyDataSetChanged();
            return true;
        }

        return super.onContextItemSelected(item);
    }


    public void writeNotesToFile() {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    openFileOutput(FILE_NAME, MODE_PRIVATE)
            ));

            bw.write("<data>");
            for (int i = 0; i < notes.size(); i++) {
                bw.write("<note>");

                /*Log.d("writeToFile", "<id>" + String.valueOf(notes.get(i).getId()) + "</id>");
                Log.d("writeToFile", "<subject>" + notes.get(i).getSubject() + "</subject>");
                Log.d("writeToFile", "<text>" + notes.get(i).getText() + "</text>");*/

                bw.write("<id>" + String.valueOf(notes.get(i).getId()) + "</id>");
                bw.write("<subject>" + notes.get(i).getSubject() + "</subject>");
                bw.write("<text>" + notes.get(i).getText() + "</text>");
                bw.write("</note>");
            }
            bw.write("</data>");

            bw.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readNotesFromFile() {
        try {
            notes.clear();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    openFileInput(FILE_NAME)));
            String file = "";
            String tmp;
            while ((tmp = br.readLine()) != null)
                file += "\n" + tmp;
            br.close();

            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(file));
            Document doc = db.parse(is);

            NodeList nodeLst = doc.getElementsByTagName("note");
            Log.d("sizeNodeList", String.valueOf(nodeLst.getLength()));
            for (int i = 0; i < nodeLst.getLength(); i++) {
                Node fstNode = nodeLst.item(i);
                if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element fstElem = (Element) fstNode;
                    Note tmNote = new Note(Long.parseLong(((Element)(fstElem.getElementsByTagName("id").item(0))).getTextContent()));
                    tmNote.setSubject(((Element)(fstElem.getElementsByTagName("subject").item(0))).getTextContent());
                    tmNote.setText(((Element)(fstElem.getElementsByTagName("text").item(0))).getTextContent());
                    notes.add(tmNote);
                }
            }

        }
        catch (Exception e) {
            Log.d("Error", "reading");
            e.printStackTrace();
        }
    }

}