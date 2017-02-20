package com.teachingassistant.Support.Bean;

import com.teachingassistant.Support.Cookies.CookieManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class okHttpUilts {

    /**
     * 带cookieManager的OkHttpClient对象
     */
    private static OkHttpClient PersonalOkHttpClient = new OkHttpClient.Builder()
            .cookieJar(new CookieManager()).build();

    /**
     * 登陆Url
     */
    final private static String loginUrl = "http://202.119.206.62/xtgl/login_login.html";

    /**
     * 课表查询url，后加查询者的学号即可
     */
    final private static String kbcxUrl = "http://202.119.206.62/kbcx/xskbcx_cxXsKb.html" +
            "?gnmkdmKey=N253508&sessionUserKey=";

    /**
     * 成绩查询Url:有两个，一个某科目的详细信息，一个全部科目
     */
    final private static String cjcxUrl = "http://202.119.206.62/cjcx/cjcx_cxDgXscj.html" +
            "?doType=query&gnmkdmKey=N305005&sessionUserKey=";

    final private static String cjcxUrl_1 = "http://202.119.206.62/cjcx/cjcx_cxCjxq.html?time="
            +System.currentTimeMillis()+"&gnmkdmKey=N305005&sessionUserKey=";

    /**
     * 考试查询Url
     */
    final private static String kscxUrl = "http://202.119.206.62/kwgl/kscx_cxXsksxxIndex.html"+
            "?doType=query&gnmkdmKey=N358105&sessionUserKey=";

    public static String getLoginurl() {
        return loginUrl;
    }

    public static String getKbcxurl() {
        return kbcxUrl;
    }

    public static String getCjcxUrl()
    {
        return cjcxUrl;
    }

    public static String getCjcxUrl_1(){
        return cjcxUrl_1;
    }

    public static String getKscxUrl() {
        return kscxUrl;
    }

    /**
     * 获得带有cookie管理的对象
     * @return 对象
     */
    public static OkHttpClient getOkHttpClient() {
        return PersonalOkHttpClient;
    }

    /**
     * 获得请求函数
     * @param url  请求url
     * @param requestBody  请求的requestBody
     * @return 指定url的POST请求
     */
    public static Request getRequest(String url, RequestBody requestBody)
    {
        return new Request.Builder().url(url).post(requestBody).build();
    }
}
