package com.teachingassistant.Fragement;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.teachingassistant.Activity.Education.CourseActivity;
import com.teachingassistant.Activity.Education.CourseSearchActivity;
import com.teachingassistant.Activity.Education.ExamActivity;
import com.teachingassistant.Activity.Education.GradeActivity;
import com.teachingassistant.R;
import com.teachingassistant.MyApplication;


public class EducationFragment extends Fragment implements View.OnClickListener{

    private MyApplication app;
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialNext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if(view==null) {
            view = inflater.inflate(R.layout.fragment_education, container, false);
            View view1 = view.findViewById(R.id.FragmentTitle);
            TextView fragementTitle = (TextView) view1.findViewById(R.id.text_title);
            fragementTitle.setText(app.readAccount() + "的个人教务");
            ImageButton kbcx = (ImageButton) view.findViewById(R.id.kbcx);
            kbcx.setOnClickListener(this);
            ImageButton cjcx = (ImageButton) view.findViewById(R.id.cjcx);
            cjcx.setOnClickListener(this);
            ImageButton kscx = (ImageButton) view.findViewById(R.id.kscx);
            kscx.setOnClickListener(this);
            ImageButton kjscx = (ImageButton) view.findViewById(R.id.kjscx);
            kjscx.setOnClickListener(this);
            ImageButton grxx = (ImageButton) view.findViewById(R.id.grxx);
            grxx.setOnClickListener(this);
        }
        return view;
    }

    private void initialNext()
    {
        app = new MyApplication();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId())
        {
            case R.id.kbcx:
                if(app.readCourseTableNameById(app.readAccount()).size()==0) {
                    intent = new Intent(getActivity(), CourseSearchActivity.class);
                    getActivity().startActivity(intent);
                }else{
                    intent = new Intent(getActivity(),CourseActivity.class);
                    getActivity().startActivity(intent);
                }
                break;
            case R.id.cjcx:
                intent = new Intent(getActivity(),GradeActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.kscx:
                intent = new Intent(getActivity(),ExamActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.kjscx:
                Toast.makeText(getActivity(),"努力中...",Toast.LENGTH_SHORT).show();
                break;
            case R.id.grxx:
                Toast.makeText(getActivity(),"努力中...",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
