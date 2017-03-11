package com.teachingassistant.Fragement;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.teachingassistant.MyApplication;
import com.teachingassistant.R;
import com.teachingassistant.Support.Bean.dbHelper;
import com.tencent.TIMGroupAddOpt;
import com.tencent.TIMGroupBaseInfo;
import com.tencent.TIMGroupManager;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CourseFragment extends Fragment {

    private ListView groupList;
    private View view;
    private List<Map<String,Object>> list;
    private TextView nonGroup;
    private List<TIMGroupBaseInfo> myGroup = new ArrayList<>();

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("onCreate：","course");
        view = inflater.inflate(R.layout.fragment_course, container, false);
        groupList = (ListView)view.findViewById(R.id.CourseGroupList);
        groupList.setOnItemClickListener(onItemClickListener);
        nonGroup =(TextView)view.findViewById(R.id.no_course_group);
        View view1 = view.findViewById(R.id.FragmentTitle);
        TextView fragementTitle = (TextView) view1.findViewById(R.id.text_title);
        fragementTitle.setText("课程");
        //显示群组
        showGroup();
        return view;
    }

    /**
     * fragment延时加载，当可见时刷新数据
     * @param isVisibleToUser 是否可见
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            //连接群组
            connectGroup();
        }
    }

    private void showGroup() {
        MyApplication app = new MyApplication();
        String s = "2016-2017-2-"+app.readAccount();
        if(app.IsExistsCourseTable(s))
        {
            nonGroup.setVisibility(View.GONE);
            dbHelper db = new dbHelper(getActivity(),s);
            list = db.query();
            for(int i = 0;i<list.size();i++)
            {
                for(int j=i+1;j<list.size();j++)
                {
                    if(list.get(i).get("groupName").equals(list.get(j).get("groupName")))
                    {
                        list.remove(j);
                        j--;
                    }
                }
            }
            SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(),
                                                            list,
                                                            R.layout.list_items_course_group,
                                                            new String[]{"groupName"},
                                                            new int[]{R.id.courseGroupName});
            groupList.setAdapter(simpleAdapter);
        }
        else{
            nonGroup.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 处理群组
     * 从服务器获得当前用户的群组id和群组名，并存储，如果没有的话就注册群或者加入群。
     */
    private void connectGroup(){
        //获取已加入的群组信息。
        TIMGroupManager.getInstance().getGroupList(new TIMValueCallBack<List<TIMGroupBaseInfo>>() {
            @Override
            public void onError(int i, String s) {
                Toast.makeText(getActivity(),i+" "+s,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(List<TIMGroupBaseInfo> timGroupBaseInfos) {
                Log.i("查询群是否创建",String.valueOf(timGroupBaseInfos.size()));
                myGroup = timGroupBaseInfos;
                //列表非空表示自己的群已创建,否则建群
                if(timGroupBaseInfos.size()==0) {
                    for(int i=0;i<list.size();i++)
                    {
                        TIMGroupManager.CreateGroupParam param
                                = TIMGroupManager.getInstanceById(MyApplication.TIMAccount)
                                .new CreateGroupParam();
                        //设为公开群
                        param.setGroupType("Public");
                        Log.i("建群",i+":"+transformName(list.get(i).get("groupName").toString()));
                        //设定名字
                        param.setGroupName(transformName(list.get(i).get("groupName").toString()));
                        //设定允许所有人加群
                        param.setAddOption(TIMGroupAddOpt.TIM_GROUP_ADD_ANY);
                        //建群
                        TIMGroupManager.getInstance().createGroup(param, new TIMValueCallBack<String>() {
                            @Override
                            public void onError(int i, String s) {
                                Toast.makeText(getActivity(),"建群失败\n"+i+":"+s,Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess(String s) {
                                Toast.makeText(getActivity(),"建群成功\n"+s,Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                else{
                }
            }
        });
    }

    /**
     * 特殊处理函数，为了保证群名小于等于30字节
     */
    private String transformName(String name) {
        String Name[] = name.split("-");
        String teacher[] = Name[1].split("\\(");
        String second;
        if(teacher[0].length()>4) {
            second = teacher[0].substring(0,4);
        }
        else{
            second = teacher[0];
        }
        int now = 29-second.getBytes().length;
        String first = "";
        for(int i=0;i<Name[0].length();i++)
        {
            if(Name[0].substring(0,i+1).getBytes().length<=now) {
                first = Name[0].substring(0,i+1);
            }
            else{
                break;
            }
        }
        Log.i("建群：",String.valueOf((first+"-"+second).getBytes().length));
        return first+"-"+second;
    }
}
