package com.theappnerds.shubham.soundrecorder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class About extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);


        ImageView closeImageView = (ImageView) findViewById(R.id.closeImageView);
        FloatingActionButton floatingActionButton_github = (FloatingActionButton) findViewById(R.id.floatingActionButton_github);
        TextView about_library_1 = (TextView) findViewById(R.id.about_library_1);
        TextView about_library_2 = (TextView) findViewById(R.id.about_library_2);

        closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        floatingActionButton_github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://unsplash.com/license");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(About.this, "Browser not found.", Toast.LENGTH_SHORT).show();

                }

            }
        });


        about_library_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://github.com/81813780/AVLoadingIndicatorView");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(About.this, "Browser not found.", Toast.LENGTH_SHORT).show();

                }

            }
        });


        about_library_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://ionicons.com");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(About.this, "Browser not found.", Toast.LENGTH_SHORT).show();

                }

            }
        });


    }
}