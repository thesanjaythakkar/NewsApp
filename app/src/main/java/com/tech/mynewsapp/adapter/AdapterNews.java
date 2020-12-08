package com.tech.mynewsapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.tech.mynewsapp.R;
import com.tech.mynewsapp.model.NewsData;

import java.util.List;

public class AdapterNews extends RecyclerView.Adapter<AdapterNews.OriginalViewHolder> {

    private List<NewsData> listNewsData;
    private OnItemClickListener onItemClickListener;

    public AdapterNews(List<NewsData> items) {
        this.listNewsData = items;
    }

    @NonNull
    @Override
    public OriginalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_news, parent, false);
        return new OriginalViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OriginalViewHolder vItem, int position) {

        final NewsData c = listNewsData.get(position);
        if (c.getMediacontent() != null) {
            Picasso.get().load(c.getMediacontent()).fit().into(vItem.imgview);
        }

        vItem.txt_title.setText(c.getTitle());
        vItem.txt_link.setText(c.getLink());
        vItem.txt_source.setText(c.getSource());

        vItem.card_view.setTag(position);
        vItem.card_view.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, c, Integer.parseInt(v.getTag().toString()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return listNewsData.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, NewsData data, int position);
    }

    public static class OriginalViewHolder extends RecyclerView.ViewHolder {

        public CardView card_view;
        public ImageView imgview;
        public TextView txt_title, txt_link, txt_source;

        public OriginalViewHolder(View v) {
            super(v);

            card_view = (CardView) v.findViewById(R.id.card_view);
            imgview = (ImageView) v.findViewById(R.id.imgview);
            txt_title = (TextView) v.findViewById(R.id.txt_title);
            txt_link = (TextView) v.findViewById(R.id.txt_link);
            txt_source = (TextView) v.findViewById(R.id.txt_source);

        }
    }

}
