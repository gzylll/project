package com.teachingassistant.Presentation.Presenter;

import android.support.annotation.Nullable;
import android.util.Log;

import com.teachingassistant.Bean.Message;
import com.teachingassistant.Presentation.Event.MessageEvent;
import com.teachingassistant.Presentation.Event.RefreshEvent;
import com.teachingassistant.Presentation.ViewFeatures.ChatView;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageDraft;
import com.tencent.TIMValueCallBack;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * 聊天界面业务逻辑
 */

public class ChatPresenter implements Observer{

    private final static String TAG = "ChatPresenter";
    private ChatView chatView;
    //当前聊天会话
    private TIMConversation conversation;
    //是否获得了消息
    private boolean isGetingMessage = false;
    //加载最新的多少条消息
    private final int LAST_MESSAGE_NUM = 20;

    public ChatPresenter(ChatView view, String identify, TIMConversationType type){
        this.chatView = view;
        conversation = TIMManager.getInstance().getConversation(type,identify);
    }

    /**
     * 加载页面逻辑
     */
    public void start() {
        //注册消息监听
        MessageEvent.getInstance().addObserver(this);
        RefreshEvent.getInstance().addObserver(this);
        //加载消息
        getMessage(null);
        //加载草稿
        if (conversation.hasDraft()){
            chatView.showDraft(conversation.getDraft());
        }

    }

    /**
     * 中止页面逻辑
     */
    public void stop() {
        //注销消息监听
        MessageEvent.getInstance().deleteObserver(this);
        RefreshEvent.getInstance().deleteObserver(this);
    }

    /**
     * 获取聊天TIM会话
     */
    public TIMConversation getConversation(){
        return conversation;
    }

    /**
     * 发送消息
     *
     * @param message 发送的消息
     */
    public void sendMessage(final TIMMessage message) {
        conversation.sendMessage(message, new TIMValueCallBack<TIMMessage>() {
            @Override
            public void onError(int code, String desc) {//发送消息失败
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code含义请参见错误码表
                chatView.onSendMessageFail(code, desc, message);
            }

            @Override
            public void onSuccess(TIMMessage msg) {
                //发送消息成功,消息状态已在sdk中修改，此时只需更新界面
                MessageEvent.getInstance().onNewMessage(null);

            }
        });
        //message对象为发送中状态
        MessageEvent.getInstance().onNewMessage(message);
    }

    /**
     * 检测订阅的事件，当其发生时执行操作
     * @param observable 发生的事件
     * @param o 变化
     */
    @Override
    public void update(Observable observable, Object o) {
        if(observable instanceof MessageEvent){
            TIMMessage msg = (TIMMessage)o;
            if(msg==null||msg.getConversation().getPeer().equals(conversation.getPeer())
                    &&msg.getConversation().getType()==conversation.getType()){
                chatView.showMessage(msg);
                readMessages();
            }
        }else if (observable instanceof RefreshEvent){
            chatView.clearAllMessage();
            getMessage(null);
        }
    }

    /**
     * 获取消息
     *
     * @param message 最后一条消息
     */
    public void getMessage(@Nullable TIMMessage message){
        if (!isGetingMessage){
            isGetingMessage = true;
            conversation.getMessage(LAST_MESSAGE_NUM, message, new TIMValueCallBack<List<TIMMessage>>() {
                @Override
                public void onError(int i, String s) {
                    isGetingMessage = false;
                    Log.e(TAG,"get message error"+s);
                }

                @Override
                public void onSuccess(List<TIMMessage> timMessages) {
                    isGetingMessage = false;
                    chatView.showMessage(timMessages);
                }
            });
        }
    }

    /**
     * 设置会话为已读
     *
     */
    public void readMessages(){
        Log.i("Message","Read");
        Log.i("Message:Before read",String.valueOf(conversation.getUnreadMessageNum()));
        conversation.setReadMessage();
        Log.i("Message:After read",String.valueOf(conversation.getUnreadMessageNum()));
    }


    /**
     * 保存草稿
     *
     * @param message 消息数据
     */
    public void saveDraft(TIMMessage message){
        //清除原有数据
        conversation.setDraft(null);
        //保存
        if (message != null && message.getElementCount() > 0){
            TIMMessageDraft draft = new TIMMessageDraft();
            for (int i = 0; i < message.getElementCount(); ++i){
                draft.addElem(message.getElement(i));
            }
            conversation.setDraft(draft);
        }

    }

}
