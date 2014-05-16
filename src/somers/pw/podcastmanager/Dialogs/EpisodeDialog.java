package somers.pw.podcastmanager.Dialogs;

import android.app.Dialog;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import somers.pw.podcastmanager.Database;
import somers.pw.podcastmanager.Episode;
import somers.pw.podcastmanager.PodcastActivity;
import somers.pw.podcastmanager.PodcastViewActivity;
import somers.pw.podcastmanager.R;


/**
 * Created by petersomers on 2/6/14.
 */
public class EpisodeDialog extends Dialog {
    private static final String TAG = PodcastActivity.class.getName();
    private Episode episode;
    private PodcastViewActivity podcastViewActivity;
    private Database database;
    public EpisodeDialog(final PodcastViewActivity context, Episode e, int array_pos) {
        super(context);
        episode = e;
        podcastViewActivity = context;
        database = new Database(context.getApplicationContext());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.episode_details_dialog);
        //setTitle(episode.getTitle());

        String dateFormat = PodcastActivity.getAppContext().getString(R.string.episode_detail_date_format);
        String date = DateFormat.format(dateFormat,episode.getPubDate()).toString();

        TextView title = (TextView)findViewById(R.id.episode_detail_title);
        TextView summary = (TextView)findViewById(R.id.episode_detail_summary);
        TextView duration = (TextView)findViewById(R.id.episode_detail_duration);
        TextView pubDate = (TextView)findViewById(R.id.episode_detail_pubdate);
        title.setText(episode.getTitle());
        summary.setText(episode.getSummary());
        duration.setText(episode.getDuration());
        pubDate.setText(date);

        Button ok_button = (Button)findViewById(R.id.episode_detail_ok);
        ok_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dismiss();
            }
        });

        Button delete_button = (Button)findViewById(R.id.episode_detail_delete);
        delete_button.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                episode.deleteLocalFile();
                podcastViewActivity.notifyDataSetChanged();
                dismiss();
                return true;
            }
        });

        final Button mark_finished = (Button)findViewById(R.id.episode_detail_mark_finished);
        mark_finished.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if( database.markEpisodeFinished(episode) ){
                    mark_finished.setEnabled(false);
                    context.reloadFromDatabase();
                } else {
                    Toast.makeText(context,"Error marking episode as \"finished\"",Toast.LENGTH_LONG);
                }
            }
        });
    }
}
