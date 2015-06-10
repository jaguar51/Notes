package me.academeg.notes.Control;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import me.academeg.notes.Model.SquaredImageView;


public class ImageAdapter extends BaseAdapter {
    private static final String PATCH_PHOTOS = Environment.getExternalStorageDirectory().getPath() + "/.notes/";

    private Context mContext;
    private ArrayList<String> mThumbIds;

    private static LruCache<String, Bitmap> mMemoryCache = null;

    public ImageAdapter(Context c, ArrayList<String> arrayList) {
        mContext = c;
        mThumbIds = arrayList;

        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;
        if (mMemoryCache == null) {
            mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    // The cache size will be measured in kilobytes rather than
                    // number of items.
                    return bitmap.getByteCount() / 1024;
                }
            };
        }
    }

    public void clearCache() {
        mMemoryCache.evictAll();
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

        SquaredImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new SquaredImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            convertView = imageView;

            holder = new ViewHolder();
            holder.image = imageView;
            holder.position = position;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.position = position;
        }

        // Async load image to imageView
        BitmapLoader task = new BitmapLoader();
        task.execute(holder);
        return convertView;
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth,
                                             int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        return BitmapFactory.decodeFile(path, options);
    }

    public int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    static class ViewHolder {
        SquaredImageView image;
        int position;
    }

    // Async load image to bitmap
    class BitmapLoader extends AsyncTask<ViewHolder, Void, Bitmap> {
        private ViewHolder view;

        @Override
        protected Bitmap doInBackground(ViewHolder... params) {
            view = params[0];
            String bitmapName = mThumbIds.get(view.position);
            Bitmap bitmap = getBitmapFromMemCache(bitmapName);
            if(bitmap == null) {
                bitmap = decodeSampledBitmapFromUri(
                        PATCH_PHOTOS + mThumbIds.get(view.position), 250, 250);
                addBitmapToMemoryCache(bitmapName, bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            view.image.setImageBitmap(bitmap);
        }
    }

}