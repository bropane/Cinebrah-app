package com.cinebrah.cinebrah.activities.drawer;

import android.view.View;

public abstract class DrawerItem {
    /**
     * Parent class of any item to be shown in the navigation drawer
     */

    String title;

    protected DrawerItem(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public abstract int getLayoutResource();

    public abstract ViewHolder createHolder(View view);

    public boolean isEnabled() {
        return true;
    }

}
