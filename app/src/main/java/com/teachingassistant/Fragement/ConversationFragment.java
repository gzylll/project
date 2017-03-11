package com.teachingassistant.Fragement;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teachingassistant.R;


public class ConversationFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("onCreate：","conversation");
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_conversation, container, false);
        View view1 = view.findViewById(R.id.FragmentTitle);
        TextView fragementTitle = (TextView) view1.findViewById(R.id.text_title);
        fragementTitle.setText("消息");
        return view;
    }

}
