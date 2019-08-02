package com.stratagile.pnrouter.ui.activity.email.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.stratagile.pnrouter.R;
import com.stratagile.pnrouter.view.ImageButtonWithText;

import java.util.List;

public class SortAdapter extends BaseAdapter implements SectionIndexer {
    private List<ContactSortModel> list = null;
    private Context mContext;

    public SortAdapter(Context mContext, List<ContactSortModel> list) {
        this.mContext = mContext;
        this.list = list;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<ContactSortModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        final ContactSortModel mContent = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.email_item_contact, null);
            viewHolder.tvLetter = (TextView) view.findViewById(R.id.tvLetter);
            viewHolder.friendName = (TextView) view.findViewById(R.id.friendName);
            viewHolder.friendAdress = (TextView) view.findViewById(R.id.friendAdress);
            viewHolder.ivAvatar = (ImageButtonWithText) view.findViewById(R.id.avatar);
            viewHolder.checkBox = (CheckBox) view.findViewById(R.id.checkBox);

            //设置convertView的LayoutParams
           /* view.setLayoutParams(new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 285)
            );*/
            view.setTag(viewHolder);
            //viewHolder.tvLetter = (TextView) view.findViewById(R.id.tv_catagory);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        int section = getSectionForPosition(position);

        if (position == getPositionForSection(section)) {
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setText(mContent.getSortLetters());
        } else {
            viewHolder.tvLetter.setVisibility(View.GONE);
        }
        viewHolder.checkBox.setChecked(this.list.get(position).isChoose());
        viewHolder.friendName.setText(this.list.get(position).getName());
        viewHolder.friendAdress.setText(this.list.get(position).getAccount());
        viewHolder.ivAvatar.setText(this.list.get(position).getName());
        return view;

    }


    final static class ViewHolder {
        //TextView tvLetter;
        ImageButtonWithText ivAvatar;
        TextView tvLetter;
        TextView friendName;
        TextView friendAdress;
        CheckBox checkBox;

    }

    public int getSectionForPosition(int position) {
        return list.get(position).getSortLetters().charAt(0);
    }

    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public Object[] getSections() {
        return null;
    }
}