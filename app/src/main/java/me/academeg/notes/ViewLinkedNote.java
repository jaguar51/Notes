package me.academeg.notes;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;


public class ViewLinkedNote extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_linked_note);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }



    @Override
    public void onBackPressed() {
        /*Intent intent = new Intent();
        intent.putExtra("subject", ((EditText) findViewById(R.id.subjectTxt)).getText().toString());
        intent.putExtra("text", ((EditText) findViewById(R.id.noteTxt)).getText().toString());
        setResult(RESULT_OK, intent);*/
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Log.d("mylog", "back pressed");
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
