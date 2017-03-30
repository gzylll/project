package com.teachingassistant.Bean;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.teachingassistant.Activity.Chat.ChatActivity;
import com.teachingassistant.R;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;

/**
 * 会话类
 * Created by 11650 on 2017/3/17.
 */

public class Conversation implements Comparable{

    //会话ID
    private String identify;
    //会话类型
    private TIMConversationType type;
    //会话对象名字
    private String name;
    //会话对象
    private TIMConversation conversation;
    //最新的一条消息
    private Message lastMessage;

    //构造函数
    public Conversation(TIMConversation conversation){
        this.conversation = conversation;
        type = conversation.getType();
        identify = conversation.getPeer();
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    //获得头像
    public int getAvatar() {
        switch (type){
            case C2C:
                return R.mipmap.head_other;
            case Group:
                return R.mipmap.head_group;
        }
        return 0;
    }

    public String getIdentify(){
        return identify;
    }
    /**
     * 跳转到聊天
     * @param context Context
     */
    public void navToChat(Context context)
    {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("identify", identify);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    /**
     * 获取最后一条消息摘要
     */
    public String getLastMessageSummary(){
        if (conversation.hasDraft()){
            TextMessage textMessage = new TextMessage(conversation.getDraft());
            if (lastMessage == null || lastMessage.getMessage().timestamp() < conversation.getDraft().getTimestamp()){
                return "[草稿]" + textMessage.getSummary();
            }else{
                return lastMessage.getSummary();
            }
        }else{
            if (lastMessage == null) return "";
            return lastMessage.getSummary();
        }
    }

    /**
     * 获取名称
     */
    public String getName() {
        if (type == TIMConversationType.Group){
            name=Group.getInstance().GetNameById(identify);
            if (name.equals("")) name = identify;
        }
        else{
            name = identify;
        }
        return name;
    }

    /**
     * 获取未读消息数量
     */
    public long getUnreadNum(){
        if (conversation == null) return 0;
        return conversation.getUnreadMessageNum();
    }

    /**
     * 将所有消息标记为已读
     */
    public void readAllMessage(){
        if (conversation != null){
            conversation.setReadMessage();
        }
    }

    /**
     * 获取最后一条消息的时间
     */
    public long getLastMessageTime() {
        if (conversation.hasDraft()){
            if (lastMessage == null ||
                    lastMessage.getMessage().timestamp() < conversation.getDraft().getTimestamp()){
                return conversation.getDraft().getTimestamp();
            }else{
                return lastMessage.getMessage().timestamp();
            }
        }
        if (lastMessage == null) return 0;
        return lastMessage.getMessage().timestamp();
    }

    /**
     * 获取会话类型
     */
    public TIMConversationType getType(){
        return conversation.getType();
    }

    /**
     * 比较两个消息是否相等
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conversation that = (Conversation) o;
        if (!identify.equals(that.identify)) return false;
        return type == that.type;

    }

    @Override
    public int hashCode() {
        int result = identify.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        Conversation anotherConversation = (Conversation) o;
        long timeGap = anotherConversation.getLastMessageTime() - getLastMessageTime();
        if (timeGap > 0) return  1;
        else if (timeGap < 0) return -1;
        return 0;
    }
}
