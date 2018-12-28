package com.stratagile.pnrouter.utils;
import com.google.gson.Gson;
import com.stratagile.pnrouter.entity.HttpData;

import okhttp3.*;

import java.io.IOException;

/**
 * @author zl
 * @description okHttpClient封装
 * @date 2018-12-7 13:55
 * @created by androidsutido
 */
public class HttpClient {
    public static final OkHttpClient httpClient = new OkHttpClient();
    //Get方法调用服务
    public static HttpData httpGet(String url){
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        HttpData httpData = null;
        Gson gson = GsonUtil.getIntGson();
        try{
            response = httpClient.newCall(request).execute();
            if(response != null)
            {
                httpData = gson.fromJson(response.body().string(), HttpData.class);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            return httpData;
        }

    }
}
