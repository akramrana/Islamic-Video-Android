package com.akramhossain.islamicvideo;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.akramhossain.islamicvideo.Adapter.CategoryAdapter;
import com.akramhossain.islamicvideo.Adapter.GridViewAdapter;
import com.akramhossain.islamicvideo.Config.ConnectionDetector;
import com.akramhossain.islamicvideo.Listener.RecyclerTouchListener;
import com.akramhossain.islamicvideo.Models.Category;
import com.akramhossain.islamicvideo.Models.GridItem;
import com.akramhossain.islamicvideo.Tasks.CategoryJsonFromUrlTask;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.akramhossain.islamicvideo.Config.Main.host;

/**
 * Created by Lenovo on 8/5/2018.
 */

public class CategoryActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = CategoryActivity.class.getSimpleName();
    //private static final String FEED_URL = "http://www.json-generator.com/api/json/get/ccLAsEcOSq?indent=2";
    private static final String FEED_URL = host+"api/categories";

    //
    private RecyclerView categoryView;
    private ArrayList<Category> categories;
    private CategoryAdapter catAdapter;

    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;
    String double_click_exit = "1";
    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            double_click_exit = extras.getString("double_click_exit");
        }

        setContentView(R.layout.activity_category);
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

        categoryView = (RecyclerView) findViewById(R.id.categoryview);
        categoryView.setLayoutManager(new GridLayoutManager(this,2));
        setCategoryViewAdapter();

        categoryView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), categoryView, new RecyclerTouchListener.ClickListener() {
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
    }

    private void setCategoryViewAdapter() {
        categories = new ArrayList<Category>();
        catAdapter = new CategoryAdapter(CategoryActivity.this, categories);
        categoryView.setAdapter(catAdapter);
    }

    private void getDataFromInternet() {
        if (isInternetPresent) {
            new CategoryJsonFromUrlTask(this, FEED_URL).execute();
        }
        else {
            AlertDialog.Builder alert = new AlertDialog.Builder(CategoryActivity.this);
            alert.setTitle(R.string.text_warning);
            alert.setMessage(R.string.text_enable_internet);
            alert.setPositiveButton(R.string.text_ok,null);
            alert.show();
        }
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
        Log.i(TAG, result);
        try {
            JSONObject response = new JSONObject(result);
            JSONArray jArray = new JSONArray(response.getString("data"));
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                //categories
                Category ct = new Category();
                ct.setCategory_id(jObject.getString("id"));
                ct.setName(jObject.getString("name"));
                ct.setImageUrl(jObject.getString("icon"));
                categories.add(ct);
            }
            catAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
