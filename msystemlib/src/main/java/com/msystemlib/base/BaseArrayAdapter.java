package com.msystemlib.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.widget.BaseAdapter;

public abstract class BaseArrayAdapter<T> extends BaseAdapter
{
  private List<T> mList;

  public BaseArrayAdapter()
  {
    this.mList = new ArrayList();
  }

  public BaseArrayAdapter(List<T> list) {
    this.mList = list;
  }

  public int getCount()
  {
    return this.mList.size();
  }

  public T getItem(int position)
  {
    if ((position < 0) || (position >= this.mList.size())) {
      return null;
    }
    return this.mList.get(position);
  }

  public long getItemId(int position)
  {
    return position;
  }

  public void add(T item) {
    this.mList.add(item);
    notifyDataSetChanged();
  }

  public void add(int index, T item) {
    this.mList.add(index, item);
    notifyDataSetChanged();
  }

  public void addAll(Collection<T> array) {
    this.mList.addAll(array);
    notifyDataSetChanged();
  }

  public void set(List<T> list) {
    this.mList = list;
    notifyDataSetChanged();
  }

  public void set(int index, T v) {
    this.mList.set(index, v);
    notifyDataSetChanged();
  }

  public void remove(T item) {
    this.mList.remove(item);
    notifyDataSetChanged();
  }

  public void remove(int i) {
    if ((i < 0) || (i >= this.mList.size())) {
      return;
    }
    this.mList.remove(i);
    notifyDataSetChanged();
  }

  public void clear() {
    this.mList.clear();
    notifyDataSetChanged();
  }
}