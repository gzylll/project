package com.teachingassistant.Presentation.Presenter;



import android.util.Log;

import com.teachingassistant.Presentation.Event.GroupEvent;
import com.teachingassistant.Presentation.Event.MessageEvent;
import com.teachingassistant.Presentation.Event.RefreshEvent;
import com.teachingassistant.Presentation.ViewFeatures.ConversationView;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMGroupCacheInfo;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * 会话界面逻辑
 */

public class ConversationPresenter implements Observer {

    private static final String TAG = "ConversationPresenter";
    private ConversationView view;

    public ConversationPresenter(ConversationView view) {
        //注册消息的监听
        MessageEvent.getInstance().addObserver(this);
        //注册刷新监听
        RefreshEvent.getInstance().addObserver(this);
        //注册群关系监听
        GroupEvent.getInstance().addObserver(this);
        this.view = view;
    }

    /**
     * 底层接收到新消息时调用此函数，更新
     * @param observable
     * @param o
     */
    @Override
    public void update(Observable observable, Object o) {
        //判断消息到来
        if(observable instanceof MessageEvent){
            //获取消息
            TIMMessage msg = (TIMMessage)o;
            Log.i("NewMessage:","get");
            //界面更新消息
            view.updateMessage(msg);
        }else if (observable instanceof GroupEvent) {
            GroupEvent.NotifyCmd cmd = (GroupEvent.NotifyCmd) o;
            switch (cmd.type) {
                case UPDATE:
                case ADD:
                    view.updateGroupInfo((TIMGroupCacheInfo) cmd.data);
                    break;
                case DEL:
                    view.removeConversation((String) cmd.data);
                    break;
            }
        }else if(observable instanceof RefreshEvent) {
            view.refresh();
        }
    }

    /**
     * 获取除系统之外的其他会话
     */
    public void getConversation(){
        //获取所有会话列表
        List<TIMConversation> list = TIMManager.getInstance().getConversionList();
        List<TIMConversation> result = new ArrayList<>();
        for (TIMConversation conversation : list){
            //去除系统会话
            if (conversation.getType() == TIMConversationType.System) continue;
            result.add(conversation);
            //获取当前会话最新消息，三个参数：1：从最后一条往前的消息数，
            //                          2，已获得最新消息，传null，从最新的开始
            //                          3，回掉
            conversation.getMessage(1, null, new TIMValueCallBack<List<TIMMessage>>() {
                @Override
                public void onError(int i, String s) {
                    Log.e(TAG, "get message error" + s);
                }

                @Override
                public void onSuccess(List<TIMMessage> timMessages) {
                    if (timMessages.size() > 0) {
                        view.updateMessage(timMessages.get(0));
                    }

                }
            });
        }
        view.initView(result);
    }

    /**
     * 删除会话
     *
     * @param type 会话类型
     * @param id 会话对象id
     */
    public boolean delConversation(TIMConversationType type, String id){
        return TIMManager.getInstance().deleteConversationAndLocalMsgs(type, id);
    }
}
