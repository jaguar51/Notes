package me.academeg.notes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

/**
 * Created by Yuriy on 26.02.2015.
 */

public class ViewNoteActivity extends ActionBarActivity {
    private final int REQUEST_CODE_LINK_NOTES = 1;

    private long noteID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //back button in action bar

        Intent intent = getIntent();
        noteID = intent.getLongExtra("id", -1);
        ((EditText) findViewById(R.id.subjectTxt)).setText(intent.getStringExtra("subject"));
        ((EditText) findViewById(R.id.noteTxt)).setText(intent.getStringExtra("text"));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_LINK_NOTES:

                    break;
            }
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            //Log.d("mylog", "back pressed");
            onBackPressed();
            return true;
        }

        if (id == R.id.linkNote) {
            Log.d("myLog", "Добавляем ссылки");
            Intent intent = new Intent(ViewNoteActivity.this, ViewLinkedNoteActivity.class);
            intent.putExtra("id", noteID);
            startActivity(intent);
            //startActivityForResult(intent, REQUEST_CODE_LINK_NOTES);
        }

        return super.onOptionsItemSelected(item);
    }


}