package com.cinebrah.cinebrah.activities.drawer;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cinebrah.cinebrah.R;

import butterknife.InjectView;

/**
 * Created by Taylor on 11/23/2014.
 */
public class DrawerSelection extends DrawerItem {

    final static int resourceId = R.layout.drawer_selection;

    Drawable icon;

    public DrawerSelection(String title, Drawable icon) {
        super(title);
        this.icon = icon;
    }

    public Drawable getIcon() {
        return icon;
    }

    @Override
    public int getLayoutResource() {
        return resourceId;
    }

    @Override
    public ViewHolder createHolder(View view) {
        return new SelectionViewHolder(view);
    }

    public static class SelectionViewHolder extends ViewHolder {

        @InjectView(R.id.drawerTitle)
        TextView titleTV;

        @InjectView(R.id.drawerIcon)
        ImageView iconIV;

        public SelectionViewHolder(View convertView) {
            super(convertView);
        }

        @Override
        public void render(DrawerItem item) {
            DrawerSelection selection = (DrawerSelection) item;
            titleTV.setText(selection.getTitle());
            iconIV.setImageDrawable(selection.getIcon());
        }
    }
}
