package com.teachingassistant.Presenter;

import android.content.Context;

import com.teachingassistant.R;
import com.teachingassistant.Support.View.WheelView.adapter.AbstractWheelTextAdapter;

import java.util.ArrayList;

/**
 * Created by 郭振阳 on 2017/2/8.
 */

public class TextWheelAdapter extends AbstractWheelTextAdapter {

    ArrayList<String> list;

    public TextWheelAdapter(Context context, ArrayList<String> list, int currentItem,
                            int maxsize, int minsize) {
        super(context, R.layout.item_wheel, R.id.wheelValue, currentItem, maxsize, minsize);
        this.list = list;
    }

    @Override
    public CharSequence getItemText(int index) {
        return list.get(index);
    }

    @Override
    public int getItemsCount() {
        return list.size();
    }
}
