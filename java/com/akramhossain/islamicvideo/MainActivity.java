package com.akramhossain.islamicvideo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akramhossain.islamicvideo.Adapter.RecentlyWatchViewAdapter;
import com.akramhossain.islamicvideo.Adapter.RecyclerViewAdapter;
import com.akramhossain.islamicvideo.Adapter.TopCategoryAdapter;
import com.akramhossain.islamicvideo.Config.ConnectionDetector;
import com.akramhossain.islamicvideo.Listener.RecyclerTouchListener;
import com.akramhossain.islamicvideo.Models.Book;
import com.akramhossain.islamicvideo.Models.RecentlyWatched;
import com.akramhossain.islamicvideo.Models.Category;

import com.akramhossain.islamicvideo.Adapter.CustomListViewAdapter;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.akramhossain.islamicvideo.Models.Video;
import com.akramhossain.islamicvideo.Tasks.GetJsonFromUrlTask;

import static com.akramhossain.islamicvideo.Config.Main.host;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView listview;
    private RecyclerView recyclerview;
    private RecyclerView recentlyWatchView;
    private RecyclerView topCategoryView;

    private ArrayList<Book> books;
    private ArrayList<Video> videos;
    private ArrayList<RecentlyWatched> recentlyWatches;
    private ArrayList<Category> categories;

    private ArrayAdapter<Book> adapter;

    private RecyclerViewAdapter rvAdapter;
    private RecentlyWatchViewAdapter rwAdapter;
    private TopCategoryAdapter tcAdapter;

    private final static String TAG = MainActivity.class.getSimpleName();
    //private final static String url = "http://www.json-generator.com/api/json/get/ccLAsEcOSq?indent=2";
    private final static String url = host+"api/home";

    private RelativeLayout recently_watched_section;
    private RelativeLayout top_category_section;
    private RelativeLayout featured_section;
    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*listview = (ListView) findViewById(R.id.listview);
        setListViewAdapter();
        getDataFromInternet();*/

        recently_watched_section = (RelativeLayout) findViewById(R.id.recently_watched_section);
        top_category_section = (RelativeLayout) findViewById(R.id.top_category_section);
        featured_section = (RelativeLayout) findViewById(R.id.featured_section);

        //FEATURED VIDEO LIST
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
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

        //RECENTLY WATCHED VIDEO
        recentlyWatchView = (RecyclerView) findViewById(R.id.recentlyWatchView);
        LinearLayoutManager rLinearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        recentlyWatchView.setLayoutManager(rLinearLayoutManager);
        setRecentlyWatchViewAdapter();
        recentlyWatchView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recentlyWatchView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                RecentlyWatched rw = recentlyWatches.get(position);
                //Toast.makeText(getApplicationContext(), rw.getVideo_id() + " is selected!", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(getApplicationContext(),VideoPlayActivity.class);
                in.putExtra("video_id", rw.getVideo_id());
                in.putExtra("video_name", rw.getName());
                startActivityForResult(in, 100);
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        //TOP CATEGORIES
        topCategoryView = (RecyclerView) findViewById(R.id.topCategoryView);
        topCategoryView.setLayoutManager(new GridLayoutManager(this,3));
        setTopCategoryViewAdapter();
        topCategoryView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), topCategoryView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Category cat = categories.get(position);
                //Toast.makeText(getApplicationContext(), cat.getCategory_id() + " is selected!", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(getApplicationContext(),CategoryDetailActivity.class);
                in.putExtra("category_id", cat.getCategory_id());
                in.putExtra("category_name", cat.getName());
                startActivityForResult(in, 100);
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        //FETCH DATA FROM REMOTE SERVER
        getDataFromInternet();

        TextView categoryViewAll = (TextView) findViewById(R.id.category_view_all);
        TextView featuredViewAll = (TextView) findViewById(R.id.featured_view_all);
        categoryViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CategoryActivity.class);
                i.putExtra("double_click_exit","0");
                startActivity(i);
            }
        });
        featuredViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), BrowseActivity.class);
                i.putExtra("is_featured", "1");
                i.putExtra("double_click_exit","0");
                startActivity(i);
            }
        });

        Button button = findViewById(R.id.btn_browse_all);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), BrowseActivity.class);
                startActivity(i);
            }
        });

    }

    private void getDataFromInternet() {
        if (isInternetPresent) {
            new GetJsonFromUrlTask(this, url).execute();
        }else {
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setTitle(R.string.text_warning);
            alert.setMessage(R.string.text_enable_internet);
            alert.setPositiveButton(R.string.text_ok,null);
            alert.show();
            /*Toast.makeText(getApplicationContext(),
                    "Please enable internet connection",
                    Toast.LENGTH_LONG).show();*/
        }
    }

    private void setRecyclerViewAdapter() {
        videos = new ArrayList<Video>();
        rvAdapter = new RecyclerViewAdapter(MainActivity.this, videos);
        recyclerview.setAdapter(rvAdapter);
    }

    private void setRecentlyWatchViewAdapter() {
        recentlyWatches = new ArrayList<RecentlyWatched>();
        rwAdapter = new RecentlyWatchViewAdapter(MainActivity.this, recentlyWatches);
        recentlyWatchView.setAdapter(rwAdapter);
    }

    private void setTopCategoryViewAdapter() {
        categories = new ArrayList<Category>();
        tcAdapter = new TopCategoryAdapter(MainActivity.this, categories);
        topCategoryView.setAdapter(tcAdapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
            {
                super.onBackPressed();
                return;
            }
            else { Toast.makeText(getBaseContext(), "Please click BACK again to exit", Toast.LENGTH_SHORT).show(); }
            mBackPressed = System.currentTimeMillis();
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

        if (id == R.id.nav_categories) {
            Intent i = new Intent(getApplicationContext(), CategoryActivity.class);
            startActivity(i);
            //finishAffinity();
        }
        else if (id == R.id.nav_browse) {
            Intent i = new Intent(getApplicationContext(), BrowseActivity.class);
            startActivity(i);
            //finishAffinity();
        }
        else if (id == R.id.nav_favorites) {
            Intent i = new Intent(getApplicationContext(), FavoriteActivity.class);
            startActivity(i);
            //finishAffinity();
        } else if (id == R.id.nav_recently_watched) {
            Intent i = new Intent(getApplicationContext(), WatchHistoryActivity.class);
            startActivity(i);
            //finishAffinity();
        } else if (id == R.id.nav_feed_back) {
            Intent i = new Intent(getApplicationContext(), HelpFeedbackActivity.class);
            startActivity(i);
            //finishAffinity();
        } else if (id == R.id.nav_about) {
            Intent i = new Intent(getApplicationContext(), CmsActivity.class);
            i.putExtra("cms_title", "About Us");
            i.putExtra("cms_page", "about-us");
            startActivity(i);
            //finishAffinity();
        } else if (id == R.id.nav_terms) {
            Intent i = new Intent(getApplicationContext(), CmsActivity.class);
            i.putExtra("cms_title", "Terms and Conditions");
            i.putExtra("cms_page", "terms");
            startActivity(i);
            //finishAffinity();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void parseJsonResponse(String result) {
        //Log.i(TAG, result);
        try {
            JSONObject res = new JSONObject(result);
            JSONObject json = res.getJSONObject("data");

            JSONArray jArray = new JSONArray(json.getString("top_categories"));
            JSONArray jArray1 = new JSONArray(json.getString("recently_watched"));
            JSONArray jArray2 = new JSONArray(json.getString("featured_videos"));

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                //top categories
                Category ct = new Category();
                ct.setCategory_id(jObject.getString("id"));
                ct.setName(jObject.getString("name"));
                ct.setImageUrl(jObject.getString("icon"));
                categories.add(ct);
            }

            for (int i = 0; i < jArray1.length(); i++) {
                JSONObject jObject = jArray1.getJSONObject(i);
                //recently watched videos
                RecentlyWatched rw = new RecentlyWatched();
                rw.setVideo_id(jObject.getString("id"));
                rw.setName(jObject.getString("title"));
                rw.setImageUrl(jObject.getString("image"));
                recentlyWatches.add(rw);
            }

            for (int i = 0; i < jArray2.length(); i++) {
                JSONObject jObject = jArray2.getJSONObject(i);
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
            rwAdapter.notifyDataSetChanged();
            tcAdapter.notifyDataSetChanged();

            recently_watched_section.setVisibility(View.VISIBLE);
            top_category_section.setVisibility(View.VISIBLE);
            featured_section.setVisibility(View.VISIBLE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
