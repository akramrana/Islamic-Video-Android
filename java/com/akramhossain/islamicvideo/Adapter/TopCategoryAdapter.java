package com.akramhossain.islamicvideo.Adapter;

/**
 * Created by Lenovo on 8/15/2018.
 */
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import com.akramhossain.islamicvideo.Models.Category;
import com.squareup.picasso.Picasso;
import com.akramhossain.islamicvideo.R;

public class TopCategoryAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context c;
    ArrayList<Category> categories;

    public TopCategoryAdapter(Context c, ArrayList<Category> categories) {
        this.c = c;
        this.categories = categories;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(c).inflate(R.layout.top_categories,parent,false);
        CategoryViewHolder rvHolder = new CategoryViewHolder(v);
        return rvHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CategoryViewHolder rvHolder= (CategoryViewHolder) holder;
        Category cg = categories.get(position);
        rvHolder.categoryId.setText(cg.getCategory_id());
        rvHolder.titleTxt.setText(cg.getName());
        Picasso.with(c).load(cg.getImageUrl()).into(rvHolder.thumbnail);
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
