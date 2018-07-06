package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    List<Tweet> mTweets;
    Context context;

    // pass in the Tweets array in the constructor
    public TweetAdapter(List<Tweet> tweets) {
        mTweets = tweets;
    }

    // for each row, inflate the layout and cache references into ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    // bind the values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // get the data according to position
        Tweet tweet = mTweets.get(position);

        // populate the views according to this data
        holder.tvName.setText(tweet.user.name);
        holder.tvBody.setText(tweet.body);
        holder.tvUserName.setText('@' + tweet.user.screenName);
        holder.tvTimestamp.setText(getRelativeTimeAgo(tweet.createdAt));

        Glide.with(context)
                .load(tweet.user.profileImageUrl)
                .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(80)))
                .into(holder.ivProfileImage);
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
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

    // Clean all elements of the recycler
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }

    // create ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView ivProfileImage;
        public TextView tvName;
        public TextView tvBody;
        public TextView tvUserName;
        public TextView tvTimestamp;
        public Button replyBtn;
        public Button retweetBtn;
        public Button likeBtn;
        public Boolean liked = false;
        public Boolean retweeted = false;

        public ViewHolder(View itemView) {
            super(itemView);

            // perform findViewById lookups

            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvName = itemView.findViewById(R.id.tvName);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            replyBtn = itemView.findViewById(R.id.replyBtn);
            retweetBtn = itemView.findViewById(R.id.retweetBtn);
            likeBtn = itemView.findViewById(R.id.likeBtn);

            replyBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), ComposeActivity.class);
                    i.putExtra("premessage", tvUserName.getText());
                    v.getContext().startActivity(i);
                }
            });

            likeBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    liked = !liked;
                    if (liked) {
                        v.setBackgroundResource(R.drawable.ic_vector_heart);
                        v.setBackgroundTintList(v.getContext().getResources().getColorStateList(R.color.inline_action_like_pressed));
                    }
                    else {
                        v.setBackgroundResource(R.drawable.ic_vector_heart_stroke);
                        v.setBackgroundTintList(v.getContext().getResources().getColorStateList(R.color.grey));
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
                    }
                    else {
                        v.setBackgroundResource(R.drawable.ic_vector_retweet_stroke);
                        v.setBackgroundTintList(v.getContext().getResources().getColorStateList(R.color.grey));
                    }
                }
            });

            itemView.setOnClickListener(this);
        }

        // when the user clicks on a row, show TweetDetailsActivity for the selected movie
        @Override
        public void onClick(View v) {
            // gets item position
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the movie at the position, this won't work if the class is static
                Tweet tweet = mTweets.get(position);

                // create intent for the new activity
                Intent intent = new Intent(context, TweetDetailsActivity.class);
                // serialize the movie using parceler, use its short name as a key
                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                // show the activity
                context.startActivity(intent);
            }
        }
    }
}
