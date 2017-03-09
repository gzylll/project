package com.teachingassistant.Support.TIM;

import android.content.Context;

import com.tencent.TIMManager;

/**
 * Created by 郭振阳 on 2017/3/5.
 */

public class InitBusiness {

    public static void initIMsdk(Context context)
    {
        TIMManager.getInstance().init(context);
        TIMManager.getInstance().disableCrashReport();
    }

}
