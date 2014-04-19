package com.example.android.podcastmanager.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.podcastmanager.Podcast;
import com.example.android.podcastmanager.R;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by petersomers on 12/19/13. Based on the tutorial at:
 * www.vogella.com/articles/AndroidListView/article.html
 */
public class SearchResultArrayAdapter extends ArrayAdapter<Podcast> {
    private final Context context;
    private final Podcast[] values;
    private ImageView icon;

    public SearchResultArrayAdapter(Context context, Podcast[] values){
        super(context, R.layout.search,values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list, parent, false);
        icon = (ImageView)rowView.findViewById(R.id.result_icon);
        icon.setTag(values[position].getArtworkUrl("100"));
        new RetrieveBitmapTask().execute(icon);
        TextView title = (TextView)rowView.findViewById(R.id.result_title);
        TextView description = (TextView)rowView.findViewById(R.id.result_description);
        title.setText(values[position].getArtistName());
        description.setText(values[position].getCollectionName());
        return rowView;
    }

    class RetrieveBitmapTask extends AsyncTask<ImageView, Void, Bitmap> {
        ImageView imageView = null;
        protected Bitmap doInBackground(ImageView... imageViews) {
            this.imageView = imageViews[0];
            try {
                return BitmapFactory.decodeStream(new URL(imageView.getTag().toString()).openStream());//seriously???
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(Bitmap result) {
            try{
                imageView.setImageBitmap(result);
            } catch (Exception ex) {
                Log.e("test","error setting icon",ex);
            }
        }
    }
}
