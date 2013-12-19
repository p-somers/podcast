package com.example.android.podcastapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

/**
 * Created by petersomers on 12/17/13.
 */
public class SearchActivity extends Activity {
    protected EditText box;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        box = (EditText)findViewById(R.id.box);
    }

    public void onClick(View view){
        switch(view.getId()){
            case R.id.box:search(box.getText().toString());
        }
    }

    public void search(String title){
        Log.d("test",title);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
