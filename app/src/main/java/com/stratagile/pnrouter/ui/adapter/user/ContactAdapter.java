package com.stratagile.pnrouter.ui.adapter.user;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.stratagile.pnrouter.R;

import java.util.List;

public class ContactAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public ContactAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(0, R.layout.layout_contact_list_item);
        addItemType(1, R.layout.layout_contact_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        switch (helper.getItemViewType()) {
            case 0:
                helper.setGone(R.id.checkBox, false);
                final UserHead lv0 = (UserHead) item;
                helper.setText(R.id.tvNickName, lv0.getUserName() + "(" + lv0.getSubItems().size() + ")");
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = helper.getAdapterPosition();
                        if (lv0.isExpanded()) {
                            collapse(pos);
                        } else {
                            expand(pos);
                        }
                    }
                });
                break;
            case 1:
                break;
            default:
                break;
        }
    }
}
