package com.cinebrah.cinebrah.activities.drawer;

import android.view.View;
import android.widget.TextView;

import com.cinebrah.cinebrah.R;

import butterknife.InjectView;

/**
 * Created by Taylor on 11/23/2014.
 */
public class DrawerHeader extends DrawerItem {

    final static int resourceId = R.layout.drawer_header_item;

    public DrawerHeader(String title) {
        super(title);
    }

    @Override
    public int getLayoutResource() {
        return resourceId;
    }

    @Override
    public ViewHolder createHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    public static class HeaderViewHolder extends ViewHolder {

        @InjectView(R.id.drawerTitle)
        TextView headerText;

        public HeaderViewHolder(View convertView) {
            super(convertView);
        }

        @Override
        public void render(DrawerItem item) {
            headerText.setText(item.getTitle());
        }
    }

}
