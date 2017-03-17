package com.teachingassistant.Fragement;

import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teachingassistant.Presentation.ViewFeatures.ConversationView;
import com.teachingassistant.R;
import com.tencent.TIMConversation;
import com.tencent.TIMGroupCacheInfo;
import com.tencent.TIMMessage;

import java.util.List;


public class ConversationFragment  extends Fragment implements ConversationView{

    private View view;
    //private List<Conversation>
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(view==null) {
            view = inflater.inflate(R.layout.fragment_conversation, container, false);
            View view1 = view.findViewById(R.id.FragmentTitle);
            TextView fragementTitle = (TextView) view1.findViewById(R.id.text_title);
            fragementTitle.setText("消息");
        }
        return view;
    }

    @Override
    public void initView(List<TIMConversation> conversationList) {

    }

    @Override
    public void updateMessage(TIMMessage message) {

    }


    @Override
    public void removeConversation(String identify) {

    }

    @Override
    public void updateGroupInfo(TIMGroupCacheInfo info) {

    }

    @Override
    public void refresh() {

    }
}
