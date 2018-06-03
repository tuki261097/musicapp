package com.example.asus.lastdemo;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SongAdapter extends BaseAdapter {

    private ArrayList<String> paths;
    private Context context;
    private LayoutInflater inflater;

    public SongAdapter(Context context, ArrayList<String> paths) {
        this.paths = paths;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return paths.size();
    }

    @Override
    public Object getItem(int position) {
        return paths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = inflater.inflate(R.layout.list_song, parent, false);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.listtitle);
            holder.tvArtist = (TextView) convertView.findViewById(R.id.listsinger);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(paths.get(position));
        String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        holder.tvArtist.setText(artist);
        holder.tvTitle.setText(title);
        return convertView;
    }

    private class Holder {
        TextView tvTitle;
        TextView tvArtist;
    }
}
