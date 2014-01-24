package com.example.android.podcastapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.podcastapp.Episode;
import com.example.android.podcastapp.R;

import java.util.ArrayList;

/**
 * Created by petersomers on 12/22/13.
 */
public class EpisodeArrayAdapter extends ArrayAdapter<Episode> {

    private final Context context;
    private final ArrayList<Episode> values;

    public EpisodeArrayAdapter(Context context, ArrayList<Episode> values){
        super(context, R.layout.search,values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.episode_list, parent, false);
        TextView title = (TextView)rowView.findViewById(R.id.title);
        Episode episode = values.get(position);
        title.setText(episode.getTitle());
        if(episode.isDownloaded()){
            int color = context.getResources().getColor(R.color.downloaded_text_color);
            title.setTextColor(color);
        }
        return rowView;
    }
}
