package com.teachingassistant.Support.TIM;

import tencent.tls.platform.TLSAccountHelper;
import tencent.tls.platform.TLSLoginHelper;

/**
 * Created by 郭振阳 on 2017/3/5.
 */

public class TLSService {

    //TLSService对象
    private TLSService tlsService;

    //Helper
    private TLSAccountHelper accountHelper;
    private TLSLoginHelper loginHelper;


    public TLSService getInstance()
    {
        if(tlsService==null)
        {
            tlsService=new TLSService();
        }
        return tlsService;
    }


}
