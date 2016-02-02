package com.dudu.android.launcher.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dudu.android.launcher.R;
import com.dudu.android.launcher.model.WindowMessageEntity;

import java.util.List;

/**
 * Created by Administrator on 2016/1/4.
 */
public class MessageAdapter extends BaseAdapter {

    private Context mContext;

    private List<WindowMessageEntity> mMessageData;

    public MessageAdapter(Context context, List<WindowMessageEntity> messageData) {
        mContext = context;
        mMessageData = messageData;
    }

    public void addMessage(WindowMessageEntity message) {
        if (mMessageData != null) {
            mMessageData.add(message);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mMessageData.size();
    }

    @Override
    public Object getItem(int position) {
        return mMessageData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.list_message_item_layout, parent, false);
            holder.leftView = convertView.findViewById(R.id.list_message_item_left);
            holder.rightView = convertView.findViewById(R.id.list_message_item_right);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        WindowMessageEntity message = mMessageData.get(position);

        TextView content;
        switch (message.getType()) {
            case MESSAGE_INPUT:
                holder.leftView.setVisibility(View.VISIBLE);
                holder.rightView.setVisibility(View.GONE);
                content = (TextView) holder.leftView.findViewById(R.id.tv_chatcontent);
                content.setText(message.getContent());
                break;
            case MESSAGE_OUTPUT:
                holder.leftView.setVisibility(View.GONE);
                holder.rightView.setVisibility(View.VISIBLE);
                content = (TextView) holder.rightView.findViewById(R.id.tv_chatcontent);
                content.setText(message.getContent());
                break;
        }

        return convertView;
    }

    static class ViewHolder {
        View leftView;
        View rightView;
    }
}
