package com.example.prepcheck;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.ColorInt;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AboutUs extends AppCompatActivity {

    ImageView imgLinkedIn, imgGithub, imgInstagram;
    TextView txtEmail;


    private static final String LINKEDIN_PROFILE_URL = "http://linkedin.com/in/harshit-sharma-414535214";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about_us);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("About the Developer");
        }


        imgLinkedIn = findViewById(R.id.imgLinkdin);
        imgGithub = findViewById(R.id.imgGithub);
        imgInstagram = findViewById(R.id.imgInstagram);
        txtEmail = findViewById(R.id.txtEmail);



        imgLinkedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToUrl("https://linkedin.com/in/harshit-sharma-414535214");
            }
        });

        imgGithub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToUrl("https://github.com/harshit-nh");
            }
        });

        imgInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToUrl("https://www.instagram.com/harshit_nh/");
            }
        });

        txtEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + "harshitbly18@gmail.com"));
                    intent.putExtra(Intent.EXTRA_SUBJECT, "From PrepCheck");
                    startActivity(intent);
                }catch(ActivityNotFoundException e){
                    System.out.println(e);
                }
            }
        });

        setStatusBarColor(getResources().getColor(R.color.status_bar_color2));

    }

    private void navigateToUrl(String url){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // Method to change status bar color
    private void setStatusBarColor(@ColorInt int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

}