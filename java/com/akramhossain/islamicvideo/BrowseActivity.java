package com.akramhossain.islamicvideo;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.akramhossain.islamicvideo.Adapter.RecyclerViewAdapter;
import com.akramhossain.islamicvideo.Config.ConnectionDetector;
import com.akramhossain.islamicvideo.Config.DeviceDetector;
import com.akramhossain.islamicvideo.Listener.RecyclerTouchListener;
import com.akramhossain.islamicvideo.Models.Video;
import com.akramhossain.islamicvideo.Tasks.BrowseJsonFromUrlTask;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.akramhossain.islamicvideo.Config.Main.host;

/**
 * Created by Lenovo on 8/23/2018.
 */

public class BrowseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static String URL;
    private static final String TAG = BrowseActivity.class.getSimpleName();

    private RecyclerView recyclerview;
    private ArrayList<Video> videos;
    private RecyclerViewAdapter rvAdapter;
    public String page = "1";
    public int Counter = 1;
    public String maxPage;
    public String is_featured="";

    LinearLayoutManager mLayoutManager;
    private boolean itShouldLoadMore = true;

    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;
    String double_click_exit = "1";
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    DeviceDetector dd;
    Boolean isTablet = false;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            is_featured = extras.getString("is_featured");
            double_click_exit = extras.getString("double_click_exit");
            setTitle("Featured Video");
        }

        URL = host+"api/search?lang=en&q=&category_id=&featured="+is_featured+"&page="+page+"&per_page=10";

        //Toast.makeText(getApplicationContext(), categoryId + " is selected!", Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_browse);
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

        recyclerview = (RecyclerView) findViewById(R.id.browse_video_list);
        if(isTablet) {
            mLayoutManager = new GridLayoutManager(this, 2);
        }else {
            mLayoutManager = new LinearLayoutManager(this);
        }
        recyclerview.setLayoutManager(mLayoutManager);
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
        recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    // Recycle view scrolling downwards...
                    // this if statement detects when user reaches the end of recyclerView, this is only time we should load more
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                        if (itShouldLoadMore) {
                            //loadMore();
                            int counter = Integer.parseInt(page);
                            int maxPageCount = Integer.parseInt(maxPage);
                            if(counter < maxPageCount) {
                                counter = (counter + 1);
                                page = Integer.toString(counter);
                                //Toast.makeText(getApplicationContext(), "Reached the end of recycler view", Toast.LENGTH_LONG).show();
                                URL = host+"api/search?lang=en&q=&category_id=&featured="+is_featured+"&page="+page+"&per_page=10";
                                getDataFromInternet();
                                //Log.i(TAG, URL);
                            }
                        }
                    }
                }
            }

        });
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        //FETCH DATA FROM REMOTE SERVER
        getDataFromInternet();
    }

    private void getDataFromInternet() {
        itShouldLoadMore = false;
        if (isInternetPresent) {
            new BrowseJsonFromUrlTask(this, URL);
        }
        else{
            AlertDialog.Builder alert = new AlertDialog.Builder(BrowseActivity.this);
            alert.setTitle(R.string.text_warning);
            alert.setMessage(R.string.text_enable_internet);
            alert.setPositiveButton(R.string.text_ok,null);
            alert.show();
        }
    }

    private void setRecyclerViewAdapter() {
        videos = new ArrayList<Video>();
        rvAdapter = new RecyclerViewAdapter(BrowseActivity.this, videos);
        recyclerview.setAdapter(rvAdapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
//            if(double_click_exit.equals("1")) {
//                if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
//                    super.onBackPressed();
//                    return;
//                } else {
//                    Toast.makeText(getBaseContext(), "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
//                }
//                mBackPressed = System.currentTimeMillis();
//            }
//            else{
//                super.onBackPressed();
//            }
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
        else if (id == R.id.nav_categories) {
            Intent i = new Intent(getApplicationContext(), CategoryActivity.class);
            startActivity(i);
            finishAffinity();
        }
        else if (id == R.id.nav_browse) {
            Intent i = new Intent(getApplicationContext(), BrowseActivity.class);
            startActivity(i);
            finishAffinity();
        }
        else if (id == R.id.nav_favorites) {
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
        Log.e(TAG, result);
        try {
            JSONObject response = new JSONObject(result);
            JSONObject json = response.getJSONObject("data");

            JSONArray jArray = new JSONArray(json.getString("videos"));
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
            itShouldLoadMore = true;
            maxPage = json.getString("total_pages");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
