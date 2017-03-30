package com.teachingassistant.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teachingassistant.R;

public class DynamicFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.i("onCreate：","dynamic");
        View view = inflater.inflate(R.layout.fragment_dynamic, container, false);
        View view1 = view.findViewById(R.id.FragmentTitle);
        TextView fragementTitle = (TextView) view1.findViewById(R.id.text_title);
        fragementTitle.setText("动态");
        return view;
    }
}
