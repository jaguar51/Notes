package me.academeg.notes.View;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.academeg.notes.Control.ImageAdapter;
import me.academeg.notes.Model.NotesDatabase;
import me.academeg.notes.R;


public class ViewPhotosActivity extends AppCompatActivity {
    private static final int GALLERY_REQUEST = 1;
    private static final String PATCH_PHOTOS = Environment.getExternalStorageDirectory().getPath() + "/.notes/";
    private static final int CM_DELETE = 1;

    private TextView messageTxtView;
    private GridView photoGridView;

    private NotesDatabase notesDatabase;
    private int noteID;
    private ImageAdapter imageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photos);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        noteID = intent.getIntExtra("id", -1);

//        Initializing view elements
        messageTxtView = (TextView) findViewById(R.id.infoTxt);
        photoGridView = (GridView) findViewById(R.id.photoGridView);

        FloatingActionButton floatingButton = (FloatingActionButton) findViewById(R.id.addPhoto);
        floatingButton.attachToListView(photoGridView);
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
            }
        });

//        Connect to DB and set this data to photoGridView
        notesDatabase = new NotesDatabase(this);
        notesDatabase.open();

        Cursor imageNameList = notesDatabase.getListPhotos(noteID);
        //Find photoList and set adapter
        imageAdapter = new ImageAdapter(this, imageNameList);
        photoGridView.setAdapter(imageAdapter);
        registerForContextMenu(photoGridView);
        photoGridView.setOnItemClickListener(gridViewOnItemClickListener);

        showInfoMessage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        notesDatabase.close();
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

    private GridView.OnItemClickListener gridViewOnItemClickListener = new GridView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position,
                                long id) {

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            File sdPath = new File(PATCH_PHOTOS + imageAdapter.getItem(position));
            Uri selectImage = Uri.fromFile(sdPath);
            intent.setDataAndType(selectImage, "image/*");
            startActivity(intent);
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (resultCode == RESULT_OK) {
            if(requestCode == GALLERY_REQUEST) {
                Uri selectedImage = imageReturnedIntent.getData();
                new ImageLoaderToCache(noteID, this).execute(selectedImage);
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
//        getMenuInflater().inflate(R.menu.menu_view_photos, menu);
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

            String namePhoto = (String) imageAdapter.getItem(acmi.position);

            File deletePhotoFile = new File(PATCH_PHOTOS + namePhoto);
            deletePhotoFile.delete();
            notesDatabase.deletePhoto(namePhoto);
            imageAdapter.changeCursor(notesDatabase.getListPhotos(noteID));
            showInfoMessage();
            return true;
        }

        return super.onContextItemSelected(item);
    }


    class ImageLoaderToCache extends AsyncTask<Uri, Void, Void> {

        private int noteID;
        private Uri selectedImage;
        private NotesDatabase notesDB;
        private Context mContext;
        private ProgressDialog progressDialog;


        public ImageLoaderToCache(int noteID, Context context) {
            this.noteID = noteID;
            this.mContext = context;
            progressDialog = new ProgressDialog(mContext);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setTitle(R.string.loadImage);
            progressDialog.setMessage(getString(R.string.loadImageMsg));
            progressDialog.show();
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

        private String generateFileName() {
            Date dNow = new Date( );
            SimpleDateFormat ft =
                    new SimpleDateFormat ("yyyyMMdd_HHmmss");
            return ft.format(dNow);
        }

        @Override
        protected Void doInBackground(Uri... params) {
            selectedImage = params[0];

            Bitmap galleryPic = null;
            try {
                galleryPic = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), selectedImage);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            String fileName = generateFileName();
            writePhotoToCache(fileName, galleryPic);

            notesDB = new NotesDatabase(getApplicationContext());
            notesDB.open();
            notesDB.addPhoto(fileName, this.noteID);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            imageAdapter.changeCursor(notesDB.getListPhotos(noteID));
            notesDB.close();
            try {
                showInfoMessage();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            progressDialog.cancel();
        }
    }

}

