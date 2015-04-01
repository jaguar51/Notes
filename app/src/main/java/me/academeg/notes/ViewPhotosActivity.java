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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class ViewPhotosActivity extends ActionBarActivity {
    private long noteID;
    static final int GALLERY_REQUEST = 1;
    Uri selectedImage;
    Bitmap galleryPic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photos);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        noteID = intent.getLongExtra("id", -1);

        //Test write file
        ((Button)findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent in = new Intent();
                in.setAction(Intent.ACTION_VIEW);
                in.setDataAndType(selectedImage, "image/*");
                startActivity(in);*/

                if (!Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    Log.d("mLog", "SD-карта не доступна: " + Environment.getExternalStorageState());
                    return;
                }

                // Create patch for file
                File sdPath = Environment.getExternalStorageDirectory();
                sdPath = new File(sdPath.getAbsolutePath() + "/" + ".notes");
                sdPath.mkdirs();

                //Get time to generate filename
                Time time = new Time();   time.setToNow();
                String fileName = Long.toString(time.toMillis(false))+".png";

                //Write photo to file
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

                Log.d("mLogs", "Запись успешно завершена");

            }
        });
        //Test open file
        ((Button)findViewById(R.id.button2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent();
                in.setAction(Intent.ACTION_VIEW);

                File sdPath = new File("/sdcard/.notes/1.png");
                selectedImage = Uri.fromFile(sdPath);
                Log.d("mLog", selectedImage.getPath());
                in.setDataAndType(selectedImage, "image/*");
                startActivity(in);
                //((ImageView)findViewById(R.id.imageView)).setImageURI(selectedImage);
            }
        });
    }


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