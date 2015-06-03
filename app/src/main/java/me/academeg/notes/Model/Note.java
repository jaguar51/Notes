package me.academeg.notes.Model;


public class Note {

    private int id;
    private String subject;
    private String text;


    public Note (int id) {
        this.id = id;
        this.subject = "";
        this.text = "";
    }

    public Note(int id, String sub, String txt) {
        this.id = id;
        this.subject = sub;
        this.text = txt;
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

    public int getId() {
        return id;
    }

}