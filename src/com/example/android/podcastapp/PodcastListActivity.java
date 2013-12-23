package com.example.android.podcastapp;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.android.podcastapp.Adapters.SearchResultArrayAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by petersomers on 12/19/13.
 */
public class PodcastListActivity extends ListActivity {
    private Podcast results[];
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);
        progressBar = (ProgressBar)findViewById(R.id.results_list_progress_bar);
        Bundle bundle = this.getIntent().getExtras();
        parseResults(bundle.getString("result"));
    }

    private void parseResults(String json_result){
        try {
            JSONObject json = new JSONObject(json_result);
            JSONArray arr = json.getJSONArray("results");
            int count = arr.length();
            JSONObject objs[] = new JSONObject[count];
            for(int i = 0; i < count; i++)
                objs[i] = arr.getJSONObject(i);
            (new CreatePodcastObjs()).execute(objs);
        } catch (Exception e) {
            Log.e("test", "", e);
        }
    }

    private void updateResults(Podcast podcasts[]) {
        this.results = new Podcast[podcasts.length];
        for(int i = 0; i < results.length; i++)
            this.results[i] = podcasts[i];
        Log.d("test","Result: "+results[0]);
        SearchResultArrayAdapter adapter = new SearchResultArrayAdapter(this, podcasts);
        setListAdapter(adapter);
        ListView list = (ListView)findViewById(android.R.id.list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Podcast podcast = results[position];
                Intent intent = new Intent(getBaseContext(), PodcastViewActivity.class);
                intent.putExtra("podcast",podcast);
                startActivity(intent);
            }
        });
    }

    private void setProgressPercent(int percent){
        progressBar.setProgress(percent);
    }

    /**
     * This is done in an AsyncTask method because otherwise and exception is thrown
     * when it tries to access the network.
     * This was somewhat majorly taken from the asynctask example on
     * developer.android.com
     */
    private class CreatePodcastObjs extends AsyncTask<JSONObject,Integer,Podcast[]>{
        @Override
        protected Podcast[] doInBackground(JSONObject... jsonObjects) {
            JSONParser podcastParser = new JSONParser();
            int count = jsonObjects.length;
            Podcast podcasts[] = new Podcast[count];
            for(int i = 0; i < count; i++){
                try{
                    podcasts[i] = podcastParser.toPodcastObj(jsonObjects[i]);
                } catch(IOException ex) {
                    Log.e("test","",ex);
                } finally {
                    publishProgress((int)(i / (float)count * 100));
                }
            }
            publishProgress(100);
            return podcasts;
        }

        @Override
        protected void onProgressUpdate(Integer... progress){
            setProgressPercent(progress[0]);
        }

        @Override
        protected void onPostExecute(Podcast podcasts[]){
            progressBar.setVisibility(ProgressBar.GONE);
            updateResults(podcasts);
        }
    }
}
