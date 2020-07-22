package com.example.signinapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import retrofit2.Call;

public class MainActivity extends AppCompatActivity {

    private GoogleSignInOptions googleSignInOptions;
    private GoogleSignInClient googleSignInClient;
    TwitterLoginButton twitterLoginButton;
    TwitterApiClient twitterApiClient;
    private final int REQUEST_CODE_SIGN_IN = 123;
    private final int GOOGLE_BUTTON = 0;
    private final int TWITTER_BUTTON = 1;
    private int clickedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))//enable logging when app is in debug mode
                .twitterAuthConfig(new TwitterAuthConfig(
                        getResources().getString(R.string.twitter_api_key),
                        getResources().getString(R.string.twitter_api_secret)))
                .debug(true)//enable debug mode
                .build();
        Twitter.initialize(this);
        setContentView(R.layout.activity_main);

        googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.google_client_id))
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        SignInButton googleSignInButton = findViewById(R.id.google_signin);
        googleSignInButton.setSize(SignInButton.SIZE_STANDARD);
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedItem = GOOGLE_BUTTON;
                googleSignIn();
            }
        });

        twitterLoginButton = findViewById(R.id.twitter_signin);
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
                clickedItem = TWITTER_BUTTON;
                twitterApiClient = TwitterCore.getInstance().getApiClient();
                twitterSignIn(twitterSession);
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(MainActivity.this, "Can't find Twitter App on your device", Toast.LENGTH_LONG).show();
                Log.i("MainActivity","TwitterException: " + exception.getMessage());
            }
        });
    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(clickedItem) {
            case GOOGLE_BUTTON:
                if (requestCode == REQUEST_CODE_SIGN_IN) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    handleSignInResult(task);
                }
            case TWITTER_BUTTON:
                twitterLoginButton.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void googleSignIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            AppUser appUser;
            if(account != null) {
                appUser = new AppUser(
                        account.getPhotoUrl(),
                        account.getDisplayName(),
                        account.getEmail()
                );
                Intent profileActivityIntent = new Intent(MainActivity.this, ProfileActivity.class);
                profileActivityIntent.putExtra("USER_ACCOUNT", appUser);
                startActivity(profileActivityIntent);
            }
        } catch (ApiException e) {
            Toast.makeText(this, "Something's wrong! Please try again...", Toast.LENGTH_SHORT)
                    .show();
            Log.i("MainActivity", "SignInResult: Failed Code: "
                    + e.getStatusCode()
                    + "\n" + e);
        }
    }

    private void twitterSignIn(TwitterSession twitterSession) {
        Call<User> call = twitterApiClient
                .getAccountService()
                .verifyCredentials(true, false, true);
        call.enqueue(new Callback<User>() {
            @Override
            public void success(Result<User> result) {
                User user = result.data;
                AppUser appUser = new AppUser(
                        Uri.parse(user.profileImageUrl),
                        user.name,
                        user.email
                );

                Intent profileActivityIntent = new Intent(MainActivity.this, ProfileActivity.class);
                profileActivityIntent.putExtra("USER_ACCOUNT", appUser);
            }

            @Override
            public void failure(TwitterException exception) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}