package me.academeg.notes;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

/**
 * Created by Yuriy on 26.02.2015.
 */

public class ViewNoteActivity extends ActionBarActivity {
    private final int REQUEST_CODE_LINK_NOTES = 1;
    private static final int REQUEST_TAKE_PHOTO = 2;
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
            if (requestCode == REQUEST_CODE_LINK_NOTES) {

            }

            if(requestCode==REQUEST_TAKE_PHOTO) {
                Log.d("myLog", "Photo gets");
                
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

        if (id == R.id.addPhoto) {
            Intent takePictureIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            takePictureIntent.setType("image/*");
            startActivityForResult(Intent.createChooser(takePictureIntent, getResources().getString(R.string.selectFile)), REQUEST_TAKE_PHOTO);
        }

        return super.onOptionsItemSelected(item);
    }


}