package me.academeg.notes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class ViewPhotosActivity extends ActionBarActivity {
    private long noteID;
    static final int GALLERY_REQUEST = 1;
    private Uri selectedImage;
    private Bitmap galleryPic;
    private ArrayList<String> ListImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photos);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        noteID = intent.getLongExtra("id", -1);

        ListImage = new ArrayList<String>();

        ListImage.add("/sdcard/.notes/1.png");
        ListImage.add("/sdcard/.notes/2.png");



        GridView gridView = (GridView) findViewById(R.id.gridView1);
        gridView.setAdapter(new ImageAdapter(this, ListImage));
        gridView.setOnItemClickListener(gridviewOnItemClickListener);
        //((GridView) findViewById(R.id.gridView1)).setAdapter(new ImageAdapter(this, ListImage));
    }

    private GridView.OnItemClickListener gridviewOnItemClickListener = new GridView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position,
                                long id) {

            Intent in = new Intent();
            in.setAction(Intent.ACTION_VIEW);
            File sdPath = new File(ListImage.get(position));
            selectedImage = Uri.fromFile(sdPath);
            in.setDataAndType(selectedImage, "image/*");
            startActivity(in);
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (resultCode == RESULT_OK) {
            if(requestCode == GALLERY_REQUEST) {
                galleryPic = null;
                selectedImage = imageReturnedIntent.getData();
                try {
                    galleryPic = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                //myImageView.setImageBitmap(galleryPic);
            }
        }

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (id == R.id.addPhoto) {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
        }

        return super.onOptionsItemSelected(item);
    }

}