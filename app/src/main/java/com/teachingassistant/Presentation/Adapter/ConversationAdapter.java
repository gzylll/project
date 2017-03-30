package com.teachingassistant.Presentation.Adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.teachingassistant.R;
import com.teachingassistant.Bean.Conversation;
import com.teachingassistant.Bean.Time;
import com.teachingassistant.Support.CustomView.IMView.CircleImageView;

import java.util.List;

/**
 * Created by 11650 on 2017/3/18.
 */

public class ConversationAdapter extends ArrayAdapter<Conversation> {

    private int resourceId;
    private View view;
    private ViewHolder viewHolder;

    public ConversationAdapter(@NonNull Context context, @LayoutRes int resource,
                               @NonNull List<Conversation> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView != null){
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }else{
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView) view.findViewById(R.id.name);
            viewHolder.avatar = (CircleImageView) view.findViewById(R.id.avatar);
            viewHolder.lastMessage = (TextView) view.findViewById(R.id.last_message);
            viewHolder.time = (TextView) view.findViewById(R.id.message_time);
            viewHolder.unread = (TextView) view.findViewById(R.id.unread_num);
            view.setTag(viewHolder);
        }
        //填充数据
        final Conversation data = getItem(position);
        viewHolder.tvName.setText(data.getName());
        viewHolder.avatar.setImageResource(data.getAvatar());
        viewHolder.lastMessage.setText(data.getLastMessageSummary());
        viewHolder.time.setText(Time.getTimeStr(data.getLastMessageTime()));
        long unRead = data.getUnreadNum();
        if (unRead <= 0){
            viewHolder.unread.setVisibility(View.INVISIBLE);
        }else{
            viewHolder.unread.setVisibility(View.VISIBLE);
            String unReadStr = String.valueOf(unRead);
            if (unRead < 10){
                viewHolder.unread.setBackground(getContext().getResources().getDrawable(R.mipmap.point1));
            }else{
                viewHolder.unread.setBackground(getContext().getResources().getDrawable(R.mipmap.point2));
                if (unRead > 99){
                    unReadStr = "99+";
                }
            }
            viewHolder.unread.setText(unReadStr);
        }
        return view;
    }

    public class ViewHolder{
        public TextView tvName;
        public CircleImageView avatar;
        public TextView lastMessage;
        public TextView time;
        public TextView unread;
    }
}
