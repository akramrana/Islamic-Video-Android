package com.akramhossain.islamicvideo.Adapter;

/**
 * Created by Lenovo on 8/10/2018.
 */
import android.content.Context;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import com.akramhossain.islamicvideo.Models.Video;
import com.squareup.picasso.Picasso;
import com.akramhossain.islamicvideo.R;

import org.w3c.dom.Text;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context c;
    ArrayList<Video> videos;
    Typeface font;

    public RecyclerViewAdapter(Context c, ArrayList<Video> videos) {
        this.c = c;
        this.videos = videos;
        font = Typeface.createFromAsset(c.getAssets(),"fonts/Siyamrupali.ttf");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(c).inflate(R.layout.main_videos,parent,false);
        RecyclerViewHolder rvHolder = new RecyclerViewHolder(v);
        rvHolder.titleTxt.setTypeface(font);
        return rvHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecyclerViewHolder rvHolder= (RecyclerViewHolder) holder;
        Video video = videos.get(position);
        rvHolder.videoId.setText(video.getVideo_id());
        rvHolder.titleTxt.setText(video.getTitle());
        rvHolder.categoryNameTxt.setText(video.getCategory_name());
        rvHolder.views.setText(video.getViews()+" views");
        Picasso.get().load(video.getImage()).into(rvHolder.thumbnail);
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
        TextView views;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            videoId = (TextView) itemView.findViewById(R.id.video_id);
            titleTxt = (TextView) itemView.findViewById(R.id.title);
            titleTxt.setTypeface(font);
            categoryNameTxt = (TextView) itemView.findViewById(R.id.categoryName);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            views = (TextView) itemView.findViewById(R.id.views);

        }
    }
}
