package me.academeg.notes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;


public class ImageAdapter extends BaseAdapter {
    private static final String PATCH_PHOTOS = Environment.getExternalStorageDirectory().getPath() + "/.notes/";;

    private Context mContext;
    private ArrayList<String> mThumbIds;

    public ImageAdapter(Context c) {
        mContext = c;
        mThumbIds = new ArrayList<String>();
    }

    public ImageAdapter(Context c, ArrayList<String> arrayList) {
        mContext = c;
        mThumbIds = arrayList;
    }

    @Override
    public int getCount() {
        return mThumbIds.size();
    }

    @Override
    public Object getItem(int position) {
        return mThumbIds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(350, 350));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);

            convertView = imageView;

            holder = new ViewHolder();
            holder.image = imageView;
            holder.position = position;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.position = position;
            ((ImageView) convertView).setImageBitmap(null);
        }

        // Async load image to imageview
        new AsyncTask<ViewHolder, Void, Bitmap>() {
            private ViewHolder view;

            @Override
            protected Bitmap doInBackground(ViewHolder... params) {
                view = params[0];
                Bitmap bm = decodeSampledBitmapFromUri(
                        PATCH_PHOTOS + mThumbIds.get(view.position), 250, 250);
                return bm;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                view.image.setImageBitmap(bitmap);
            }
        }.execute(holder);

        return convertView;
    }

    public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth,
                                             int reqHeight) {

        Bitmap bm = null;
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(path, options);

        return bm;
    }

    public int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height
                        / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }

        return inSampleSize;
    }

    class ViewHolder {
        ImageView image;
        int position;
    }

}