package me.academeg.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by TM on 26.02.2015.
 */
public class NotesAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<Note> objects;

    NotesAdapter(Context context, ArrayList<Note> notes) {
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
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item_note_list, parent, false);
        }

        Note note = getItem(position);

        ((TextView)view.findViewById(R.id.subjectTv)).setText(note.getSubject());
        ((TextView)view.findViewById(R.id.textTv)).setText(cutText(note.getText()));

        return view;
    }

    private String cutText(String txt) {
        String res = txt.substring(0, (txt.length() < 35 ? txt.length() : 35));
        /*String res = txt;
        if (txt.length() > 37) {
            res = txt.substring(0, 35);
            res += "...";
        }*/
        if (txt.length() >= 35) res += "...";
        return res;
    }
}