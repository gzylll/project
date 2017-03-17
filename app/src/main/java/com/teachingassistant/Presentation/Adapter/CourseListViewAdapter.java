package com.teachingassistant.Presentation.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;

/**
 * Created by 郭振阳 on 2017/2/4.
 */

public class CourseListViewAdapter extends SimpleAdapter {

    private Context context;
    private List<? extends Map<String, ?>> data;
    private LayoutInflater layoutInflater;
    private int itemHeight;

    public CourseListViewAdapter(Context context, List<? extends Map<String, ?>> data,
                                 int resource, String[] from, int[] to,int height) {
        super(context, data, resource, from, to);
        this.context=context;
        this.data=data;
        this.layoutInflater = LayoutInflater.from(context);
        itemHeight = height;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = super.getView(position,convertView,parent);

        ViewGroup.LayoutParams params = convertView.getLayoutParams();
        params.height = itemHeight;
        convertView.setLayoutParams(params);
        return convertView;
    }

}
