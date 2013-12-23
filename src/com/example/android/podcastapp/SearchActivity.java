package com.example.android.podcastapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by petersomers on 12/17/13.
 */
public class SearchActivity extends Activity {
    private ArrayList<Podcast> results;
    private String result;
    private EditText box;
    private EditText num_results_box;
    private final String url_base = "https://itunes.apple.com/search?term=";
    private String options = "&media=podcast";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        box = (EditText)findViewById(R.id.box);
        num_results_box = (EditText)findViewById(R.id.num_results_box);
        results = new ArrayList<Podcast>();
    }

    public void onClick(View view){
        switch(view.getId()){
            case R.id.search_button:search(box.getText().toString());
        }
    }

    public void search(String terms){
        try{
            terms = terms.replaceAll("\\s","+");
            int num_results = Integer.parseInt(num_results_box.getText().toString());
            options = "&limit="+num_results+options;
            String url = url_base+terms+options;
            results.clear();
            (new DownloadPodcastTask()).execute(url);
        }catch(NumberFormatException ex){
            //Do nothing. This shouldn't happen.
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    // Implementation of AsyncTask used to download the JSON object.
    private class DownloadPodcastTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... urls) {
            try {
                loadJSONFromItunes(urls[0]);
            } catch (IOException e) {
                //Ignore
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            send_results_to_list();
        }

        private void loadJSONFromItunes(String urlString) throws IOException {
            InputStream stream = null;
            stream = downloadUrl(urlString);
            Scanner scan = new Scanner(stream);
            StringBuilder result = new StringBuilder();
            while( scan.hasNextLine() ) {
                String line = scan.nextLine();
                result.append( line );
            }
            SearchActivity.this.result=result.toString();
            if (stream != null) {
                stream.close();
            }
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
    private void send_results_to_list(){
        Intent listActivity = new Intent(getBaseContext(), PodcastListActivity.class);
        listActivity.putExtra("result",this.result);
        startActivity(listActivity);

    }
}
