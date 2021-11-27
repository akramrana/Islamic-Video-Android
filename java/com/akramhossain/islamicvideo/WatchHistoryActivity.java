package com.akramhossain.islamicvideo;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.akramhossain.islamicvideo.Adapter.RecyclerViewAdapter;
import com.akramhossain.islamicvideo.Adapter.WatchHistoryViewAdapter;
import com.akramhossain.islamicvideo.Config.ConnectionDetector;
import com.akramhossain.islamicvideo.Config.Db;
import com.akramhossain.islamicvideo.Config.DeviceDetector;
import com.akramhossain.islamicvideo.Listener.RecyclerTouchListener;
import com.akramhossain.islamicvideo.Models.Video;
import com.google.android.material.navigation.NavigationView;

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
 * Created by Lenovo on 8/26/2018.
 */

public class WatchHistoryActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static final String TAG = BrowseActivity.class.getSimpleName();

    private RecyclerView recyclerview;
    private ArrayList<Video> videos;
    private WatchHistoryViewAdapter rvAdapter;

    LinearLayoutManager mLayoutManager;
    Db dbhelper;
    Cursor c;
    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;

    private boolean itShouldLoadMore = true;
    Integer offset = 0;
    Integer limit = 10;
    Integer counter = 0;
    TextView clear_history_title;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    DeviceDetector dd;
    Boolean isTablet = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Toast.makeText(getApplicationContext(), categoryId + " is selected!", Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_watch_history);
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

        recyclerview = (RecyclerView) findViewById(R.id.watch_history_video_list);
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
                            SQLiteDatabase db = dbhelper.getWritableDatabase();
                            String sql = "SELECT COUNT(*) FROM \n" +
                                    "(select t1.*\n" +
                                    "from watch_history AS t1\n" +
                                    "LEFT OUTER JOIN watch_history AS t2 ON t1.video_id = t2.video_id AND (t1.watch_history_id < t2.watch_history_id OR (t1.watch_history_id < t2.watch_history_id))\n" +
                                    "group by t1.video_id\n" +
                                    "order by t1.watch_history_id desc) ";
                            Cursor countHistory = db.rawQuery(sql,null);
                            countHistory.moveToFirst();
                            int maxHistoryCount = countHistory.getInt(0);
                            countHistory.close();
                            int maxPageCount = (int) Math.ceil(maxHistoryCount/limit);

                            //Log.i(TAG, Integer.toString(maxPageCount));
                            //Log.i(TAG, Integer.toString(counter));

                            if(counter < maxPageCount) {
                                counter = (counter + 1);
                                offset = offset+limit;
                                getDataFromLocalDb();
                                //Log.i(TAG, URL);
                            }
                        }
                    }
                }
            }

        });

        clear_history_title = (TextView) findViewById(R.id.clear_history_title);
        clear_history_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SQLiteDatabase db = dbhelper.getWritableDatabase();
                    String sql = "SELECT * FROM watch_history";
                    Cursor cursor = db.rawQuery(sql,null);
                    if (cursor.moveToFirst()) {
                        String sql1 = "DELETE FROM watch_history";
                        db.execSQL(sql1);
                        Toast.makeText(getApplicationContext(), "Watch history cleared.", Toast.LENGTH_LONG).show();
                        setRecyclerViewAdapter();
                    }

                } catch (Exception e) {
                    Log.e("Favorite", "Delete failed");
                }
            }
        });

        dbhelper = new Db(getApplicationContext());
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        //FETCH DATA FROM LOCAL DB
        getDataFromLocalDb();
    }

    private void getDataFromLocalDb() {
        //itShouldLoadMore = false;
        if (isInternetPresent) {
            SQLiteDatabase db = dbhelper.getWritableDatabase();
            String sql = "select t1.*\n" +
                    "from watch_history AS t1\n" +
                    "LEFT OUTER JOIN watch_history AS t2 ON t1.video_id = t2.video_id AND (t1.watch_history_id < t2.watch_history_id OR (t1.watch_history_id < t2.watch_history_id))\n" +
                    "group by t1.video_id\n" +
                    "order by t1.watch_history_id desc " +
                    "limit " + offset + "," + limit;
            Log.i(TAG, sql);
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                do {
                    Video video = new Video();
                    video.setVideo_id(cursor.getString(cursor.getColumnIndex("video_id")));
                    video.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    video.setCategory_name(cursor.getString(cursor.getColumnIndex("category_name")));
                    video.setImage(cursor.getString(cursor.getColumnIndex("image")));
                    videos.add(video);
                } while (cursor.moveToNext());
            }
            db.close();
            rvAdapter.notifyDataSetChanged();
        }
        else{
            AlertDialog.Builder alert = new AlertDialog.Builder(WatchHistoryActivity.this);
            alert.setTitle(R.string.text_warning);
            alert.setMessage(R.string.text_enable_internet);
            alert.setPositiveButton(R.string.text_ok,null);
            alert.show();
        }
    }

    private void setRecyclerViewAdapter() {
        videos = new ArrayList<Video>();
        rvAdapter = new WatchHistoryViewAdapter(WatchHistoryActivity.this, videos);
        recyclerview.setAdapter(rvAdapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
//            if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
//            {
//                super.onBackPressed();
//                return;
//            }
//            else { Toast.makeText(getBaseContext(), "Please click BACK again to exit", Toast.LENGTH_SHORT).show(); }
//            mBackPressed = System.currentTimeMillis();
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
        }else if (id == R.id.nav_feed_back) {
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
}
