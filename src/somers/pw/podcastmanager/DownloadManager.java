package somers.pw.podcastmanager;

/**
 * Created by petersomers on 4/14/14.
 */
public class DownloadManager {
    private static final String TAG = PodcastActivity.class.getName();
    private static Episode current_downloads[];
    private static final int max_downloads = 5;

    public static boolean register_download(Episode episode){
        if(current_downloads == null) {
            //Initialization
            current_downloads = new Episode[max_downloads];
            for (int i = 0; i < max_downloads; i++) {
                current_downloads[i] = null;
            }
        }
        for(int i=0; i<max_downloads; i++){
            if(current_downloads[i]==null){
                current_downloads[i]=episode;
                return true;
            }
        }
        return false;
    }

    public static void unregister_download(Episode episode){
        for(int i=0; i<max_downloads; i++){
            if(current_downloads[i]==episode){
                current_downloads[i]=null;
                break;
            }
        }
    }

    public static boolean is_being_downloaded(Episode episode){
        if(current_downloads == null)
            return false;
        for(int i=0; i<max_downloads; i++) {
            if(current_downloads[i] == episode) {
                return true;
            }
        }
        return false;
    }
}
