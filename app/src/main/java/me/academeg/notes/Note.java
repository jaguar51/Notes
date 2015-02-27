package me.academeg.notes;

/**
 * Created by Yuriy on 26.02.2015.
 */
public class Note {
    private String subject;
    private String text;
    private long id;

    Note(long _id, String sub, String txt) {
        id = _id;
        subject = sub;
        text = txt;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String sub) {
        subject = sub;
    }

    public String getText() {
        return text;
    }

    public void setText(String txt) {
        text = txt;
    }

    public long getId() {
        return id;
    }

    private void setId(long _id) {
        id = _id;
    }
}
