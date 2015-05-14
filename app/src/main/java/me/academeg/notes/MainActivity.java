package me.academeg.notes;

import android.content.Intent;
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
import android.widget.AbsListView.MultiChoiceModeListener;
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


public class MainActivity extends ActionBarActivity {

    private ListView notesList;
    private ArrayList<Note> notes = new ArrayList<Note>();
    private NotesAdapter notesAdapter;

    private static final int REQUEST_CODE_EDIT_NOTE = 1;
    private static final int REQUEST_CODE_CREATE_NOTE = 2;

    private static final int CM_DELETE = 1;

    private static final String FILE_NAME = "notes";
    private static final String FILE_NAME_PHOTOS = "photos";
    private static final String FILE_NAME_LINKS = "links";

    private Note tmpNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readNotesFromFile();

        notesList = (ListView) findViewById(R.id.notesListView);
        notesAdapter = new NotesAdapter(this, notes);
        notesList.setAdapter(notesAdapter);
        notesList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        notesList.setMultiChoiceModeListener(new MultiChoiceModeListener() {
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
                selectedItems=new boolean[notes.size()];
                for(int i=0; i<selectedItems.length; i++) selectedItems[i]=false;
                countSelectedItems=0;
                mode.getMenuInflater().inflate(R.menu.context_main, menu);
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (item.getItemId() == R.id.deleteNotes) {
                    for(int index=selectedItems.length-1; index>=0; index--) {
                        if (selectedItems[index]) {
                            removeLinksFromFile(notes.get(index).getId());
                            removePhotosFromFile(notes.get(index).getId());
                            notes.remove(index);
                            writeNotesToFile();
                            notesAdapter.notifyDataSetChanged();
//                            Log.d("Notes", "Note with index " + Integer.toString(index) + "was deleted.");
                        }
                    }
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
                tmpNote = notesAdapter.getItem(position);
                intent.putExtra("id", tmpNote.getId());
                intent.putExtra("subject", tmpNote.getSubject());
                intent.putExtra("text", tmpNote.getText());
                startActivityForResult(intent, REQUEST_CODE_EDIT_NOTE);
            }
        });

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.createNoteButton);
        floatingActionButton.attachToListView(notesList);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long maxId = 0;
                for (int i = 0; i < notes.size(); i++)
                    if (notes.get(i).getId() > maxId)
                        maxId = notes.get(i).getId();
                maxId += 1;
                //Log.d("myLog", String.valueOf(maxId));
                tmpNote = new Note(maxId);
                Intent intent = new Intent(MainActivity.this, ViewNoteActivity.class);
                intent.putExtra("id", tmpNote.getId());
                startActivityForResult(intent, REQUEST_CODE_CREATE_NOTE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if(requestCode == REQUEST_CODE_EDIT_NOTE) {
                tmpNote.setText(data.getStringExtra("text"));
                tmpNote.setSubject(data.getStringExtra("subject"));
                for (int i = 0; i < notes.size(); i++) {
                    if(notes.get(i).getId() == tmpNote.getId()) {
                        notes.get(i).setSubject(tmpNote.getSubject());
                        notes.get(i).setText(tmpNote.getText());
                        break;
                    }
                }
                writeNotesToFile();
                notesAdapter.notifyDataSetChanged();
                return;
            }

            if(requestCode == REQUEST_CODE_CREATE_NOTE) {
                tmpNote.setSubject(data.getStringExtra("subject"));
                tmpNote.setText(data.getStringExtra("text"));
                if (!tmpNote.getText().isEmpty() || !tmpNote.getSubject().isEmpty()) {
                    notes.add(tmpNote);
                    writeNotesToFile();
                    notesAdapter.notifyDataSetChanged();
                }
                return;
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

        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE, 0, R.string.deleteNote);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE) {
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            removeLinksFromFile(notes.get(acmi.position).getId());
            removePhotosFromFile(notes.get(acmi.position).getId());
            notes.remove(acmi.position);
            writeNotesToFile();
            notesAdapter.notifyDataSetChanged();
            return true;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (tmpNote != null) {
            outState.putLong("tmpNoteId", tmpNote.getId());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tmpNote = new Note(savedInstanceState.getLong("tmpNoteId"));
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

    public void writeNotesToFile() {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    openFileOutput(FILE_NAME, MODE_PRIVATE)
            ));

            bw.write("<data>");
            for (int i = 0; i < notes.size(); i++) {
                bw.write("<note>");
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
            //Log.d("sizeNodeList", String.valueOf(nodeLst.getLength()));
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