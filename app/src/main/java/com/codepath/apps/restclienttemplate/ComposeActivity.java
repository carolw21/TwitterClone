package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    TwitterClient client;
    Tweet tweet;
    EditText etTweet;
    TextView etCount;
    // Instance of the progress action-view
    MenuItem miActionProgressItem;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_compose, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        String premessage = getIntent().getStringExtra("premessage");
        etTweet = findViewById(R.id.tvTweet);
        etCount = findViewById(R.id.tvCharacterCount);
        etTweet.setText(premessage);
        TextWatcher textEditorWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //This sets a textview to the current length
                etCount.setText(String.valueOf(140-s.length())+"/140");
            }

            public void afterTextChanged(Editable s) {
            }
        };
        etTweet.addTextChangedListener(textEditorWatcher);
    }

    public void onTweet(View v) {
        client = TwitterApp.getRestClient(this);
        showProgressBar();
        client.sendTweet(etTweet.getText().toString(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // load the results into movie list
                try {
                    tweet = Tweet.fromJSON(response);
                    // Prepare data intent
                    Intent data = new Intent();
                    // Pass relevant data back as a result
                    data.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                    // Activity finished ok, return the data
                    Log.i("TwitterApp", ""+response.toString());
                    setResult(RESULT_OK, data); // set result code and bundle data for response
                    finish();
                    hideProgressBar();
                } catch (JSONException e) {
                    Log.e("TwitterApp","Tweet failed to send");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("TwitterApp","Tweet failed to send");
            }
        });
    }

    public void showProgressBar() {
        // Show progress item
        miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisible(false);
    }

}
