package com.android.jst.displayjokelibrary;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class DisplayActivityFragment extends Fragment {

    public DisplayActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_display_activity, container, false);
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(DisplayActivity.JOKE_KEY)) {
            String joke = intent.getStringExtra(DisplayActivity.JOKE_KEY);
            TextView jokeTextView = view.findViewById(R.id.tv_joke);
            if (!TextUtils.isEmpty(joke)) {
                jokeTextView.setText(joke);
            }
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
