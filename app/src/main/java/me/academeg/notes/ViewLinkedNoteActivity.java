package me.academeg.notes;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
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
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class ViewLinkedNoteActivity extends ActionBarActivity {
    private final String FILE_NAME = "notes";
    private final String FILE_NAME_LINKS = "links";

    private long noteID;
    private ArrayList<Note> notes = new ArrayList<Note>();
    private NotesLinksAdapter notesLinksAdapter;
    private ListView notesList;
    private ArrayList<Pair<Long, Long>> linkNote = new ArrayList<Pair<Long, Long>>(); // пары связей
    private ArrayList<Long> thisLinks = new ArrayList<Long>(); // связи для нашей заметки(noteID)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_linked_note);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        noteID = intent.getLongExtra("id", -1);
        Log.d("checkID", String.valueOf(noteID));

        readNotesFromFile();
        readLinksFromFile();

        notesList = (ListView) findViewById(R.id.linkedNotesListView);
        notesLinksAdapter = new NotesLinksAdapter(this, notes, thisLinks, noteID);
        notesList.setAdapter(notesLinksAdapter);

        notesList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewLinkedNoteActivity.this);
                Note tmpNote = notesLinksAdapter.getItem(position);
                builder.setTitle(tmpNote.getSubject());
                builder.setMessage(tmpNote.getText());
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
        try {
            thisLinks.clear();
            linkNote.clear();

            Scanner inputLink = new Scanner(openFileInput(FILE_NAME_LINKS));
            while (inputLink.hasNext()) {
                long first = inputLink.nextLong();
                long second = inputLink.nextLong();
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

            for (int i = 0; i < thisLinks.size(); i++) {
                outputLink.print(thisLinks.get(i));
                outputLink.print(" ");
                outputLink.println(noteID);
            }

            outputLink.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
