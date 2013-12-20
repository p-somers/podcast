package com.example.android.podcastapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by petersomers on 12/19/13. Based on the tutorial at:
 * www.vogella.com/articles/AndroidListView/article.html
 */
public class SearchResultArrayAdapter extends ArrayAdapter<Podcast> {
    private final Context context;
    private final Podcast[] values;

    public SearchResultArrayAdapter(Context context, Podcast[] values){
        super(context,R.layout.search,values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.resultslist, parent, false);
        TextView title = (TextView)rowView.findViewById(R.id.result_title);
        TextView description = (TextView)rowView.findViewById(R.id.result_description);
        ImageView icon = (ImageView)rowView.findViewById(R.id.result_icon);
        title.setText(values[position].getArtistName());
        description.setText(values[position].getCollectionName());
        try{
        Bitmap myBitmap = BitmapFactory.decodeFile(values[position].getArtworkPath("100"));
        icon.setImageBitmap(myBitmap);
        } catch (Exception ex) {
            Log.e("test","error setting icon",ex);
        }
        return rowView;
    }
}
