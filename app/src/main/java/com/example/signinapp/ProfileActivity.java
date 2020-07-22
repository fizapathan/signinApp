package com.example.signinapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class ProfileActivity extends AppCompatActivity {

    AppUser appUser;
    ImageView profilePicture;
    TextView userName;
    TextView userMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent profileIntent = getIntent();
        appUser = (AppUser) profileIntent.getParcelableExtra("USER_ACCOUNT");
        profilePicture = findViewById(R.id.profile_picture);
        userName = findViewById(R.id.user_name);
        userMail = findViewById(R.id.user_mail);

        assignToUI();
    }

    private void assignToUI() {
        Glide.with(this)
                .load(appUser.getProfilePicture())
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.ic_user_profile_placeholder)
                .error(R.drawable.ic_user_profile_placeholder)
                .into(profilePicture);

        userName.setText(appUser.getUserName());
        userMail.setText(appUser.getUserMail());
    }
}