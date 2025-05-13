package com.publish.monitorsystem.utils;

import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class ImageUtils {
    private static ImageUtils instance;
    private final RequestOptions defaultOptions;

    private ImageUtils() {
        defaultOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop();
    }

    public static ImageUtils getInstance() {
        if (instance == null) {
            instance = new ImageUtils();
        }
        return instance;
    }

    public void loadImage(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .apply(defaultOptions)
                .into(imageView);
    }

    public void loadImage(Context context, String url, ImageView imageView, int placeholderResId) {
        Glide.with(context)
                .load(url)
                .apply(defaultOptions.placeholder(placeholderResId))
                .into(imageView);
    }

    public void loadImage(Context context, String url, ImageView imageView, int placeholderResId, int errorResId) {
        Glide.with(context)
                .load(url)
                .apply(defaultOptions
                        .placeholder(placeholderResId)
                        .error(errorResId))
                .into(imageView);
    }
} 