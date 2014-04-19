package com.example.android.podcastmanager.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.podcastmanager.R;

/**
 * Created by petersomers on 2/6/14.
 */
public class PodcastDialog extends Dialog {
    public PodcastDialog(Context context) {
        super(context);
        setContentView(R.layout.podcast_details_dialog);

        ImageView artwork = (ImageView)findViewById(R.id.podcast_dialog_artwork);
        TextView artistName = (TextView)findViewById(R.id.podcast_dialog_artist_name);
        TextView collectionName = (TextView)findViewById(R.id.podcast_dialog_collection_name);
        TextView primaryGenre = (TextView)findViewById(R.id.podcast_dialog_primary_genre);
        TextView genreList = (TextView)findViewById(R.id.podcast_dialog_genres);


    }
}
