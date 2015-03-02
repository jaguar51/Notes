package me.academeg.notes;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class ViewLinkedNoteActivity extends ActionBarActivity {
    private final String FILE_NAME = "notes";

    private long noteID;
    private ArrayList<Note> notes = new ArrayList<Note>();
    private NotesLinksAdapter notesLinksAdapter;
    private ListView notesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_linked_note);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        noteID = intent.getLongExtra("id", -1);

        readNotesFromFile();

        notesList = (ListView) findViewById(R.id.linkedNotesListView);
        //notesLinksAdapter = new NotesLinksAdapter(this, notes);

        NotesAdapter notesAdapter = new NotesAdapter(this, notes);
        notesList.setAdapter(notesAdapter);

    }


    @Override
    public void onBackPressed() {
        /*Intent intent = new Intent();
        intent.putExtra("subject", ((EditText) findViewById(R.id.subjectTxt)).getText().toString());
        intent.putExtra("text", ((EditText) findViewById(R.id.noteTxt)).getText().toString());
        setResult(RESULT_OK, intent);*/
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
            //Log.d("mylog", "back pressed");
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            //Log.d("sizeNodeList", String.valueOf(nodeLst.getLength()));
            for (int i = 0; i < nodeLst.getLength(); i++) {
                Node fstNode = nodeLst.item(i);
                if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element fstElem = (Element) fstNode;
                    Note tmNote = new Note(Long.parseLong(((Element)(fstElem.getElementsByTagName("id").item(0))).getTextContent()));
                    if(tmNote.getId() == noteID)
                        continue;
                    tmNote.setSubject(((Element) (fstElem.getElementsByTagName("subject").item(0))).getTextContent());
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



    public void readLinksFromFile() {

    }

    public void writeLinksToFile() {

    }
}
