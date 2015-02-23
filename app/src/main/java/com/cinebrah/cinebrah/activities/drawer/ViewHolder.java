package com.cinebrah.cinebrah.activities.drawer;

import android.view.View;

import butterknife.ButterKnife;

/**
 * Created by Taylor on 11/24/2014.
 */
public abstract class ViewHolder {

    public final View view;
    public Object tag = null;

    public ViewHolder(View convertView) {
        this.view = convertView;
        convertView.setTag(this);
        ButterKnife.inject(this, convertView);
    }

    public abstract void render(DrawerItem item);
}
