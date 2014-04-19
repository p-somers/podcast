package com.example.android.podcastmanager;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.podcastmanager.Adapters.EpisodeArrayAdapter;
import com.example.android.podcastmanager.Dialogs.EpisodeDialog;
import com.example.android.podcastmanager.RSS.XMLParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by petersomers on 12/20/13.
 */
public class PodcastViewActivity extends Activity {
    private static final String TAG = PodcastActivity.class.getName();

    public static final int MEDIA_PLAYER = 0;
    public static final int EPISODE_FINISHED = 1;
    public static final int EPISODE_INTERRUPTED = 2;
    public static final int MAX_EPISODES_LOADED = 20;

    private ArrayList<Episode> episodes_arraylist;
    private Button button;
    private Database database;
    private Episode episodes_array[];
    private EpisodeArrayAdapter adapter;
    private ListView list;
    private List<Episode> downloaded_episodes;
    private Podcast podcast;
    private ProgressDialog progressDialog;
    private Spinner display_options;
    private XMLParser parser;
    private int numEpisodesProcessed;
    private long ids[];
    private boolean subscribed;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view);

        Bundle extras = this.getIntent().getExtras();
        episodes_arraylist = new ArrayList<Episode>();
        database = new Database(getApplicationContext());

        podcast = extras.getParcelable("podcast");
        ImageView artwork = (ImageView)findViewById(R.id.artwork);
        Bitmap myBitmap = podcast.getBitmap("600");
        artwork.setImageBitmap(myBitmap);

        button = (Button)findViewById(R.id.subscribe_button);
        subscribed = database.isSubscribedTo(podcast);
        if(subscribed){
            button.setText("Unsubscribe");
            Episode episodes[] = database.getEpisodes(podcast);
            if(episodes != null && episodes.length>0)
                downloaded_episodes = Arrays.asList(episodes);
        } else {
            button.setText("Subscribe");
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.loading_episodes_title);
        if(podcast.getTrackCount() < MAX_EPISODES_LOADED)
            progressDialog.setMax(podcast.getTrackCount());
        else
            progressDialog.setMax(MAX_EPISODES_LOADED);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        numEpisodesProcessed = 0;

        TextView artistName = (TextView)findViewById(R.id.artistName);
        artistName.setText(podcast.getArtistName());

        display_options = (Spinner)findViewById(R.id.display_options);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.display_options,android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        display_options.setAdapter(arrayAdapter);
        display_options.setOnItemSelectedListener(new SpinnerChangeListener());
        display_options.setEnabled(false);

        if(PodcastActivity.internetConnected()){
            parser = new XMLParser(this,true);
            parser.execute(podcast.getFeedUrl());
        } else if(downloaded_episodes != null){
            for(Episode episode: downloaded_episodes)
                episodes_arraylist.add(episode);
            onEpisodeParsingDone();
        }
    }

    private void subscribe() {
        podcast.downloadAllArtworkToFiles();
        database.subscribeTo(podcast);
        database.closeDB();
        button.setText(R.string.unsubscribe);
    }

    private void unsubscribe() {
        int num_removed = database.unsubscribeFrom(podcast.getCollectionId());
        database.closeDB();
        for(Episode e: downloaded_episodes){
            e.deleteLocalFile();
        }
        String text = num_removed + " episode";
        if(num_removed != 1)
            text += "s";
        text += " removed";
        Log.d(TAG,text);
        Toast.makeText(this,text,Toast.LENGTH_LONG);
        button.setText(R.string.subscribe);
        adapter.notifyDataSetChanged();
    }

    public void addEpisode(Episode episode){
        progressDialog.setProgress(numEpisodesProcessed++);
        episode.setParent(podcast);
        if(downloaded_episodes != null && downloaded_episodes.contains(episode)){
            int episode_index = downloaded_episodes.indexOf(episode);
            Episode downloaded = downloaded_episodes.get(episode_index);
            episode.copyLocalInfo(downloaded);
        }
        episodes_arraylist.add(episode);
    }

    public Podcast getPodcast(){
        return podcast;
    }

    public void onEpisodeParsingDone(){
        Collections.sort(episodes_arraylist,new EpisodeComparator());
        Collections.reverse(episodes_arraylist);//otherwise it's sorted with oldest at the top... bad.
        if(episodes_array != null)
            Arrays.fill(episodes_array,null);
        else
            episodes_array = new Episode[episodes_arraylist.size()];
        episodes_array = episodes_arraylist.toArray(episodes_array);

        prepareListView();

        if(progressDialog != null)
          progressDialog.dismiss();
        display_options.setEnabled(true);
    }

    /**
     * Sets up the listview stuff once the episode parsing is done.
     */
    private void prepareListView(){
        adapter = new EpisodeArrayAdapter(this, episodes_array);
        list = (ListView)findViewById(R.id.episodeList);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PodcastViewActivity.this.onItemClick(parent,view,position,id);
            }
        });
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long l) {
                Episode episode = episodes_arraylist.get(position);
                final Dialog dialog = new EpisodeDialog(PodcastViewActivity.this,episode);
                dialog.show();
                return true;
            }
        });
    }

    public void notifyDataSetChanged(){
        adapter.notifyDataSetChanged();
    }

    private void onItemClick(AdapterView<?> parent, View view, int position, long id){
        try{
            Episode episode = episodes_arraylist.get(position);
            if(!episode.isDownloaded()){
                episode.download(view);
            } else {
                int num_downloaded = 0;
                for(Episode e: episodes_array)
                    if(e.isDownloaded() && !e.isFinished())
                        num_downloaded++;
                ids = new long[num_downloaded];
                int i = 0;
                for(Episode e: episodes_array)
                    if(e.isDownloaded() && !e.isFinished()){
                        ids[i] = episodes_array[i].getId();
                        i++;
                    }
                Intent intent = new Intent(PodcastViewActivity.this, MediaPlayerActivity.class);
                intent.putExtra("episode",episode);
                intent.putExtra("position",position);
                intent.putExtra("ids",ids);
                PodcastViewActivity.this.startActivityForResult(intent, MEDIA_PLAYER);
            }
        }catch(Exception e){
            Log.e(TAG,"",e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == MEDIA_PLAYER){
            Bundle extras = data.getExtras();
            Episode episode = extras.getParcelable("episode");
            int position = extras.getInt("position");//position in the array for the listview
            if(resultCode == EPISODE_FINISHED){
                database.markEpisodeFinished(episode.getId());
                episodes_array[position].setField("finished", true);
                episodes_array[position].deleteLocalFile();
                adapter.notifyDataSetChanged();
            } else if(resultCode == EPISODE_INTERRUPTED){
                int playback_position = extras.getInt("playback_position");
                episodes_array[position].setPlaybackPosition(playback_position);
                database.setEpisodePlaybackPosition(episode.getId(),playback_position);
            }
        }
    }

    public class EpisodeComparator implements Comparator<Episode> {
        public int compare(Episode e1, Episode e2){
            return e1.getPubDate().compareTo(e2.getPubDate());
        }
    }

    private class SpinnerChangeListener implements AdapterView.OnItemSelectedListener{

        public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
            if(adapter != null)
                switch(position){
                    case 0: adapter.showUnplayed(); break;
                    case 1: adapter.showDownloaded(); break;
                    case 2: adapter.showFinished(); break;
                    case 3: adapter.showAll(); break;
                }
        }

        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.subscribe_button:
                if(subscribed){
                    unsubscribe();
                } else {
                    subscribe();
                }
                subscribed = !subscribed;
                break;
        }
    }
}
