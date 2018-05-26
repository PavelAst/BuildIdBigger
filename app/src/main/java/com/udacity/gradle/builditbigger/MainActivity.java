package com.udacity.gradle.builditbigger;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.jst.displayjokelibrary.DisplayActivity;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.udacity.gradle.builditbigger.IdlingResource.JokeIdlingResource;
import com.udacity.gradle.builditbigger.backend.myApi.MyApi;

import java.io.IOException;
import java.lang.ref.WeakReference;


public class MainActivity extends AppCompatActivity {

    @Nullable
    private JokeIdlingResource mIdlingResource;

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new JokeIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(this, R.string.greetings, Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void tellJoke(View view) {
        new JokeAsyncTask(this, mIdlingResource).execute();
    }

    private static class JokeAsyncTask extends AsyncTask<Void, Void, String> {

        private WeakReference<MainActivity> mActivityWeakReference;
        private MyApi myApiService = null;
        JokeIdlingResource mIdlingResource;

        public JokeAsyncTask(MainActivity activity, final JokeIdlingResource idlingResource) {
            mActivityWeakReference = new WeakReference<>(activity);
            mIdlingResource = idlingResource;
            if (mIdlingResource != null) {
                mIdlingResource.setIdleState(false);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MainActivity activity = mActivityWeakReference.get();
            if (activity == null || activity.isFinishing()) return;

            ProgressBar loadingIndicator = activity.findViewById(R.id.pb_loading_indicator);
            loadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            if (myApiService == null) {  // Only do this once
                MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });

                myApiService = builder.build();
            }

            try {
                return myApiService.getJoke().execute().getData();
            } catch (IOException e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String joke) {
            if (mIdlingResource != null) {
                mIdlingResource.setIdleState(true);
            }

            // get a reference to the activity if it is still there
            MainActivity activity = mActivityWeakReference.get();
            if (activity == null || activity.isFinishing()) return;

            ProgressBar loadingIndicator = activity.findViewById(R.id.pb_loading_indicator);
            loadingIndicator.setVisibility(View.INVISIBLE);

            Intent myIntent = new Intent(activity, DisplayActivity.class);
            myIntent.putExtra(DisplayActivity.JOKE_KEY, joke);
            activity.startActivity(myIntent);
        }
    }

}
