package me.academeg.notes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //back button in action bar
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("subject", ((EditText) findViewById(R.id.subjectTxt)).getText().toString());
        intent.putExtra("text", ((EditText) findViewById(R.id.noteTxt)).getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            Log.d("mylog", "back pressed");
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}