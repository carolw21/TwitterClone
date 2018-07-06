package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class TweetDetailsActivity extends AppCompatActivity {

    public ImageView ivProfileImage;
    public TextView tvName;
    public TextView tvBody;
    public TextView tvUserName;
    public TextView tvTimestamp;
    public ImageView ivMedia;
    public Button replyBtn;
    public Button retweetBtn;
    public Button likeBtn;
    public Tweet tweet;
    public Boolean liked;
    public Boolean retweeted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvName = findViewById(R.id.tvName);
        tvBody = findViewById(R.id.tvBody);
        tvUserName = findViewById(R.id.tvUserName);
        tvTimestamp = findViewById(R.id.tvTimestamp);
        replyBtn = findViewById(R.id.replyBtn);
        retweetBtn = findViewById(R.id.retweetBtn);
        likeBtn = findViewById(R.id.likeBtn);
        ivMedia = findViewById(R.id.ivMedia);

        tvName.setText(tweet.user.name);
        tvBody.setText(tweet.body);
        tvUserName.setText('@' + tweet.user.screenName);
        tvTimestamp.setText(getRelativeTimeAgo(tweet.createdAt));

        liked = tweet.liked;
        retweeted = tweet.retweeted;

        final TwitterClient client = TwitterApp.getRestClient(this);

        Glide.with(this)
                .load(tweet.user.profileImageUrl)
                .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(80)))
                .into(ivProfileImage);

        if (!tweet.media.equals("none")) {
            Glide.with(this)
                    .load(tweet.media)
                    .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(20)))
                    .into(ivMedia);
        }

        replyBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), ComposeActivity.class);
                i.putExtra("premessage", tvUserName.getText());
                v.getContext().startActivity(i);
            }
        });

        if (tweet.liked) {
            likeBtn.setBackgroundResource(R.drawable.ic_vector_heart);
            likeBtn.setBackgroundTintList(likeBtn.getContext().getResources().getColorStateList(R.color.inline_action_like_pressed));
        }
        else {
            likeBtn.setBackgroundResource(R.drawable.ic_vector_heart_stroke);
            likeBtn.setBackgroundTintList(likeBtn.getContext().getResources().getColorStateList(R.color.grey));
        }

        if (tweet.retweeted) {
            retweetBtn.setBackgroundResource(R.drawable.ic_vector_retweet);
            retweetBtn.setBackgroundTintList(retweetBtn.getContext().getResources().getColorStateList(R.color.inline_action_retweet));
        }
        else {
            retweetBtn.setBackgroundResource(R.drawable.ic_vector_retweet_stroke);
            retweetBtn.setBackgroundTintList(retweetBtn.getContext().getResources().getColorStateList(R.color.grey));
        }

        likeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                liked = !liked;

                if (liked) {
                    v.setBackgroundResource(R.drawable.ic_vector_heart);
                    v.setBackgroundTintList(v.getContext().getResources().getColorStateList(R.color.inline_action_like_pressed));
                    client.likeTweet(tweet.uid, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.i("TwitterApp","Success");
                            TimelineActivity.fetchTimelineAsync(0);
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            Log.e("TwitterApp", errorResponse.toString());
                        }
                    });
                }
                else {
                    v.setBackgroundResource(R.drawable.ic_vector_heart_stroke);
                    v.setBackgroundTintList(v.getContext().getResources().getColorStateList(R.color.grey));
                    client.unlikeTweet(tweet.uid, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.i("TwitterApp","Success");
                            TimelineActivity.fetchTimelineAsync(0);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            Log.e("TwitterApp", errorResponse.toString());
                        }
                    });
                }
            }
        });

        retweetBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                retweeted = !retweeted;
                if (retweeted) {
                    v.setBackgroundResource(R.drawable.ic_vector_retweet);
                    v.setBackgroundTintList(v.getContext().getResources().getColorStateList(R.color.inline_action_retweet));
                    client.retweet(tweet.uid, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.i("TwitterApp","Success");
                            TimelineActivity.fetchTimelineAsync(0);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            Log.e("TwitterApp", errorResponse.toString());
                        }
                    });
                }
                else {
                    v.setBackgroundResource(R.drawable.ic_vector_retweet_stroke);
                    v.setBackgroundTintList(v.getContext().getResources().getColorStateList(R.color.grey));
                    client.unretweet(tweet.uid, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.i("TwitterApp","Success");
                            TimelineActivity.fetchTimelineAsync(0);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            Log.e("TwitterApp", errorResponse.toString());
                        }
                    });
                }
            }
        });
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

}
