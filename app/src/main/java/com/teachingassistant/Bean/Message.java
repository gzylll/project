package com.teachingassistant.Bean;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.teachingassistant.Presentation.Adapter.ChatAdapter;
import com.tencent.TIMConversationType;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageStatus;

/**
 * message基类，提供消息处理和显示的函数
 */

public abstract class Message {
    //消息
    TIMMessage message;

    private boolean hasTime;

    /**
     * 消息描述信息
     */
    private String desc;


    public TIMMessage getMessage() {
        return message;
    }


    /**
     * 显示消息
     *  显示消息流程：根据消息获取对应的布局，屏蔽一半（例如自己发的就屏蔽别人的布局）
     *  然后填充与消息内容无关的部分信息，最后填充消息。
     * @param viewHolder 界面样式
     * @param context 显示消息的上下文
     */
    public abstract void showMessage(ChatAdapter.ViewHolder viewHolder, Context context);

    /**
     * 获取显示气泡
     *  根据消息内容获得应该显示的界面，然后填充除数据之外的部分，
     *  将应该填充信息的部分返回供不同的消息填充。
     * @param viewHolder 界面样式
     */
    public RelativeLayout getBubbleView(ChatAdapter.ViewHolder viewHolder){
        viewHolder.systemMessage.setVisibility(hasTime? View.VISIBLE:View.GONE);
        viewHolder.systemMessage.setText(Time.getChatTimeStr(message.timestamp()));
        showDesc(viewHolder);
        if (message.isSelf()){
            viewHolder.leftPanel.setVisibility(View.GONE);
            viewHolder.rightPanel.setVisibility(View.VISIBLE);
            return viewHolder.rightMessage;
        }else{
            viewHolder.leftPanel.setVisibility(View.VISIBLE);
            viewHolder.rightPanel.setVisibility(View.GONE);
            //群聊显示名称，群名片>个人昵称>identify
            if (message.getConversation().getType() == TIMConversationType.Group){
                viewHolder.sender.setVisibility(View.VISIBLE);
                String name = "";
                if (message.getSenderGroupMemberProfile()!=null) name = message.getSenderGroupMemberProfile().getNameCard();
                if (name.equals("")&&message.getSenderProfile()!=null) name = message.getSenderProfile().getNickName();
                if (name.equals("")) name = message.getSender();
                viewHolder.sender.setText(name);
            }else{
                viewHolder.sender.setVisibility(View.GONE);
            }
            return viewHolder.leftMessage;
        }

    }

    /**
     * 显示消息状态
     *  根据状态显示不同的组件
     * @param viewHolder 界面样式
     */
    public void showStatus(ChatAdapter.ViewHolder viewHolder){
        switch (message.status()){
            case Sending:
                viewHolder.error.setVisibility(View.GONE);
                viewHolder.sending.setVisibility(View.VISIBLE);
                break;
            case SendSucc:
                viewHolder.error.setVisibility(View.GONE);
                viewHolder.sending.setVisibility(View.GONE);
                break;
            case SendFail:
                viewHolder.error.setVisibility(View.VISIBLE);
                viewHolder.sending.setVisibility(View.GONE);
                viewHolder.leftPanel.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 判断是否是自己发的
     *
     */
    public boolean isSelf(){
        return message.isSelf();
    }

    /**
     * 获取消息摘要
     *
     */
    public abstract String getSummary();

    /**
     * 保存消息或消息文件
     *
     */
    public abstract void save();


    /**
     * 删除消息
     *
     */
    public void remove(){
        if (message != null){
            message.remove();
        }
    }

    /**
     * 是否需要显示时间获取
     *
     */
    public boolean getHasTime() {
        return hasTime;
    }


    /**
     * 是否需要显示时间设置
     *  300之内不显示时间
     * @param message 上一条消息
     */
    public void setHasTime(TIMMessage message){
        if (message == null){
            hasTime = true;
            return;
        }
        hasTime = this.message.timestamp() - message.timestamp() > 300;
    }


    /**
     * 消息是否发送失败
     *
     */
    public boolean isSendFail(){
        return message.status() == TIMMessageStatus.SendFail;
    }

    /**
     * 清除气泡原有数据
     *  清空填充消息的气泡，供后续填充消息使用
     */
    protected void clearView(ChatAdapter.ViewHolder viewHolder){
        getBubbleView(viewHolder).removeAllViews();
        getBubbleView(viewHolder).setOnClickListener(null);
    }

    /**
     * 获取发送者
     *
     */
    public String getSender(){
        if (message.getSender() == null) return "";
        return message.getSender();
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    private void showDesc(ChatAdapter.ViewHolder viewHolder){

        if (desc == null || desc.equals("")){
            viewHolder.rightDesc.setVisibility(View.GONE);
        }else{
            viewHolder.rightDesc.setVisibility(View.VISIBLE);
            viewHolder.rightDesc.setText(desc);
        }
    }
}
