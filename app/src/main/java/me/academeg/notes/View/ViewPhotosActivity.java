package me.academeg.notes.View;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import me.academeg.notes.Control.ImageAdapter;
import me.academeg.notes.Model.NotesDatabaseHelper;
import me.academeg.notes.R;


public class ViewPhotosActivity extends ActionBarActivity {
    private static final int GALLERY_REQUEST = 1;
    private static final String PATCH_PHOTOS = Environment.getExternalStorageDirectory().getPath() + "/.notes/";
    private static final int CM_DELETE = 1;

    private TextView messageTxtView;
    private GridView photoGridView;

    private NotesDatabaseHelper notesDatabase;
    private int noteID;
    private ArrayList<String> thisPhotoName = new ArrayList<String>();
    private ImageAdapter imageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photos);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        noteID = intent.getIntExtra("id", -1);
        notesDatabase = new NotesDatabaseHelper(getApplicationContext());
        getPhotosName();

        messageTxtView = (TextView) findViewById(R.id.infoTxt);
        photoGridView = (GridView) findViewById(R.id.photoGridView);

        //Find photoList and set adapter
        imageAdapter = new ImageAdapter(this, thisPhotoName);
        photoGridView.setAdapter(imageAdapter);
        registerForContextMenu(photoGridView);
        photoGridView.setOnItemClickListener(gridviewOnItemClickListener);

        showInfoMessage();
    }

    private void showInfoMessage() {
        if (imageAdapter.getCount() == 0) {
            messageTxtView.setVisibility(View.VISIBLE);
            photoGridView.setVisibility(View.GONE);
        }
        else {
            messageTxtView.setVisibility(View.GONE);
            photoGridView.setVisibility(View.VISIBLE);
        }
    }

    private void getPhotosName() {
        thisPhotoName.clear();

        SQLiteDatabase sdb = notesDatabase.getReadableDatabase();
        Cursor cursor = sdb.query(
                NotesDatabaseHelper.TABLE_PHOTO,
                null,
                "note" + NotesDatabaseHelper.UID + " = " + Integer.toString(noteID),
                null,
                null,
                null,
                NotesDatabaseHelper.PHOTO_NAME + " DESC"
        );

        int idPhotoName = cursor.getColumnIndex(NotesDatabaseHelper.PHOTO_NAME);
        while (cursor.moveToNext()) {
            thisPhotoName.add(cursor.getString(idPhotoName));
        }

        cursor.close();
        sdb.close();
    }

    private void addNewPhotoToDB(String namePhoto) {
        SQLiteDatabase sdb = notesDatabase.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("note" + NotesDatabaseHelper.UID, noteID);
        cv.put(NotesDatabaseHelper.PHOTO_NAME, namePhoto);
        sdb.insert(NotesDatabaseHelper.TABLE_PHOTO, null, cv);
        sdb.close();
    }

    private void deletePhoto(String namePhoto) {
        SQLiteDatabase sdb = notesDatabase.getWritableDatabase();
        sdb.delete(
                NotesDatabaseHelper.TABLE_PHOTO,
                NotesDatabaseHelper.PHOTO_NAME + " =  ?",
                new String[] { namePhoto }
        );
        sdb.close();
    }

    private GridView.OnItemClickListener gridviewOnItemClickListener = new GridView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position,
                                long id) {

            Intent in = new Intent();
            in.setAction(Intent.ACTION_VIEW);
            File sdPath = new File(PATCH_PHOTOS + thisPhotoName.get(position));
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
                    galleryPic = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), selectedImage);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                String fileName = generateFileName();
                writePhotoToCache(fileName, galleryPic);
                thisPhotoName.add(0, fileName);
                addNewPhotoToDB(fileName);
                imageAdapter.notifyDataSetChanged();

                showInfoMessage();

                return;
            }
        }

    }

    @Override
    public void onBackPressed() {
        // Clear image cache where we close activity
        // If screen was rotated cache will not clean
        imageAdapter.clearCache();
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE, 0, R.string.delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE) {
            AdapterView.AdapterContextMenuInfo acmi =
                    (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

            File deletePhotoFile = new File(PATCH_PHOTOS + thisPhotoName.get(acmi.position));
            deletePhotoFile.delete();
            deletePhoto(thisPhotoName.get(acmi.position));
            thisPhotoName.remove(acmi.position);
            imageAdapter.notifyDataSetChanged();
            showInfoMessage();
            return true;
        }

        return super.onContextItemSelected(item);
    }

    //Generate name for photo, how time
    private String generateFileName() {
        Date dNow = new Date( );
        SimpleDateFormat ft =
                new SimpleDateFormat ("yyyyMMdd_HHmmss");
        return ft.format(dNow);
    }

    private void writePhotoToCache(String fileName, Bitmap galleryPic) {
        // Create patch for file
        File sdPath = Environment.getExternalStorageDirectory();
        sdPath = new File(sdPath.getAbsolutePath() + "/.notes");
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

