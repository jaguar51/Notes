package me.academeg.notes;


import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;

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

public class ReadWriteFiles {

    static private final String FILE_NAME = "notes";
    static private final String FILE_NAME_LINKS = "links";
    static private final String FILE_NAME_PHOTOS = "photos";

    Context fileContext;

    public ReadWriteFiles(Context context) {
        fileContext = context;
    }

    public void writeNotesToFile(ArrayList<Note> notes) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    fileContext.openFileOutput(FILE_NAME, fileContext.MODE_PRIVATE)
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

    public void readNotesFromFile(ArrayList<Note> notes) {
        try {
            notes.clear();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    fileContext.openFileInput(FILE_NAME)));
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

    public void writeLinksToFile(ArrayList<Long> thisLinks, ArrayList<Pair<Long, Long>> linkNote, long noteID) {
        try {
            PrintWriter outputLink = new PrintWriter(fileContext.openFileOutput(
                    FILE_NAME_LINKS, fileContext.MODE_PRIVATE));

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

    public void readLinksFromFile(ArrayList<Long> thisLinks, ArrayList<Pair<Long, Long>> linkNote, long noteID) {
        try {
            thisLinks.clear();
            linkNote.clear();

            Scanner inputLink = new Scanner(fileContext.openFileInput(FILE_NAME_LINKS));
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

    public void removeLinksFromFile(long noteID) {
        ArrayList<Pair<Long, Long>> linkNote = new ArrayList<Pair<Long, Long>>();

        try {
            linkNote.clear();
            Scanner inputLink = new Scanner(fileContext.openFileInput(FILE_NAME_LINKS));
            while (inputLink.hasNext()) {
                long first = inputLink.nextLong();
                long second = inputLink.nextLong();
                if (first != noteID && second != noteID)
                    linkNote.add(Pair.create(first, second));
            }
            inputLink.close();

            PrintWriter outputLink = new PrintWriter(fileContext.openFileOutput(
                    FILE_NAME_LINKS, fileContext.MODE_PRIVATE));
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
            Scanner inputPhotos = new Scanner(fileContext.openFileInput(FILE_NAME_PHOTOS));
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

            PrintWriter outputLink = new PrintWriter(fileContext.openFileOutput(
                    FILE_NAME_PHOTOS, fileContext.MODE_PRIVATE));

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
