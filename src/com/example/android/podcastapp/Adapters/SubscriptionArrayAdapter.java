package com.example.android.podcastapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.podcastapp.Podcast;
import com.example.android.podcastapp.R;

/**
 * Created by petersomers on 1/9/14.
 */
public class SubscriptionArrayAdapter extends ArrayAdapter<Podcast> {
    private final Context context;
    private final Podcast[] values;
    private ImageView icon;

    public SubscriptionArrayAdapter(Context context, Podcast[] values){
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
        icon.setImageBitmap(values[position].getBitmap("100"));
        TextView title = (TextView)rowView.findViewById(R.id.result_title);
        TextView description = (TextView)rowView.findViewById(R.id.result_description);
        title.setText(values[position].getArtistName());
        description.setText(values[position].getCollectionName());
        return rowView;
    }
}
