package com.example.fabflix;

import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    String email;
    char[] password;
    EditText emailED, passwordED;
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
                password = passwordED.getText().toString().toCharArray();
                System.out.println(email+" abcd");
            }
        });



    }
}
