package com.akramhossain.islamicvideo.Adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.akramhossain.islamicvideo.Config.Db;
import com.akramhossain.islamicvideo.Models.Video;
import com.akramhossain.islamicvideo.R;
import com.akramhossain.islamicvideo.VideoPlayActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Lenovo on 8/29/2018.
 */

public class FavoriteViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context c;
    ArrayList<Video> videos;
    Db dbhelper;
    Typeface font;

    public FavoriteViewAdapter(Context c, ArrayList<Video> videos) {
        this.c = c;
        this.videos = videos;
        dbhelper = new Db(c);
        font = Typeface.createFromAsset(c.getAssets(),"fonts/Siyamrupali.ttf");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(c).inflate(R.layout.favorite_videos,parent,false);
        RecyclerViewHolder rvHolder = new RecyclerViewHolder(v);
        return rvHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final RecyclerViewHolder rvHolder= (RecyclerViewHolder) holder;
        final Video video = videos.get(position);
        rvHolder.videoId.setText(video.getVideo_id());
        rvHolder.titleTxt.setText(video.getTitle());
        rvHolder.categoryNameTxt.setText(video.getCategory_name());
        Picasso.with(c).load(video.getImage()).into(rvHolder.thumbnail);
        rvHolder.buttonViewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(c, rvHolder.buttonViewOption);
                popup.inflate(R.menu.options_menu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu1:
                                //handle menu1 click
                                try{
                                    SQLiteDatabase db = dbhelper.getWritableDatabase();
                                    String sql = "SELECT * FROM favorite WHERE video_id = "+video.getVideo_id();
                                    //Log.e("Remove Favorite", sql);
                                    Cursor cursor = db.rawQuery(sql,null);
                                    if (cursor.moveToFirst()) {
                                        db.execSQL("DELETE FROM favorite WHERE video_id = "+video.getVideo_id());
                                        Toast.makeText(c, "Video removed from favorite.", Toast.LENGTH_LONG).show();
                                        videos.remove(rvHolder.getAdapterPosition());
                                        notifyItemRemoved(rvHolder.getAdapterPosition());
                                    }
                                }catch (Exception e) {
                                    Log.e("Favorite", "remove failed");
                                }
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();

            }
        });
        rvHolder.titleTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(c,VideoPlayActivity.class);
                in.putExtra("video_id", video.getVideo_id());
                in.putExtra("video_name", video.getTitle());
                c.startActivity(in);
            }
        });
        rvHolder.categoryNameTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(c,VideoPlayActivity.class);
                in.putExtra("video_id", video.getVideo_id());
                in.putExtra("video_name", video.getTitle());
                c.startActivity(in);
            }
        });
        rvHolder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(c,VideoPlayActivity.class);
                in.putExtra("video_id", video.getVideo_id());
                in.putExtra("video_name", video.getTitle());
                c.startActivity(in);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView titleTxt;
        TextView categoryNameTxt;
        ImageView thumbnail;
        TextView videoId;
        TextView buttonViewOption;


        public RecyclerViewHolder(View itemView) {
            super(itemView);

            videoId = (TextView) itemView.findViewById(R.id.video_id);
            titleTxt = (TextView) itemView.findViewById(R.id.title);
            titleTxt.setTypeface(font);
            categoryNameTxt = (TextView) itemView.findViewById(R.id.categoryName);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            buttonViewOption = (TextView) itemView.findViewById(R.id.textViewOptions);
        }
    }
}
