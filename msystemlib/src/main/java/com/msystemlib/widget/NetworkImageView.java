package com.msystemlib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class NetworkImageView extends ImageView
{
  private DisplayImageOptions mOptions = new DisplayImageOptions.Builder()
    .cacheInMemory(true)
    .cacheOnDisc(true)
    .build();

  private ImageLoader mImageLoader = ImageLoader.getInstance();
  private ImageLoadingListener mListener;

  public NetworkImageView(Context context)
  {
    super(context);
  }

  public NetworkImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public NetworkImageView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public void setOptions(DisplayImageOptions options) {
    this.mOptions = options;
  }

  public void setDefaultImage(int resId) {
    if (resId != this.mOptions.getStubImage())
      this.mOptions = new DisplayImageOptions.Builder().cloneFrom(this.mOptions).showStubImage(resId)
        .showImageForEmptyUri(resId)
        .showImageOnFail(resId).build();
  }

  public void setCacheInMemory(boolean cache)
  {
    if ((cache | this.mOptions.isCacheInMemory()))
      this.mOptions = new DisplayImageOptions.Builder().cloneFrom(this.mOptions).cacheInMemory(cache).build();
  }

  public void setCacheOnDisc(boolean cache)
  {
    if ((cache | this.mOptions.isCacheInMemory()))
      this.mOptions = new DisplayImageOptions.Builder().cloneFrom(this.mOptions).cacheOnDisc(cache).build();
  }

  public void setImageUrl(String imgUrl)
  {
    this.mImageLoader.cancelDisplayTask(this);

    if (imgUrl == null) {
      imgUrl = "";
    }
    this.mImageLoader.displayImage(imgUrl, this, this.mOptions, this.mListener);
  }

  public void setImageLoadingListener(ImageLoadingListener listener) {
    this.mListener = listener;
  }
}