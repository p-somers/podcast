package somers.pw.podcastmanager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.FileInputStream;

/**
 * Created by petersomers on 1/21/14.
 */
public class MediaPlayerActivity extends Activity implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener{
    private static final String TAG = PodcastActivity.class.getName();
    private enum State {Idle, Initialized, Preparing, Prepared, Started, Paused, Stopped, PlaybackCompleted, Error}

    private final int SEEKBAR_ANIMATION_STEP_LENGTH = 10;

    private boolean seekBarBeingMoved;
    private Database database;
    private Episode episode;
    private MediaPlayer player;
    private TextView title, summary,
                     time_elapsed, duration;
    private SeekBar playback_bar;
    private ImageView artwork;
    private ImageButton back, forward, play_pause;
    private MyReceiver onBecomeNoisyReceiver;
    private Handler seekBarHandler;
    private long ids[];
    private int episode_array_position;
    private int last_player_position;//in case the user accidentally presses "back"
    private State state;

    private Intent data;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.player);
        data = this.getIntent();
        Bundle extras = data.getExtras();
        Episode episode = extras.getParcelable("episode");
        ids = extras.getLongArray("ids");
        episode_array_position = extras.getInt("position");

        seekBarBeingMoved = false;

        title = (TextView)findViewById(R.id.player_episode_title);
        time_elapsed = (TextView)findViewById(R.id.player_time_elpased);
        duration = (TextView)findViewById(R.id.player_episode_duration);
        playback_bar = (SeekBar)findViewById(R.id.playback_position);
        artwork = (ImageView)findViewById(R.id.player_artwork);
        summary = (TextView)findViewById(R.id.player_episode_summary);
        back = (ImageButton)findViewById(R.id.back_button);
        forward = (ImageButton)findViewById(R.id.forward_button);
        play_pause = (ImageButton)findViewById(R.id.play_pause_button);

        artwork.setImageBitmap(episode.getParent().getBitmap("600"));
        play_pause.setEnabled(false);
        back.setEnabled(true);
        forward.setEnabled(true);
        play_pause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                play_pause();
            }
        });
        back.setOnClickListener(new BackButtonListener());
        back.setOnLongClickListener(new BackButtonLongClickListener());
        forward.setOnClickListener(new ForwardButtonListener());

        database = new Database(getApplicationContext());

        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnErrorListener(this);
        player.setOnCompletionListener(this);
        prepareEpisode(episode);
    }

    @Override
    public void onResume(){
        onBecomeNoisyReceiver = new MyReceiver();
        registerReceiver(onBecomeNoisyReceiver,
                new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));
        super.onResume();
    }

    @Override
    public void onPause(){
        database.setEpisodePlaybackPosition(episode.getId(),player.getCurrentPosition());
        database.closeDB();
        unregisterReceiver(onBecomeNoisyReceiver);
        super.onPause();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(player != null)
            player.release();
        player = null;
    }

    @Override
    public void onBackPressed(){
        data.putExtra("playback_position",player.getCurrentPosition());
        setResult(PodcastViewActivity.EPISODE_INTERRUPTED, data);
        finish();
    }

    private void prepareEpisode(Episode _episode){
        this.episode = _episode;
        title.setText(episode.getTitle());
        summary.setText(episode.getSummary());
        try {
            FileInputStream inputStream = new FileInputStream(episode.getLocalFile());
            player.reset();
            player.setDataSource(inputStream.getFD());
            inputStream.close();
            player.prepareAsync();
            state = State.Preparing;
        } catch (Exception e) {
            Log.e(TAG,"",e);
        }
    }

    /**
     * This function connects the media player and the SeekBar.
     */
    private void preparePlaybackBar(){
        playback_bar.setMax(episode.getDurationInMilliseconds());
        playback_bar.setProgress(0);
        playback_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser && player != null){
                    time_elapsed.setText(millisecondsToTimeElapsed(progress));
                    player.seekTo(progress);
                }
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBarBeingMoved = true;
                if(player != null)
                    player.pause();
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBarBeingMoved = false;
                if(player != null)
                    player.start();
            }
        });
        seekBarHandler = new Handler();
        Runnable moveSeekBar = new Runnable() {
            public void run() {
                if(player != null){
                    int pos = player.getCurrentPosition();
                    playback_bar.setProgress(pos);
                    time_elapsed.setText(millisecondsToTimeElapsed(pos));
                    seekBarHandler.postDelayed(this,SEEKBAR_ANIMATION_STEP_LENGTH);
                }
            }
        };
        seekBarHandler.post(moveSeekBar);
    }

    private String millisecondsToTimeElapsed(int millis){
        int total_seconds = millis / 1000;
        int total_minutes = total_seconds / 60;
        int hours = total_minutes / 60;
        int seconds = total_seconds % 60;
        int minutes = total_minutes % 60;

        String s = "";
        if(hours < 10)
            s+="0";
        s+=hours+":";
        if(minutes < 10)
            s+="0";
        s+=minutes+":";
        if(seconds < 10)
            s+="0";
        s+=seconds;
        return s;
    }

    private void pause(){
        player.pause();
        play_pause.setImageResource(R.drawable.play);
        state = State.Paused;
    }

    private void play(){
        player.start();
        play_pause.setImageResource(R.drawable.pause);
        state = State.Started;
    }

    /** Called when MediaPlayer is ready */
    public void onPrepared(MediaPlayer player) {
        state = State.Prepared;
        player.seekTo(episode.getPlaybackPosition());
        play_pause.setEnabled(true);
        preparePlaybackBar();
        duration.setText(millisecondsToTimeElapsed(player.getDuration()));
    }

    public void play_pause(){
        if(!player.isPlaying()){
            play();
        }
        else{
            pause();
        }
    }

    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        state = State.Error;
        if(i==MediaPlayer.MEDIA_ERROR_SERVER_DIED)
            Log.e("test","server died");
        else if(i==MediaPlayer.MEDIA_ERROR_UNKNOWN)
            Log.e("test","unknown error");
        if(i2==MediaPlayer.MEDIA_ERROR_IO)
            Log.e("test","io error");
        else if(i2==MediaPlayer.MEDIA_ERROR_MALFORMED)
            Log.e("test","malformed");
        else if(i2==MediaPlayer.MEDIA_ERROR_UNSUPPORTED)
            Log.e("test","unsupported");
        return false;
    }

    private void previous(){
        episode_array_position -= 1;
        prepareEpisode(database.getEpisodeById(ids[episode_array_position]));
    }

    private void next(){
        episode_array_position += 1;
        prepareEpisode(database.getEpisodeById(ids[episode_array_position]));
    }

    public void onCompletion(MediaPlayer mediaPlayer) {
        if(!seekBarBeingMoved){//Stupid MediaPlayer...
            state = State.PlaybackCompleted;
            episode.deleteLocalFile();
            setResult(PodcastViewActivity.EPISODE_FINISHED, data);
            finish();
        }
    }

    private boolean canSeek(){
        return  (state == State.Prepared) ||
                (state == State.Started)  ||
                (state == State.Paused)   ||
                (state == State.PlaybackCompleted);
    }

    /**
     * This is for when the audio suddenly goes from internal speakers to external ones.
     */
    private class MyReceiver extends BroadcastReceiver{
        public void onReceive(Context ctx, Intent intent) {
            if (intent.getAction().equals(
                    android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
                if(player != null){
                    pause();
                }
            }
        }
    }

    private class BackButtonListener implements View.OnClickListener {
        public void onClick(View view) {
            if(player != null){
                try{
                if(player.getCurrentPosition() < 2000 && episode_array_position > 0)
                    previous();
                else{
                    last_player_position = player.getCurrentPosition();
                    player.seekTo(0);
                }
                }catch(Exception ex){
                    Log.e(TAG,"",ex);
                }
            }
        }
    }

    private class BackButtonLongClickListener implements  View.OnLongClickListener{
        public boolean onLongClick(View view) {
            if(player != null && canSeek()) {
                player.seekTo(last_player_position);
                return true;
            }
            return false;
        }
    }

    private class ForwardButtonListener implements View.OnClickListener {
        public void onClick(View view) {
            if(player != null){
                if(episode_array_position == ids.length-1)
                    player.seekTo(player.getDuration());//it gets the job done
                else
                    next();
            }
        }
    }
}
