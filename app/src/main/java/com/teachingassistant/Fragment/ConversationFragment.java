package com.teachingassistant.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.teachingassistant.Activity.Main.MainActivity;
import com.teachingassistant.Presentation.Adapter.ConversationAdapter;
import com.teachingassistant.Presentation.Presenter.ConversationPresenter;
import com.teachingassistant.Presentation.ViewFeatures.ConversationView;
import com.teachingassistant.R;
import com.teachingassistant.Bean.Conversation;
import com.teachingassistant.Bean.MessageFactory;
import com.tencent.TIMConversation;
import com.tencent.TIMGroupCacheInfo;
import com.tencent.TIMMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class ConversationFragment  extends Fragment implements ConversationView{

    private View view;
    private List<Conversation> conversationList= new LinkedList<>();
    private ConversationAdapter adapter;
    private ConversationPresenter presenter;
    private ListView listView;
    private List<String> groupList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(view==null) {
            view = inflater.inflate(R.layout.fragment_conversation, container, false);
            //获得会话listview
            listView = (ListView)view.findViewById(R.id.conversation_list);
            adapter = new ConversationAdapter(getActivity(),R.layout.item_conversation,conversationList);
            View view1 = view.findViewById(R.id.FragmentTitle);
            listView.setAdapter(adapter);
            //点击之后跳转聊天
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    conversationList.get(i).navToChat(getActivity());
                }
            });
            registerForContextMenu(listView);
            presenter = new ConversationPresenter(this);
            //获取会话，初始化view
            presenter.getConversation();
            TextView fragementTitle = (TextView) view1.findViewById(R.id.text_title);
            fragementTitle.setText("消息");
        }
        return view;
    }

    /**
     * 初始化view
     * @param conversationList 会话列表
     */
    @Override
    public void initView(List<TIMConversation> conversationList) {
        this.conversationList.clear();
        groupList = new ArrayList<>();
        for (TIMConversation item:conversationList){
            switch (item.getType()){
                case C2C:
                case Group:
                    this.conversationList.add(new Conversation(item));
                    groupList.add(item.getPeer());
                    break;
            }
        }
    }

    @Override
    public void updateMessage(TIMMessage message) {
        if (message == null){
            adapter.notifyDataSetChanged();
            return;
        }
        Conversation conversation = new Conversation(message.getConversation());
        Iterator<Conversation> iterator =conversationList.iterator();
        while (iterator.hasNext()){
            Conversation c = iterator.next();
            if (conversation.equals(c)){
                conversation = c;
                iterator.remove();
                break;
            }
        }
        conversation.setLastMessage(MessageFactory.getMessage(message));
        conversationList.add(conversation);
        refresh();
    }


    /**
     * 删除会话
     * @param identify ID
     */
    @Override
    public void removeConversation(String identify) {
        Iterator<Conversation> iterator = conversationList.iterator();
        while(iterator.hasNext()){
            Conversation conversation = iterator.next();
            if (conversation.getIdentify()!=null&&conversation.getIdentify().equals(identify)){
                iterator.remove();
                adapter.notifyDataSetChanged();
                return;
            }
        }
    }

    @Override
    public void updateGroupInfo(TIMGroupCacheInfo info) {

    }

    @Override
    public void refresh() {
        Collections.sort(conversationList);
        adapter.notifyDataSetChanged();
        if (getActivity() instanceof MainActivity)
            ((MainActivity) getActivity()).setMsgUnread(getTotalUnreadNum() == 0);
    }

    /**
     * 长按删除
     * @param menu 菜单
     * @param v view
     * @param menuInfo 信息
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Conversation conversation = conversationList.get(info.position);
        if (conversation != null){
            menu.add(0, 1, Menu.NONE, "删除会话");
        }
    }

    //选中事件
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Conversation conversation = conversationList.get(info.position);
        switch (item.getItemId()) {
            case 1:
                if (conversation != null){
                    if (presenter.delConversation(conversation.getType(), conversation.getIdentify())){
                        conversationList.remove(conversation);
                        adapter.notifyDataSetChanged();
                    }
                }
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * 获取总共的未读显示
     * @return long
     */
    private long getTotalUnreadNum(){
        long num = 0;
        for (Conversation conversation : conversationList){
            num += conversation.getUnreadNum();
        }
        return num;
    }
}
