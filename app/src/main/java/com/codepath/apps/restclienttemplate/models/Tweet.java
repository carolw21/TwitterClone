package com.codepath.apps.restclienttemplate.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class Tweet {

    // list out the attributes
    public String body;
    public long uid; // database ID for the tweet
    public User user;
    public String createdAt;
    public String media = "none";
    public Boolean liked;
    public Boolean retweeted;

    public Tweet() {}

    // deserialize the JSON
    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();

        // extract the values from JSON
        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        if (jsonObject.getJSONObject("entities").has("media")) {
            JSONArray j = jsonObject.getJSONObject("entities").getJSONArray("media");
            if (j != null && j.length() > 0) {
                tweet.media = j.getJSONObject(0).getString("media_url");
            }
        }
        tweet.liked = jsonObject.getBoolean("favorited");
        tweet.retweeted = jsonObject.getBoolean("retweeted");

        return tweet;
    }
}
