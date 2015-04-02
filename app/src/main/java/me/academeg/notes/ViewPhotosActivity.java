package me.academeg.notes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;


public class ViewPhotosActivity extends ActionBarActivity {
    static final int GALLERY_REQUEST = 1;
    static final private String PATCH_PHOTOS = "/sdcard/.notes/";
    static private final String FILE_NAME_PHOTOS = "photos";

    private long noteID;
    private ArrayList<String> thisPhotoId = new ArrayList<String>();
    private ArrayList<Pair<Long, String>> otherPhotoId = new ArrayList<Pair<Long, String>>(); // пары связей

    private ImageAdapter imageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photos);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        noteID = intent.getLongExtra("id", -1);


        /*thisPhotoId.add("1");
        thisPhotoId.add("2");*/

        readPhotosFromFile();

        //Find photoList and set adapter
        GridView photoGridView = (GridView) findViewById(R.id.photoGridView);
        imageAdapter = new ImageAdapter(this, thisPhotoId);
        photoGridView.setAdapter(imageAdapter);
        photoGridView.setOnItemClickListener(gridviewOnItemClickListener);
    }

    private GridView.OnItemClickListener gridviewOnItemClickListener = new GridView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position,
                                long id) {

            Intent in = new Intent();
            in.setAction(Intent.ACTION_VIEW);
            String pathString = "/sdcard/.notes/"+thisPhotoId.get(position);
            File sdPath = new File(PATCH_PHOTOS + thisPhotoId.get(position));
            Uri selectImage = Uri.fromFile(sdPath);
            in.setDataAndType(selectImage, "image/*");
            startActivity(in);
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (resultCode == RESULT_OK) {
            if(requestCode == GALLERY_REQUEST) {
                Bitmap galleryPic = null;
                Uri selectedImage = imageReturnedIntent.getData();
                try {
                    galleryPic = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                String fileName = generateFileName();
                writePhotoToCahce(fileName, galleryPic);
                thisPhotoId.add(fileName);
                writePhotosToFile();
                imageAdapter.notifyDataSetChanged();
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

    private String generateFileName() {
        Time time = new Time();
        time.setToNow();
        //return Long.toString(time.toMillis(false))+".png";
        return Long.toString(time.toMillis(false));
    }


    public void writePhotosToFile() {
        try {
            PrintWriter outputLink = new PrintWriter(openFileOutput(
                    FILE_NAME_PHOTOS, MODE_PRIVATE));

            for (int i = 0; i < otherPhotoId.size(); i++) {
                outputLink.print(otherPhotoId.get(i).first);
                outputLink.print(" ");
                outputLink.println(otherPhotoId.get(i).second);
            }

            for (int i = 0; i < thisPhotoId.size(); i++) {
                outputLink.print(noteID);
                outputLink.print(" ");
                outputLink.println(thisPhotoId.get(i));
            }

            outputLink.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readPhotosFromFile() {
        try {
            thisPhotoId.clear();
            otherPhotoId.clear();

            Scanner inputPhotos = new Scanner(openFileInput(FILE_NAME_PHOTOS));
            while (inputPhotos.hasNext()) {
                long idNote = inputPhotos.nextLong();
                long idPhoto = inputPhotos.nextLong();
                Log.d("testRead", String.valueOf(idNote) + " " + String.valueOf(idPhoto));
                if(idNote == noteID) {
                    thisPhotoId.add(String.valueOf(idPhoto));
                    continue;
                }
                otherPhotoId.add(Pair.create(idNote, String.valueOf(idPhoto)));
            }
            inputPhotos.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writePhotoToCahce(String fileName, Bitmap galleryPic) {
        // Create patch for file
        File sdPath = Environment.getExternalStorageDirectory();
        sdPath = new File(sdPath.getAbsolutePath() + "/" + ".notes");
        sdPath.mkdirs();

        File file = new File(sdPath, fileName);
        try {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                galleryPic.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            } finally {
                if (fos != null) fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Log.d("mLogs", "Запись успешно завершена");
    }

}