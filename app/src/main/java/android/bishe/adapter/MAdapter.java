package android.bishe.adapter;

import android.bishe.R;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Lenovo on 2016/3/3.
 */
public class MAdapter extends BaseAdapter{

    private Context mContext = null;
    public Cursor mCursor = null;
    private LayoutInflater mInflater;

    public MAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
        mInflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        return mCursor.getPosition();
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null){

            convertView=mInflater.inflate(R.layout.cell,null);

            holder=new ViewHolder();
            holder.holderImg= (ImageView) convertView.findViewById(R.id.list_img);
            holder.holderVedioImg= (ImageView) convertView.findViewById(R.id.list_video);
            holder.holderContent= (TextView) convertView.findViewById(R.id.list_content);
            holder.holderTime= (TextView) convertView.findViewById(R.id.list_time);
            holder.holderID= (TextView) convertView.findViewById(R.id.listview_cell_id);
            convertView.setTag(holder);

        }else {
            holder= (ViewHolder) convertView.getTag();
        }

        mCursor.moveToPosition(position);

        String content = mCursor.getString(mCursor.getColumnIndex("content"));
        String time = mCursor.getString(mCursor.getColumnIndex("time"));
        String url = mCursor.getString(mCursor.getColumnIndex("path"));
        String urlvideo = mCursor.getString(mCursor.getColumnIndex("video"));
        String id=mCursor.getString(mCursor.getColumnIndex("_id"));

        holder.holderContent.setText(content);
        holder.holderTime.setText(time);
        holder.holderID.setText(id);
        holder.holderVedioImg.setImageBitmap(getVideoThumbnail(urlvideo, 200, 200,
                MediaStore.Images.Thumbnails.MICRO_KIND));
        holder.holderImg.setImageBitmap(getImageThumbnail(url, 200, 200));
        return convertView;
    }

    public Bitmap getImageThumbnail(String uri, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmap = BitmapFactory.decodeFile(uri, options);
        options.inJustDecodeBounds = false;
        int beWidth = options.outWidth / width;
        int beHeight = options.outHeight / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        bitmap = BitmapFactory.decodeFile(uri, options);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    private Bitmap getVideoThumbnail(String uri, int width, int height, int kind) {
        Bitmap bitmap = null;
        bitmap = ThumbnailUtils.createVideoThumbnail(uri, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

        return bitmap;
    }


    private class ViewHolder{

        public TextView holderID;
        public ImageView holderImg;
        public ImageView holderVedioImg;
        public TextView holderContent;
        public TextView holderTime;
    }

}
