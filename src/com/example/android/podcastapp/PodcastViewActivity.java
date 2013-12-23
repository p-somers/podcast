package com.example.android.podcastapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.example.android.podcastapp.RSS.XMLParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;


/**
 * Created by petersomers on 12/20/13.
 */
public class PodcastViewActivity extends Activity {
    private Podcast podcast;
    private ImageView artwork;
    private ArrayList<Episode> episodes;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view);
        Bundle extras = this.getIntent().getExtras();
        podcast = (Podcast)extras.getParcelable("podcast");
        artwork = (ImageView)findViewById(R.id.artwork);
        Bitmap myBitmap = BitmapFactory.decodeFile(podcast.getArtworkPath("600"));
        artwork.setImageBitmap(myBitmap);
        //Log.d("test","After: "+podcast.allFieldsToString());
        Log.d("test", podcast.allFieldsToString());
        //(new DownloadPodcastTask()).execute("https://itunes.apple.com/us/artist/wbez/id364380278?uo=4");
        XMLParser parser = new XMLParser();
        Log.d("test",podcast.getFeedUrl().toString());
        parser.execute(podcast.getFeedUrl());
    }
    public void addEpisode(Episode episode){
        episodes.add(episode);
    }
    private class DownloadPodcastTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... urls) {
            try {
                InputStream stream = null;
                stream = downloadUrl(urls[0]);
                Scanner scan = new Scanner(stream);
                StringBuilder result = new StringBuilder();
                while( scan.hasNextLine() ) {
                    String line = scan.nextLine();
                    result.append( line );
                }
                Log.d("test",result.toString());
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                //Ignore
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
        }

        // Given a string representation of a URL, sets up a connection and gets
        // an input stream.
        private InputStream downloadUrl(String urlString) throws IOException {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            return conn.getInputStream();
        }
    }
}
