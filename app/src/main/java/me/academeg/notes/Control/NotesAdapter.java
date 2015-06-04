package me.academeg.notes.Control;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import me.academeg.notes.Model.Note;
import me.academeg.notes.R;


public class NotesAdapter extends BaseAdapter {
    private static final int MAX_LENGTH_TEXT = 65;

    private Context ctx;
    protected LayoutInflater lInflater;
    protected ArrayList<Note> objects;

    public NotesAdapter(Context context, ArrayList<Note> notes) {
        ctx = context;
        objects = notes;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Note getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = lInflater.inflate(R.layout.item_note_list, parent, false);
        }

        Note note = getItem(position);

        ((TextView)convertView.findViewById(R.id.subjectTv)).setText(cutText(note.getSubject()));
        ((TextView)convertView.findViewById(R.id.textTv)).setText(cutText(note.getText()));

        return convertView;
    }

    protected String cutText(String txt) {
        String res = txt.substring(0, (txt.length() < MAX_LENGTH_TEXT ? txt.length() : MAX_LENGTH_TEXT));
        res = res.replace('\n', ' ');
        if (txt.length() >= MAX_LENGTH_TEXT) res += "...";
        return res;
    }
}