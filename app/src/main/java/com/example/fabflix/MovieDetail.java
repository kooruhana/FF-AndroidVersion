package com.example.fabflix;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MovieDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        final Intent intent = getIntent();
        // Set movie title font
        TextView titleText = findViewById(R.id.detail_title);
        Typeface typefaceTitle = ResourcesCompat.getFont(titleText.getContext(), R.font.mandrawn);
        titleText.setTypeface(typefaceTitle);
        titleText.setText(intent.getStringExtra("title"));

        TextView idView = findViewById(R.id.detail_id_value);
        idView.setText(intent.getStringExtra("id"));

        TextView yearView = findViewById(R.id.detail_year_value);
        yearView.setText(intent.getStringExtra("year"));

        TextView directorView = findViewById(R.id.detail_director_value);
        directorView.setText(intent.getStringExtra("director"));

        TextView overView = findViewById(R.id.detail_overview_value);
        overView.setText(intent.getStringExtra("overview"));

        TextView rateView = findViewById(R.id.detail_rating_value);
        rateView.setText(intent.getStringExtra("rating"));

        TextView numView = findViewById(R.id.detail_num_value);
        numView.setText(intent.getStringExtra("numVotes"));

        TextView genreView = findViewById(R.id.detail_genre_value);
        genreView.setText(intent.getStringExtra("genres"));

        TextView starView = findViewById(R.id.detail_star_value);
        starView.setText(intent.getStringExtra("stars"));

        Button backButton = findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
