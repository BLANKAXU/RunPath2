package com.example.blanka.runpath.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.blanka.runpath.R;

/**
 * Created by BLANKA on 2017/7/5 0005.
 */

public class Store_Fragment extends Fragment {
    private ImageView image_shot;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_non, container, false);
        image_shot = (ImageView) view.findViewById(R.id.image_shot);
        Glide.with(this).load(R.drawable.shot).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(image_shot);
        return view;
    }
}
