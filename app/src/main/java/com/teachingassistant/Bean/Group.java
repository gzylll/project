package com.teachingassistant.Bean;

import android.util.Log;

import com.tencent.TIMGroupBaseInfo;
import com.tencent.TIMGroupManager;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 11650 on 2017/3/18.
 */

public class Group {

    private List<TIMGroupBaseInfo> timGroupBaseInfo = new ArrayList<>();


    private TIMValueCallBack<List<TIMGroupBaseInfo>> cb = new TIMValueCallBack<List<TIMGroupBaseInfo>>() {
        @Override
        public void onError(int i, String s) {

        }

        @Override
        public void onSuccess(List<TIMGroupBaseInfo> timGroupBaseInfos) {
            timGroupBaseInfo = timGroupBaseInfos;
        }
    };

    private static Group instance;

    public synchronized static Group getInstance(){
        if (instance == null){
            instance = new Group();
        }
        return instance;
    }

    private Group(){
        TIMGroupManager.getInstance().getGroupList(cb);
    }

    public String GetNameById(String id){
        Log.i("id",id);
        if(timGroupBaseInfo!=null) {
            for (TIMGroupBaseInfo info : timGroupBaseInfo) {
                Log.i("info ID:",info.getGroupId());
                if (info.getGroupId().equals(id)) {
                    Log.i("info Name:",info.getGroupName());
                    return info.getGroupName();
                }
            }
        }
        return "";
    }

}
