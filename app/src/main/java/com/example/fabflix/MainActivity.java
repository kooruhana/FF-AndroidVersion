package com.example.fabflix;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class MainActivity extends AppCompatActivity {
    static String sessionID;
    String email;
    String[] password;
    EditText emailED, passwordED;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    String loginURL = "http://andromeda-70.ics.uci.edu:9508/api/idm/login";
//    String reportURL = "http://andromeda-70.ics.uci.edu:9507/api/g/report";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView titleText = findViewById(R.id.text_title);
        Typeface typefaceTitle = ResourcesCompat.getFont(titleText.getContext(), R.font.mandrawn_itallic);
        titleText.setTypeface(typefaceTitle);

        TextView loginText = findViewById(R.id.text_login);
        Typeface typefaceLogin = ResourcesCompat.getFont(loginText.getContext(), R.font.mandrawn);
        loginText.setTypeface(typefaceLogin);

        Button loginButton = findViewById(R.id.login_button);
        emailED = (EditText) findViewById(R.id.username);
        passwordED = (EditText)findViewById(R.id.pwd);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailED.getText().toString();
                password = passwordED.getText().toString().split("");
                String[] pwd = Arrays.copyOfRange(password, 1, password.length);
                OkHttpClient client = new OkHttpClient();
                JSONObject request = new JSONObject();
                try {
                    JSONArray pwd2 = new JSONArray(pwd);
                    request.put("email", email);
                    request.put("password", pwd2);
                    String requestString = request.toString();
                    post(loginURL, requestString, client, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                            System.out.println("fail");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseText = response.body().string(); // get response text
                            try {
                                final JSONObject responseJSON = new JSONObject(responseText);
                                if (response.isSuccessful()) {
                                    if( (int) responseJSON.get("resultCode")!=120){
                                        final String message = (String) responseJSON.get("message");
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                final Toast toast = Toast.makeText(MainActivity.this, message,  Toast.LENGTH_LONG);
                                                toast.show();
                                            }
                                        });
                                    } else {
                                        // Enter search activity
                                        String sessionID = (String) responseJSON.get("sessionID");
                                        Intent intent = new Intent(com.example.fabflix.MainActivity.this, com.example.fabflix.SearchActivity.class);
                                        intent.putExtra("sessionID", sessionID);
                                        intent.putExtra("email", email);
                                        startActivity(intent);
                                    }
                                } else {
                                    final String message = (String) responseJSON.get("message");
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            final Toast toast = Toast.makeText(MainActivity.this, message,  Toast.LENGTH_LONG);
                                            toast.show();
                                        }
                                    });
                                }
                            } catch (Exception e){
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


    Call post(String url, String json, OkHttpClient client, Callback callback) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }
}
