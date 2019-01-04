package com.akramhossain.islamicvideo.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import com.akramhossain.islamicvideo.Models.RecentlyWatched;
import com.squareup.picasso.Picasso;
import com.akramhossain.islamicvideo.R;

/**
 * Created by Lenovo on 8/15/2018.
 */

public class RecentlyWatchViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context c;
    ArrayList<RecentlyWatched> recentlyWatches;
    Typeface font;

    public RecentlyWatchViewAdapter(Context c, ArrayList<RecentlyWatched> recentlyWatches) {
        this.c = c;
        this.recentlyWatches = recentlyWatches;
        font = Typeface.createFromAsset(c.getAssets(),"fonts/Siyamrupali.ttf");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(c).inflate(R.layout.recently_watched_videos,parent,false);
        RecentlyViewHolder rvHolder = new RecentlyViewHolder(v);
        return rvHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecentlyViewHolder rvHolder= (RecentlyViewHolder) holder;
        RecentlyWatched rw = recentlyWatches.get(position);
        rvHolder.videoId.setText(rw.getVideo_id());
        rvHolder.titleTxt.setText(rw.getName());
        Picasso.with(c).load(rw.getImageUrl()).into(rvHolder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return recentlyWatches.size();
    }

    class RecentlyViewHolder extends RecyclerView.ViewHolder {

        TextView videoId;
        TextView titleTxt;
        ImageView thumbnail;

        public RecentlyViewHolder(View itemView) {
            super(itemView);

            videoId = (TextView) itemView.findViewById(R.id.video_id);
            titleTxt = (TextView) itemView.findViewById(R.id.title);
            titleTxt.setTypeface(font);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);

        }
    }
}
