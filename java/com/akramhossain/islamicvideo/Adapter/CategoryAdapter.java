package com.akramhossain.islamicvideo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.akramhossain.islamicvideo.Models.Category;
import com.akramhossain.islamicvideo.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Lenovo on 8/18/2018.
 */

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context c;
    ArrayList<Category> categories;

    public CategoryAdapter(Context c, ArrayList<Category> categories) {
        this.c = c;
        this.categories = categories;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(c).inflate(R.layout.category_list,parent,false);
        CategoryViewHolder rvHolder = new CategoryViewHolder(v);
        return rvHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CategoryViewHolder rvHolder= (CategoryViewHolder) holder;
        Category cg = categories.get(position);
        rvHolder.categoryId.setText(cg.getCategory_id());
        rvHolder.titleTxt.setText(cg.getName());
        Picasso.get().load(cg.getImageUrl()).into(rvHolder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {

        TextView categoryId;
        TextView titleTxt;
        ImageView thumbnail;

        public CategoryViewHolder(View itemView) {
            super(itemView);

            categoryId = (TextView) itemView.findViewById(R.id.category_id);
            titleTxt = (TextView) itemView.findViewById(R.id.title);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);

        }
    }
}
