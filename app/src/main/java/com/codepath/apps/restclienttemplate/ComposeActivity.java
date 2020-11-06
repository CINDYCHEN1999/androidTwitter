package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {
    public static final String TAG = "ComposeActivity";
    public static final int MAX_TWEET_LENGTH = 280;
    EditText etCompose;
    Button btnTweet;
    TextView tvCount;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApp.getRestClient(this);

        tvCount = findViewById(R.id.tvCount);
        etCompose = findViewById(R.id.etCompose);

        etCompose.addTextChangedListener(new TextWatcher() { 
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCount.setText(String.valueOf(s.length()));
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().length() > 280) {
                    tvCount.setText(s.toString().length() + "/280");
                    tvCount.setTextColor(Color.RED);
                } else {
                    tvCount.setText(s.toString().length() + "/280");
                    tvCount.setTextColor(Color.GRAY);
                }

            }
        });

        btnTweet = findViewById(R.id.btnTweet);

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ShowToast")
            @Override
            public void onClick(View v) {
                final String tweetContent = etCompose.getText().toString();
                if (tweetContent.isEmpty()){
                    Toast.makeText(ComposeActivity.this, "sorry, no text", Toast.LENGTH_LONG).show();

                    return;
                }
                if (tweetContent.length() > MAX_TWEET_LENGTH){
                    Toast.makeText(ComposeActivity.this, "sorry, too long", Toast.LENGTH_LONG).show();
                    return;
                }


                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess to publish the tweet");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "publish the tweet: " + tweet.body);
                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK, intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }


                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure to publish the tweet", throwable);

                    }
                });
            }
        });
    }
}