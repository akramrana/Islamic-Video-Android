package com.akramhossain.islamicvideo;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akramhossain.islamicvideo.Adapter.RecyclerViewAdapter;
import com.akramhossain.islamicvideo.Config.ConnectionDetector;
import com.akramhossain.islamicvideo.Config.Db;
import com.akramhossain.islamicvideo.Config.DeviceDetector;
import com.akramhossain.islamicvideo.Config.Main;
import com.akramhossain.islamicvideo.Listener.RecyclerTouchListener;
import com.akramhossain.islamicvideo.Models.Video;
import com.akramhossain.islamicvideo.Tasks.VideoPlayJsonFromUrlTask;
import com.google.android.material.navigation.NavigationView;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragmentX;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.akramhossain.islamicvideo.Config.Main.host;


/**
 * Created by Lenovo on 8/20/2018.
 */

public class VideoPlayActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    public static String URL;
    private static final String TAG = VideoPlayActivity.class.getSimpleName();
    public static String videoId;
    public static String videoName;
    TextView videoTitle;
    TextView videoViews;

    private RecyclerView recyclerview;
    private ArrayList<Video> videos;
    private RecyclerViewAdapter rvAdapter;
    YouTubePlayerSupportFragmentX youTubePlayerFragment;
    YouTubePlayer.OnInitializedListener mOnInitializedListener;
    public String youtubeVideoId;
    Db dbhelper;
    JSONObject details;
    ImageView favorite;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    DeviceDetector dd;
    Boolean isTablet = false;


    private String YouTubeKey = Main.YouTubeKey;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            videoId = extras.getString("video_id");
            videoName = extras.getString("video_name");
        }

        URL = host+"api/video-details?lang=en&video_id="+videoId;

        setTitle(videoName);

        //Toast.makeText(getApplicationContext(), categoryId + " is selected!", Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_video_play);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeButtonEnabled(true);

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);

        dd = new DeviceDetector(getApplicationContext());
        isTablet = dd.isTablet();
        Log.e("Device Type", "Device is tablet: " + isTablet.toString());

        recyclerview = (RecyclerView) findViewById(R.id.related_video_list);
        if(isTablet) {
            recyclerview.setLayoutManager(new GridLayoutManager(this, 2));
        }else {
            recyclerview.setLayoutManager(new LinearLayoutManager(this));
        }
        setRecyclerViewAdapter();
        recyclerview.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Video vd = videos.get(position);
                //Toast.makeText(getApplicationContext(), vd.getVideo_id() + " is selected!", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(getApplicationContext(),VideoPlayActivity.class);
                in.putExtra("video_id", vd.getVideo_id());
                in.putExtra("video_name", vd.getTitle());
                startActivityForResult(in, 100);

            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        youTubePlayerFragment = (YouTubePlayerSupportFragmentX) getSupportFragmentManager().findFragmentById(R.id.youtubeplayerfragment);

        dbhelper = new Db(getApplicationContext());

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        //FETCH DATA FROM REMOTE SERVER
        getDataFromInternet();

        favorite = (ImageView) findViewById(R.id.favorite);
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SQLiteDatabase db = dbhelper.getWritableDatabase();
                    String sql = "SELECT * FROM favorite WHERE video_id = "+details.getString("id");
                    Cursor cursor = db.rawQuery(sql,null);
                    if (cursor.moveToFirst()) {
                        db.execSQL("DELETE FROM favorite WHERE video_id = "+details.getString("id"));
                        Toast.makeText(getApplicationContext(), "Deleted from favorite.", Toast.LENGTH_LONG).show();
                        favorite.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_nav_favorites));
                    }
                    else {
                        dbhelper.addToFavorite(details.getString("id"), details.getString("category_id"),
                                details.getString("category_name"), details.getString("title"),
                                details.getString("source"), details.getString("url"), details.getString("image"), details.getString("youtubeVideoId"));

                        Toast.makeText(getApplicationContext(), "Added to favorite.", Toast.LENGTH_LONG).show();
                        favorite.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_nav_favorites_active));
                    }

                } catch (Exception e) {
                    Log.e("Favorite", "added failed");
                }
            }
        });
    }

    private void getDataFromInternet() {
        if (isInternetPresent) {
            new VideoPlayJsonFromUrlTask(this, URL);
        }
        else{
            AlertDialog.Builder alert = new AlertDialog.Builder(VideoPlayActivity.this);
            alert.setTitle(R.string.text_warning);
            alert.setMessage(R.string.text_enable_internet);
            alert.setPositiveButton(R.string.text_ok,null);
            alert.show();
        }
    }

    private void setRecyclerViewAdapter() {
        videos = new ArrayList<Video>();
        rvAdapter = new RecyclerViewAdapter(VideoPlayActivity.this, videos);
        recyclerview.setAdapter(rvAdapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finishAffinity();
        }
        else if (id == R.id.nav_browse) {
            Intent i = new Intent(getApplicationContext(), BrowseActivity.class);
            startActivity(i);
            finishAffinity();
        }
        else if (id == R.id.nav_categories) {
            Intent i = new Intent(getApplicationContext(), CategoryActivity.class);
            startActivity(i);
            finishAffinity();
        } else if (id == R.id.nav_favorites) {
            Intent i = new Intent(getApplicationContext(), FavoriteActivity.class);
            startActivity(i);
            finishAffinity();
        } else if (id == R.id.nav_recently_watched) {
            Intent i = new Intent(getApplicationContext(), WatchHistoryActivity.class);
            startActivity(i);
            finishAffinity();
        } else if (id == R.id.nav_feed_back) {
            Intent i = new Intent(getApplicationContext(), HelpFeedbackActivity.class);
            startActivity(i);
            finishAffinity();
        } else if (id == R.id.nav_about) {
            Intent i = new Intent(getApplicationContext(), CmsActivity.class);
            i.putExtra("cms_title", "About Us");
            i.putExtra("cms_page", "about-us");
            startActivity(i);
            finishAffinity();
        } else if (id == R.id.nav_terms) {
            Intent i = new Intent(getApplicationContext(), CmsActivity.class);
            i.putExtra("cms_title", "Terms and Conditions");
            i.putExtra("cms_page", "terms");
            startActivity(i);
            finishAffinity();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void parseJsonResponse(String result) {
        Log.i(TAG, result);
        try {
            JSONObject response = new JSONObject(result);
            JSONObject json = response.getJSONObject("data");
            details = json.getJSONObject("details");

            videoTitle = (TextView) findViewById(R.id.video_title);
            videoTitle.setText(details.getString("title"));

            videoViews = (TextView) findViewById(R.id.views);
            videoViews.setText(details.getString("views")+" views");

            JSONArray jArray = new JSONArray(json.getString("related"));
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                //featured videos
                Video video = new Video();
                video.setVideo_id(jObject.getString("id"));
                video.setTitle(jObject.getString("title"));
                video.setCategory_name(jObject.getString("category_name"));
                video.setImage(jObject.getString("image"));
                video.setViews(jObject.getString("views"));
                videos.add(video);
            }
            rvAdapter.notifyDataSetChanged();

            youtubeVideoId = details.getString("youtubeVideoId");
            try {
                dbhelper.insertWatchHistory(details.getString("id"),details.getString("category_id"),
                        details.getString("category_name"),details.getString("title"),
                        details.getString("source"),details.getString("url"),details.getString("image"),details.getString("youtubeVideoId"));

                //Toast.makeText(getApplicationContext(),"Added to watch history.", Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                Log.e("Watch History", "added failed");
            }

            mOnInitializedListener = new YouTubePlayer.OnInitializedListener(){
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
                    if(!wasRestored) {
                        youTubePlayer.loadVideo(youtubeVideoId);
                    }
                }
                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult error) {

                }
            };
            youTubePlayerFragment.initialize(this.YouTubeKey, mOnInitializedListener);

            try {
                SQLiteDatabase db = dbhelper.getWritableDatabase();
                String sql = "SELECT * FROM favorite WHERE video_id = "+details.getString("id");
                Cursor cursor = db.rawQuery(sql,null);
                if (cursor.moveToFirst()) {
                    favorite.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_nav_favorites_active));
                }
                else {
                    favorite.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_nav_favorites));
                }

            } catch (Exception e) {
                Log.e("DB Operation failed", "added failed");
            }
            favorite.setVisibility(View.VISIBLE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
