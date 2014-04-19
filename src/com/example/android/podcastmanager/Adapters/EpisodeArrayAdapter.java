package com.example.android.podcastmanager.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.podcastmanager.Episode;
import com.example.android.podcastmanager.PodcastActivity;
import com.example.android.podcastmanager.R;

/**
 * Created by petersomers on 12/22/13.
 */
public class EpisodeArrayAdapter extends ArrayAdapter<Episode> {
    private static final String TAG = PodcastActivity.class.getName();
    private final int DISPLAY_ALL_UNPLAYED = 0;
    private final int DISPLAY_DOWNLOADED = 1;
    private final int DISPLAY_FINISHED = 2;
    private final int DISPLAY_ALL = 3;

    private final Context context;
    private final Episode values[];
    private int state;

    public EpisodeArrayAdapter(Context context, Episode values[]){
        super(context, R.layout.search,values);
        this.context = context;
        this.values = values;
        this.state = 0;
    }

    public void showUnplayed(){
        state = DISPLAY_ALL_UNPLAYED;
        notifyDataSetChanged();
    }
    public void showDownloaded(){
        state = DISPLAY_DOWNLOADED;
        notifyDataSetChanged();
    }
    public void showFinished(){
        state = DISPLAY_FINISHED;
        notifyDataSetChanged();
    }
    public void showAll(){
        state = DISPLAY_ALL;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Episode episode = values[position];
        View rowView;
        boolean displayItem = true;
        switch(state){
            case DISPLAY_ALL_UNPLAYED: displayItem = !episode.isFinished(); break;
            case DISPLAY_DOWNLOADED: displayItem = episode.isDownloaded(); break;
            case DISPLAY_FINISHED: displayItem = episode.isFinished(); break;
            case DISPLAY_ALL: displayItem = true; break;
            default: displayItem = true;
        }
        if(displayItem){
            rowView = inflater.inflate(R.layout.episode_list, parent, false);
            TextView title = (TextView)rowView.findViewById(R.id.list_item_title);
            TextView duration = (TextView)rowView.findViewById(R.id.list_item_duration);
            title.setText(episode.getTitle());
            duration.setText(episode.getDuration());
            if(episode.isDownloaded()){
                int color = context.getResources().getColor(R.color.downloaded_text_color);
                title.setTextColor(color);
            }
        } else
            rowView = inflater.inflate(R.layout.null_item, parent, false);
        return rowView;
    }
}
