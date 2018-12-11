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
    public static final MediaType type = MediaType.parse("application/x-www-form-urlencoded;charset=utf-8");
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
            httpData = gson.fromJson(response.body().string(), HttpData.class);
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
    //Post方法调用服务
    public static String httpPost(String url,String content) throws IOException{
        RequestBody requestBody = RequestBody.create(type,content);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Response response = httpClient.newCall(request).execute();
        return response.body().string();
    }
}
