package com.teachingassistant.Presentation.Presenter;



import com.teachingassistant.Presentation.Event.MessageEvent;

import java.util.Observable;
import java.util.Observer;

/**
 * 会话presenter，订阅消息监听
 */

public class ConversationPresenter implements Observer {

    @Override
    public void update(Observable observable, Object o) {
        if(observable instanceof MessageEvent){

        }
    }
}
