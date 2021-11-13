package com.example.ps3;

import android.annotation.SuppressLint;
import android.os.Bundle;

import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {

    TextView helloView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        String login = getIntent().getStringExtra("login");
        helloView = findViewById(R.id.helloText);
        helloView.setText("WITAJ  " + login + " !");

    }
}