package com.example.android.podcastapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.podcastapp.Adapters.EpisodeArrayAdapter;
import com.example.android.podcastapp.RSS.XMLParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Created by petersomers on 12/20/13.
 */
public class PodcastViewActivity extends Activity {
    private Podcast podcast;
    private ListView list;
    private ArrayList<Episode> episodes;
    private EpisodeArrayAdapter adapter;
    private Database database;
    private Button button;
    private XMLParser parser;
    private ProgressDialog progressDialog;
    private int numEpisodesProcessed;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view);

        Bundle extras = this.getIntent().getExtras();
        episodes = new ArrayList<Episode>();
        database = new Database(getApplicationContext());

        podcast = extras.getParcelable("podcast");
        ImageView artwork = (ImageView)findViewById(R.id.artwork);
        Bitmap myBitmap = podcast.getBitmap("100");
        artwork.setImageBitmap(myBitmap);

        button = (Button)findViewById(R.id.subscribe_button);
        if(database.isSubscribedTo(podcast)){
            button.setText("Subscribed");
            button.setEnabled(false);
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.loading_episodes_title);
        progressDialog.setMax(podcast.getTrackCount());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        numEpisodesProcessed = 0;

        TextView artistName = (TextView)findViewById(R.id.artistName);
        artistName.setText(podcast.getArtistName());


        parser = new XMLParser(this,true);
        parser.execute(podcast.getFeedUrl());
        try {
            synchronized (parser){
                parser.wait(5000);
            }
        } catch (InterruptedException e) {
            Log.e("test","",e);
        }
    }

    private void subscribe() {
        database.subscribeTo(podcast);
        database.closeDB();
        button.setText(R.string.subscribed);
        button.setEnabled(false);
        podcast.downloadAllArtworkToFiles();
    }

    public void addEpisode(Episode episode){
        progressDialog.setProgress(numEpisodesProcessed++);
        episodes.add(episode);
    }

    public Podcast getPodcast(){
        return podcast;
    }

    public void onEpisodeParsingDone(){
        Collections.sort(episodes,new EpisodeComparator());
        adapter = new EpisodeArrayAdapter(this,episodes);
        list = (ListView)findViewById(R.id.episodeList);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Episode episode = episodes.get(position);

                final Dialog dialog = new Dialog(PodcastViewActivity.this);
                dialog.setContentView(R.layout.episode_view);
                dialog.setTitle(episode.getTitle());

                TextView summary = (TextView)dialog.findViewById(R.id.episode_summary);
                TextView duration = (TextView)dialog.findViewById(R.id.episode_duration);
                TextView pubDate = (TextView)dialog.findViewById(R.id.episode_pubdate);
                summary.setText(episode.getSummary());
                duration.setText(episode.getDuration());
                pubDate.setText(episode.getPubDate().toString());

                Button button = (Button)dialog.findViewById(R.id.episode_view_dialog_button_ok);
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        if(progressDialog != null)
          progressDialog.dismiss();
    }

    public class EpisodeComparator implements Comparator<Episode> {
        public int compare(Episode e1, Episode e2){
            return e1.getPubDate().compareTo(e2.getPubDate());
        }
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.subscribe_button:
                subscribe();
                break;
        }
    }
/*
    @Override
    public void onBackPressed() {
        if(parser != null)
            parser.cancel(true);
        super.onBackPressed();
    }
    */
}
