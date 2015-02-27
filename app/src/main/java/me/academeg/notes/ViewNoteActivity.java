package me.academeg.notes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.EditText;

/**
 * Created by Yuriy on 26.02.2015.
 */

public class ViewNoteActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        Intent intent = getIntent();
        ((EditText) findViewById(R.id.subjectTxt)).setText(intent.getStringExtra("subject"));
        ((EditText) findViewById(R.id.noteTxt)).setText(intent.getStringExtra("text"));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("subject", ((EditText) findViewById(R.id.subjectTxt)).getText().toString());
        intent.putExtra("text", ((EditText) findViewById(R.id.noteTxt)).getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }
}