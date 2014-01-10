package com.example.android.podcastapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.podcastapp.Adapters.EpisodeArrayApadter;
import com.example.android.podcastapp.RSS.XMLParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;


/**
 * Created by petersomers on 12/20/13.
 */
public class PodcastViewActivity extends Activity {
    private Podcast podcast;
    private ListView list;
    private ArrayList<Episode> episodes;
    private EpisodeArrayApadter adapter;
    private Database database;
    private Button button;
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


        TextView artistName = (TextView)findViewById(R.id.artistName);
        artistName.setText(podcast.getArtistName());

        adapter = new EpisodeArrayApadter(this,this.episodes);
        list = (ListView)findViewById(R.id.episodeList);
        list.setAdapter(adapter);

        XMLParser parser = new XMLParser(this);
        parser.execute(podcast.getFeedUrl());
        /*
        try {
            synchronized (parser){
                parser.wait(5000);
            }
        } catch (InterruptedException e) {
            Log.e("test","",e);
        }*/
    }
    public void addEpisode(Episode episode){
        episodes.add(episode);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.subscribe_button:
                Log.d("test", "button pressed");
                database.subscribeTo(podcast);
                database.print_all_subscriptions();
                database.closeDB();
                button.setText("Subscribed");
                button.setEnabled(false);
                podcast.downloadAllArtworkToFiles();
                break;
        }
    }
}
