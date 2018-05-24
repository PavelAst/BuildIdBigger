package com.android.jst.displayjokelibrary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DisplayActivity extends AppCompatActivity {

    public static String JOKE_KEY = "Joke_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
    }
}
