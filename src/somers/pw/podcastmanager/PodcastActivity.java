/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package somers.pw.podcastmanager;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import somers.pw.podcastmanager.Adapters.SubscriptionArrayAdapter;

public class PodcastActivity extends Activity {
    private static final String TAG = PodcastActivity.class.getName();
    private static Context context;
    public static final String WIFI = "Wi-Fi";
    public static final String ANY = "Any";

    private Database db;
    private Podcast subscriptions[];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        PodcastActivity.context = getApplicationContext();
        /**
         * adding a handler for catching crashes, for debugging purposes.
         */
        Thread.UncaughtExceptionHandler mUEhandler = new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread thread, Throwable throwable) {
                Log.e(TAG,"",throwable);
            }
        };
        Thread.setDefaultUncaughtExceptionHandler(mUEhandler);

        db = new Database(this);
        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.show();
        }
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.to_search_button:
                Intent searchActivity = new Intent(getBaseContext(), SearchActivity.class);
                startActivity(searchActivity);
            break;
        }
    }

    // Refreshes the display if the network connection and the
    // pref settings allow it.
    @Override
    public void onStart() {
        super.onStart();
        loadSubscriptions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent searchActivity = new Intent(getBaseContext(), SearchActivity.class);
                startActivity(searchActivity);
                return true;
            case R.id.action_settings:
                Log.d(TAG,"settings button pushed");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static boolean internetConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = connectivityManager.getActiveNetworkInfo();
        if(network == null)
            return false;
        return network.isConnectedOrConnecting();
    }

    public static Context getAppContext() {
        return context;
    }

    private void loadSubscriptions(){
        this.subscriptions = db.getSubscriptions();
        if(subscriptions != null){
            SubscriptionArrayAdapter adapter = new SubscriptionArrayAdapter(this,subscriptions);
            ListView list = (ListView)findViewById(R.id.subscription_list);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Podcast podcast = subscriptions[position];
                    Intent intent = new Intent(PodcastActivity.this, PodcastViewActivity.class);
                    intent.putExtra("podcast",podcast);
                    PodcastActivity.this.startActivity(intent);
                }
            });
            list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long l) {
                    return PodcastActivity.this.onItemLongClick(parent, view, position, l);
                }
            });
            list.setAdapter(adapter);
        }
    }
}
