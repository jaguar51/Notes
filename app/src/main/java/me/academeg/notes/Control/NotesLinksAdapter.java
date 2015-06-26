package me.academeg.notes.Control;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import java.util.HashSet;

import me.academeg.notes.Model.NotesDatabaseHelper;
import me.academeg.notes.R;


public class NotesLinksAdapter extends ResourceCursorAdapter {
    private static final int MAX_LENGTH_TEXT = 65;

    private LayoutInflater mInflater;
    private int layout;

    private HashSet<Integer> links;


    public NotesLinksAdapter(Context context, int layout, Cursor c, int flags,
                             HashSet<Integer> link) {
        super(context, layout, c, flags);
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout = layout;
        this.links = link;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(this.layout, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView title = (TextView) view.findViewById(R.id.subjectTv);
        title.setText(cursor.getString(cursor.getColumnIndex(NotesDatabaseHelper.NOTE_TITLE)));
        TextView text = (TextView) view.findViewById(R.id.textTv);
        text.setText(
                cutText(cursor.getString(cursor.getColumnIndex(NotesDatabaseHelper.NOTE_TEXT))));
        CheckBox cbBox = (CheckBox) view.findViewById(R.id.cbBox);
        int idCurNote = cursor.getInt(cursor.getColumnIndex(NotesDatabaseHelper.UID));
        if (links.contains(idCurNote)) {
            cbBox.setChecked(true);
            Log.d("checked", "true");
        }
        cbBox.setOnCheckedChangeListener(myCheckChangList);
        cbBox.setTag(idCurNote);
    }

    CompoundButton.OnCheckedChangeListener myCheckChangList =
            new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            int curID = (Integer) buttonView.getTag();
            if(!isChecked) {
                links.remove(curID);
            } else {
                links.add(curID);
            }
        }
    };

    protected String cutText(String txt) {
        String res =
                txt.substring(0, (txt.length() < MAX_LENGTH_TEXT ? txt.length() : MAX_LENGTH_TEXT));
        res = res.replace('\n', ' ');

        if (txt.length() >= MAX_LENGTH_TEXT)
            res += "...";

        return res;
    }
}
