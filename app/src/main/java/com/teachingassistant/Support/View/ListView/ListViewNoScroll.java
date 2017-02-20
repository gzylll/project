package com.teachingassistant.Support.View.ListView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 屏蔽了滑动事件的listview
 * Created by 郭振阳 on 2017/2/3.
 */

public class ListViewNoScroll extends ListView {
    public ListViewNoScroll(Context context) {
        super(context);
    }

    public ListViewNoScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
