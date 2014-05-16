package somers.pw.podcastmanager;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import somers.pw.podcastmanager.Adapters.EpisodeArrayAdapter;
import somers.pw.podcastmanager.Dialogs.EpisodeDialog;
import somers.pw.podcastmanager.RSS.XMLParser;


/**
 * Created by petersomers on 12/20/13.
 */
public class PodcastViewActivity extends Activity {
    private static final String TAG = PodcastActivity.class.getName();

    public static final int MEDIA_PLAYER = 0;
    public static final int EPISODES_FINISHED = 1;
    public static final int EPISODE_INTERRUPTED = 2;

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
    private int display_option;
    private long ids[];
    private boolean subscribed;
    private boolean episodes_loading;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view);

        Bundle extras = this.getIntent().getExtras();
        episodes_arraylist = new ArrayList<Episode>();
        database = new Database(getApplicationContext());
        display_option = 0;

        podcast = extras.getParcelable("podcast");
        ImageView artwork = (ImageView)findViewById(R.id.artwork);
        Bitmap myBitmap = podcast.getBitmap("600");
        artwork.setImageBitmap(myBitmap);

        button = (Button)findViewById(R.id.subscribe_button);
        subscribed = database.isSubscribedTo(podcast);
        if(subscribed){
            button.setText("Unsubscribe");
            Episode episodes[] = database.getEpisodes(podcast);
            if(episodes != null && episodes.length>0) {
                downloaded_episodes = Arrays.asList(episodes);
            }
        } else {
            button.setText("Subscribe");
        }


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
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(R.string.loading_episodes_title);
            progressDialog.setMax(podcast.getTrackCount());
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel (show downloaded)",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            PodcastViewActivity.this.cancelParsing();
                        }
                    }
            );
            progressDialog.show();
            numEpisodesProcessed = 0;
            episodes_loading = true;
            parser = new XMLParser(this,true);
            parser.execute(podcast.getFeedUrl());
        } else if(downloaded_episodes != null){
            episodes_loading = false;
            for(Episode episode: downloaded_episodes) {
                episodes_arraylist.add(episode);
            }
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
        //episodes_array = episodes_arraylist.toArray(episodes_array);
        prepareListView();
        showUnplayed();

        if(progressDialog != null)
          progressDialog.dismiss();
        display_options.setEnabled(true);
        episodes_loading = false;
    }

    private void cancelParsing(){
        try{
            if(parser != null) {
                parser.cancel(true);
                episodes_arraylist.clear();
                for (Episode episode : downloaded_episodes)
                    episodes_arraylist.add(episode);
                if (episodes_array != null)
                    Arrays.fill(episodes_array, null);
                else
                    episodes_array = new Episode[episodes_arraylist.size()];

                episodes_array = episodes_arraylist.toArray(episodes_array);
                prepareListView();
                showDownloaded();
            }
        }catch(Exception ex){
            Log.e(TAG,"",ex);
        }
    }

    private void showUnplayed(){
        int num_unplayed = 0;
        for(int i = 0; i<episodes_arraylist.size();i++){
            Episode episode = episodes_arraylist.get(i);
            if(!episode.isFinished()){
                num_unplayed++;
            }
        }
        Episode unplayed[] = new Episode[num_unplayed];
        int index = 0;
        for(int i = 0; i<episodes_arraylist.size();i++){
            Episode episode = episodes_arraylist.get(i);
            if(!episode.isFinished()){
                unplayed[index] = episodes_arraylist.get(i);
                index++;
            }
        }
        adapter = new EpisodeArrayAdapter(this, unplayed);
        episodes_array = unplayed;
        list.setAdapter(adapter);
    }
    private void showDownloaded(){
        int num_downloaded = 0;
        for(int i = 0; i<episodes_arraylist.size();i++){
            Episode episode = episodes_arraylist.get(i);
            if(episode.isDownloaded()){
                num_downloaded++;
            }
        }
        Episode downloaded[] = new Episode[num_downloaded];
        int index = 0;
        for(int i = 0; i<episodes_arraylist.size();i++){
            Episode episode = episodes_arraylist.get(i);
            if(episode.isDownloaded()){
                downloaded[index] = episodes_arraylist.get(i);
                index++;
            }
        }
        adapter = new EpisodeArrayAdapter(this, downloaded);
        episodes_array = downloaded;
        list.setAdapter(adapter);
    }
    private void showFinished(){
        int num_played = 0;
        for(int i = 0; i<episodes_arraylist.size();i++){
            Episode episode = episodes_arraylist.get(i);
            if(episode.isFinished()){
                num_played++;
            }
        }
        Episode finished[] = new Episode[num_played];
        int index = 0;
        for(int i = 0; i<episodes_arraylist.size();i++){
            Episode episode = episodes_arraylist.get(i);
            if(episode.isFinished()){
                finished[index] = episodes_arraylist.get(i);
                index++;
            }
        }
        adapter = new EpisodeArrayAdapter(this, finished);
        episodes_array = finished;
        list.setAdapter(adapter);
    }
    private void showAll(){
        episodes_array = episodes_arraylist.toArray(episodes_array);
        adapter = new EpisodeArrayAdapter(this, episodes_array);
        list.setAdapter(adapter);
    }

    public void refresh(){
        switch(display_option){
            case 0: showUnplayed(); break;
            case 1: showDownloaded(); break;
            case 2: showFinished(); break;
            case 3: showAll(); break;
        }
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
                Episode episode = episodes_array[position];
                final Dialog dialog = new EpisodeDialog(PodcastViewActivity.this,episode,position);
                dialog.show();
                return true;
            }
        });
    }

    public void notifyDataSetChanged(){
        adapter.notifyDataSetChanged();
    }

    public void reloadFromDatabase(){
        Episode episodes[] = database.getEpisodes(podcast);
        if(episodes != null && episodes.length>0) {
            downloaded_episodes = Arrays.asList(episodes);
        }
        for(Episode episode: episodes_arraylist){

            if(downloaded_episodes != null && downloaded_episodes.contains(episode)){
                int episode_index = downloaded_episodes.indexOf(episode);
                Episode downloaded = downloaded_episodes.get(episode_index);
                episode.copyLocalInfo(downloaded);
            }
        }
        //Note: from http://stackoverflow.com/a/8276140/1955559
        int index = list.getFirstVisiblePosition();
        View v = list.getChildAt(0);
        int top = (v == null) ? 0 : v.getTop();
        refresh();
        list.setSelectionFromTop(index,top);
    }

    private void onItemClick(AdapterView<?> parent, View view, int position, long id){
        try{
            Episode episode = episodes_array[position];
            if(!episode.isDownloaded()){
                if(DownloadManager.register_download(episode)) {
                    episode.download(view);
                }
                else{
                    Toast.makeText(this,getString(R.string.max_downloads_message),Toast.LENGTH_LONG);
                }
            } else if(!DownloadManager.is_being_downloaded(episode)) {
                int num_downloaded = 0;
                for(Episode e: episodes_array) {
                    if (e.isDownloaded() && !e.isFinished())
                        num_downloaded++;
                }
                ids = new long[num_downloaded];
                int index_in_downloaded_episode_array = 0;
                int i = 0;
                for(Episode e: episodes_array)
                    if(e.isDownloaded() && !e.isFinished()){
                        if(episode.getId() == e.getId())
                            index_in_downloaded_episode_array = i;
                        ids[i] = e.getId();
                        i++;
                    }
                Intent intent = new Intent(PodcastViewActivity.this, MediaPlayerActivity.class);
                intent.putExtra("episode",episode);
                intent.putExtra("position",index_in_downloaded_episode_array);
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
            if(resultCode == EPISODES_FINISHED){
                long finished_episode_ids[] = extras.getLongArray("finised_episodes");
                for(int i = 0; i<finished_episode_ids.length;i++) {
                    database.markEpisodeFinished(finished_episode_ids[i]);
                    for(Episode idCheck: episodes_arraylist){
                        if(idCheck.getId() == finished_episode_ids[i]){
                            idCheck.setField("finished",true);
                        }
                    }
                }
                refresh();
            } else if(resultCode == EPISODE_INTERRUPTED){
            }
            adapter.notifyDataSetChanged();
        }
    }

    public class EpisodeComparator implements Comparator<Episode> {
        public int compare(Episode e1, Episode e2){
            return e1.getPubDate().compareTo(e2.getPubDate());
        }
    }

    private class SpinnerChangeListener implements AdapterView.OnItemSelectedListener{

        public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
            display_option = position;
            if(!episodes_loading) {
                PodcastViewActivity.this.refresh();
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
