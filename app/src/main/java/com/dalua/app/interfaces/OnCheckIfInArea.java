package com.dalua.app.interfaces;

import com.dalua.app.models.AquariumGroup;
import com.dalua.app.ui.aquariumdetails.GroupPaginationAdapter;

public interface OnCheckIfInArea {
    void onOptionFollowed(
            Boolean is_in_area,
            String group_type,
            GroupPaginationAdapter.AquariumViewHolder holder);

    void onItemDroped(
            Boolean is_in_area,
            String group_type,
            GroupPaginationAdapter.AquariumViewHolder item_viewHolder,
            AquariumGroup group
    );
}
