package com.example.fabflix;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {
    static int currentPage = 1;
    EditText input;
    ListView result;
    String email, query, sessionID, overview;
    String searchURL = "http://andromeda-70.ics.uci.edu:9509/api/movies/search";
    String idURL = "http://andromeda-70.ics.uci.edu:9509/api/movies/get/";
    MovieModel clickedMovie;
    JSONArray movies;
    ArrayList<MovieModel> resultAL;
    ArrayList<String> titleAL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        final Intent intent = getIntent();

        Button searchButton = findViewById(R.id.sumbit);
        input = findViewById(R.id.searchtext);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                query = input.getText().toString();
                email = intent.getStringExtra("email");
                sessionID = intent.getStringExtra("sessionID");
                final OkHttpClient client = new OkHttpClient();
                String titleURL = searchURL + "?title=" + query;
                try {
                    get(titleURL, email, sessionID, client, new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            e.printStackTrace();
                            System.out.println("fail");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseText = response.body().string(); // get response text
                            try {
                                final JSONObject responseJSON = new JSONObject(responseText);
                                if (response.isSuccessful()) {
                                    if( (int) responseJSON.get("resultCode")!=210){
                                        final String message = (String) responseJSON.get("message");
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                final Toast toast = Toast.makeText(SearchActivity.this, message,  Toast.LENGTH_LONG);
                                                toast.show();
                                            }
                                        });
                                    } else {
                                        // Found Movie----
                                        result = findViewById(R.id.resultList);
                                        resultAL = new ArrayList<>();
                                        titleAL = new ArrayList<>();
                                        movies = responseJSON.getJSONArray("movies");
                                        for ( int i=0 ; i< movies.length() ; i++){
                                            JSONObject movieJSON = movies.getJSONObject(i);
                                            ObjectMapper mapper = new ObjectMapper();
                                            MovieModel movie = mapper.readValue(movieJSON.toString(), MovieModel.class);
                                            resultAL.add(movie);
                                            titleAL.add(movie.getTitle());
                                        }
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                ListView myLW = findViewById(R.id.resultList);
                                                ArrayAdapter<String> items = new ArrayAdapter<String> (SearchActivity.this, R.layout.item, titleAL);
                                                myLW.setAdapter(items);
                                                lwClick(myLW, resultAL, client);
                                        }});
                                         }
                                } else {
                                    final String message = (String) responseJSON.get("message");
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            final Toast toast = Toast.makeText(SearchActivity.this, message,  Toast.LENGTH_LONG);
                                            toast.show();
                                        }
                                    });
                                }
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    Call get(String url, String email, String sessionID, OkHttpClient client, Callback callback) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("email", email)
                .header("sessionID", sessionID)
                .get()
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    void lwClick(ListView lw, final ArrayList<MovieModel> movieList, final OkHttpClient client){
        lw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String url = idURL + movieList.get(position).getMovieId();
                clickedMovie = movieList.get(position);
                try {
                    get(url, email, sessionID, client, new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            e.printStackTrace();
                            System.out.println("fail");
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            sessionID = response.header("sessionID");
                            String responseText = response.body().string();
                            System.out.println(responseText);
                            try {
                                final JSONObject responseJSONItem = new JSONObject(responseText);
                                if (response.isSuccessful()) {
                                    if( (int) responseJSONItem.get("resultCode")!=210){
                                        final String message = (String) responseJSONItem.get("message");
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                final Toast toast = Toast.makeText(SearchActivity.this, message,  Toast.LENGTH_LONG);
                                                toast.show();
                                            }
                                        });
                                    } else {
                                        JSONObject movie = (JSONObject) responseJSONItem.get("movie");
                                        Intent intent = new Intent(com.example.fabflix.SearchActivity.this, com.example.fabflix.MovieDetail.class);
                                        if (movie.has("overview")) {
                                            intent.putExtra("overview", (String) movie.get("overview"));
                                        } else {
                                            intent.putExtra("overview", "No Overview");
                                        }
                                        JSONArray genres = movie.getJSONArray("genres");
                                        String genreString = "";
                                        for (int i = 0; i < genres.length(); i++) {
                                            genreString += genres.getJSONObject(i).get("name") + ", ";
                                        }
                                        intent.putExtra("genres", genreString);
                                        JSONArray stars = movie.getJSONArray("stars");
                                        String starString = "";
                                        for (int i = 0; i < stars.length(); i++) {
                                            starString += stars.getJSONObject(i).get("name") + ", ";
                                        }
                                        intent.putExtra("stars", starString);
                                        intent.putExtra("sessionID", sessionID);
                                        intent.putExtra("email", email);
                                        intent.putExtra("title", (String) movie.get("title"));
                                        intent.putExtra("id", (String) movie.get("movieId"));
                                        intent.putExtra("director", (String) movie.get("director"));
                                        intent.putExtra("year", String.valueOf((int) movie.get("year")));
                                        intent.putExtra("rating", String.valueOf(movie.get("rating")));
                                        intent.putExtra("numVotes", String.valueOf((int) movie.get("numVotes")));
                                        startActivity(intent);
                                    }

                                } else {
                                    final String message = (String) responseJSONItem.get("message");
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            final Toast toast = Toast.makeText(SearchActivity.this, message, Toast.LENGTH_LONG);
                                            toast.show();
                                        }
                                    });
                                }
                            } catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}