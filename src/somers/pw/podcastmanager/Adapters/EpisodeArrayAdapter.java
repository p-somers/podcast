package somers.pw.podcastmanager.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import somers.pw.podcastmanager.DownloadManager;
import somers.pw.podcastmanager.Episode;
import somers.pw.podcastmanager.PodcastActivity;
import somers.pw.podcastmanager.R;

/**
 * Created by petersomers on 12/22/13.
 */
public class EpisodeArrayAdapter extends ArrayAdapter<Episode> {
    private static final String TAG = PodcastActivity.class.getName();

    private final Context context;
    private final Episode values[];

    public EpisodeArrayAdapter(Context context, Episode values[]){
        super(context, R.layout.search,values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Episode episode = values[position];
        View rowView;
            rowView = inflater.inflate(R.layout.episode_list, parent, false);
            TextView title = (TextView)rowView.findViewById(R.id.list_item_title);
            TextView duration = (TextView)rowView.findViewById(R.id.list_item_duration);
            title.setText(episode.getTitle());
            duration.setText(episode.getDuration());
            if(DownloadManager.is_being_downloaded(episode)){
                int color = context.getResources().getColor(R.color.downloaded_text_color);
                title.setTextColor(color);
                episode.showProgressBar();
            } else if(episode.isDownloaded()){
                int color = context.getResources().getColor(R.color.downloaded_text_color);
                title.setTextColor(color);
            }
        return rowView;
    }
}
