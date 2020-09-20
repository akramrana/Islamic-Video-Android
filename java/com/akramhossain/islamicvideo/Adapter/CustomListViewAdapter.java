package com.akramhossain.islamicvideo.Adapter;

/**
 * Created by Lenovo on 8/4/2018.
 */
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.akramhossain.islamicvideo.Models.Book;
import com.akramhossain.islamicvideo.R;
import com.squareup.picasso.Picasso;

public class CustomListViewAdapter extends ArrayAdapter<Book> {

    private Activity activity;

    public CustomListViewAdapter(Activity activity, int resource, List<Book> books) {
        super(activity, resource, books);
        this.activity = activity;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        // If holder not exist then locate all view from UI file.
        if (convertView == null) {
            // inflate UI from XML file
            convertView = inflater.inflate(R.layout.item_listview, parent, false);
            // get all UI view
            holder = new ViewHolder(convertView);
            // set tag for holder
            convertView.setTag(holder);
        } else {
            // if holder created, get tag from view
            holder = (ViewHolder) convertView.getTag();
        }

        Book book = getItem(position);

        holder.name.setText(book.getName());
        holder.authorName.setText(book.getAuthorName());
        Picasso.get().load(book.getImageUrl()).into(holder.image);

        return convertView;
    }

    private static class ViewHolder {
        private TextView name;
        private TextView authorName;
        private ImageView image;

        public ViewHolder(View v) {
            name = (TextView) v.findViewById(R.id.title);
            image = (ImageView) v.findViewById(R.id.thumbnail);
            authorName = (TextView) v.findViewById(R.id.author);
        }
    }
}
