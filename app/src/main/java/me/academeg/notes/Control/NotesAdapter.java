package me.academeg.notes.Control;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import me.academeg.notes.Model.NotesDatabaseHelper;
import me.academeg.notes.R;


public class NotesAdapter extends ResourceCursorAdapter {
    private static final int MAX_LENGTH_TEXT = 65;

    private LayoutInflater mInflater;
    private int layout;

    public NotesAdapter(Context context, int layout, Cursor c, int flags) {
        super(context, layout, c, flags);
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout = layout;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView title = (TextView) view.findViewById(R.id.subjectTv);
        title.setText(cursor.getString(cursor.getColumnIndex(NotesDatabaseHelper.NOTE_TITLE)));
        TextView text = (TextView) view.findViewById(R.id.textTv);
        text.setText(cursor.getString(cursor.getColumnIndex(NotesDatabaseHelper.NOTE_TEXT)));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(this.layout, parent, false);
    }


    protected String cutText(String txt) {
        String res = txt.substring(0, (txt.length() < MAX_LENGTH_TEXT ? txt.length() : MAX_LENGTH_TEXT));
        res = res.replace('\n', ' ');
        if (txt.length() >= MAX_LENGTH_TEXT) res += "...";
        return res;
    }
}