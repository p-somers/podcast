package com.example.android.podcastapp.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.podcastapp.Episode;
import com.example.android.podcastapp.PodcastActivity;
import com.example.android.podcastapp.PodcastViewActivity;
import com.example.android.podcastapp.R;

/**
 * Created by petersomers on 2/6/14.
 */
public class EpisodeDialog extends Dialog {
    private static final String TAG = PodcastActivity.class.getName();
    private Episode episode;
    private PodcastViewActivity podcastViewActivity;
    public EpisodeDialog(PodcastViewActivity context, Episode e) {
        super(context);
        episode = e;
        podcastViewActivity = context;
        setContentView(R.layout.episode_details_dialog);
        setTitle(episode.getTitle());

        TextView summary = (TextView)findViewById(R.id.episode_summary);
        TextView duration = (TextView)findViewById(R.id.episode_duration);
        TextView pubDate = (TextView)findViewById(R.id.episode_pubdate);
        summary.setText(episode.getSummary());
        duration.setText(episode.getDuration());
        pubDate.setText(episode.getPubDate().toString());

        Button ok_button = (Button)findViewById(R.id.episode_view_dialog_button_ok);
        ok_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dismiss();
            }
        });

        Button delete_button = (Button)findViewById(R.id.episode_view_delete_episode);
        delete_button.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                episode.deleteLocalFile();
                podcastViewActivity.notifyDataSetChanged();
                dismiss();
                return true;
            }
        });
    }
}
