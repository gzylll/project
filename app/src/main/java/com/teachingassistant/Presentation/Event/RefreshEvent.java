package com.teachingassistant.Presentation.Event;

import com.tencent.TIMConversation;
import com.tencent.TIMManager;
import com.tencent.TIMRefreshListener;

import java.util.List;
import java.util.Observable;

/**
 * 刷新事件，供上层订阅
 */

public class RefreshEvent extends Observable implements TIMRefreshListener {

    private volatile static RefreshEvent instance;

    public RefreshEvent(){
        TIMManager.getInstance().setRefreshListener(this);
    }

    public static RefreshEvent getInstance(){
        if (instance == null) {
            synchronized (RefreshEvent.class) {
                if (instance == null) {
                    instance = new RefreshEvent();
                }
            }
        }
        return instance;
    }

    /**
     * 数据刷新通知，如未读技术、会话列表等
     */
    @Override
    public void onRefresh() {
        setChanged();
        notifyObservers();
    }


    @Override
    public void onRefreshConversation(List<TIMConversation> list) {

    }
}
