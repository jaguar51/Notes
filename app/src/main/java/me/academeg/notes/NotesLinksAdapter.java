package me.academeg.notes;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;


public class NotesLinksAdapter extends NotesAdapter {
    private ArrayList<Long> links;
    private long ID;

    NotesLinksAdapter(Context context, ArrayList<Note> notes, ArrayList<Long> link, long id) {
        super(context, notes);
        links = link;
        ID = id;
        //links.add(Pair.create((long) 5, (long) 6));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = lInflater.inflate(R.layout.item_note_list_check, parent, false);
        }

        Note note = getItem(position);

        ((TextView)view.findViewById(R.id.subjectTv)).setText(cutText(note.getSubject()));
        ((TextView)view.findViewById(R.id.textTv)).setText(cutText(note.getText()));
        //for ()
        ((CheckBox)view.findViewById(R.id.cbBox)).setChecked(false);

        return view;
    }
}
