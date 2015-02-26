package me.academeg.notes;

/**
 * Created by Yuriy on 26.02.2015.
 */
public class Note {
    private String subject;
    private String text;

    Note(String sub, String txt) {
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
}
