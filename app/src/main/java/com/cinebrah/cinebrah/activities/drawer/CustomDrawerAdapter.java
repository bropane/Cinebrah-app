package com.cinebrah.cinebrah.activities.drawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class CustomDrawerAdapter extends BaseAdapter {

    Context context;
    ArrayList<DrawerItem> drawerItemList;
    LayoutInflater layoutInflater;

    public CustomDrawerAdapter(Context context) {
        this.context = context;
        this.drawerItemList = new ArrayList<DrawerItem>();
        ;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return drawerItemList.size();
    }

    @Override
    public DrawerItem getItem(int i) {
        DrawerItem result = null;
        if (i < drawerItemList.size()) {
            result = drawerItemList.get(i);
        }
        return result;
    }

    public void add(DrawerItem item) {
        drawerItemList.add(item);
        notifyDataSetChanged();
    }

    public void add(DrawerItem item, int position) {
        drawerItemList.add(position, item);
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<DrawerItem> items) {
        drawerItemList = items;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return drawerItemList.get(position).isEnabled();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        DrawerItem drawerItem = drawerItemList.get(position);
        View view = convertView;
        ViewHolder viewHolder;

        if (view == null) {
            int layoutResID = drawerItem.getLayoutResource();
            view = layoutInflater.inflate(layoutResID, parent, false);
            viewHolder = drawerItem.createHolder(view);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.render(drawerItem);
        return view;
    }
}