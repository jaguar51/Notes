package me.academeg.notes.Control;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import java.util.ArrayList;

import me.academeg.notes.Model.Note;
import me.academeg.notes.R;


public class NotesLinksAdapter extends NotesAdapter {
    private ArrayList<Integer> links;
    private long ID;


    public NotesLinksAdapter(Context context, ArrayList<Note> notes,
                             ArrayList<Integer> links) {
        super(context, notes);
        this.links = links;
    }

    public NotesLinksAdapter(Context context, ArrayList<Note> notes,
                      ArrayList<Integer> link, long id) {
        super(context, notes);
        links = link;
        ID = id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = lInflater.inflate(R.layout.item_note_list_check, parent, false);
        }

        Note note = getItem(position);

        ((TextView)convertView.findViewById(R.id.subjectTv)).setText(cutText(note.getSubject()));
        ((TextView)convertView.findViewById(R.id.textTv)).setText(cutText(note.getText()));

        CheckBox cbBox = ((CheckBox)convertView.findViewById(R.id.cbBox));
        for(int i = 0; i < links.size(); i++) {
            if(note.getId() == links.get(i))
                cbBox.setChecked(true);
        }

        cbBox.setOnCheckedChangeListener(myCheckChangList);
        cbBox.setTag(position);
        return convertView;
    }

    OnCheckedChangeListener myCheckChangList = new OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            int curID = getItem((Integer) buttonView.getTag()).getId();
            if(!isChecked) {
                for (int i = 0; i < links.size(); i++) {
                    if (links.get(i) == curID)
                        links.remove(i);
                }
            }
            else
                links.add(curID);
        }
    };
}
