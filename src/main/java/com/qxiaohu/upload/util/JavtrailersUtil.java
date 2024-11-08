package com.qxiaohu.upload.util;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.qxiaohu.upload.db.XiaohuVideo;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaxh
 * @date 2024/11/7
 */
public class JavtrailersUtil {



    public static void getVideoInfo(XiaohuVideo demoDo2){
        OkHttpClient client = SpringUtil.getBean(OkHttpClient.class);
        Request request = new Request.Builder()
                .url("https://javtrailers.com/api/video/"+demoDo2.getRemark())
                .method("GET", null)
                .addHeader("authorization", "AELAbPQCh_fifd93wMvf_kxMD_fqkUAVf@BVgb2!md@TNW8bUEopFExyGCoKRcZX")
                .addHeader("Cookie", "user-country=HK")
                .build();
        try(Response response = client.newCall(request).execute()){
            JSONObject jsonObject = JSONObject.parseObject(response.body().string());
            JSONObject video = jsonObject.getJSONObject("video");
            JSONArray categories = video.getJSONArray("categories");
            List<String> types = new ArrayList<>();
            if (!categories.isEmpty()){
                for (int i = 0; i < categories.size(); i++) {
                    types.add(categories.getJSONObject(i).getString("zhName"));
                }
                demoDo2.setTypes(String.join("、", types));
            }
            JSONArray casts = video.getJSONArray("casts");
            List<String> performer = new ArrayList<>();
            if (!casts.isEmpty()){
                for (int i = 0; i < casts.size(); i++) {
                    performer.add(casts.getJSONObject(i).getString("jpName"));
                }
                demoDo2.setPerformer(String.join("、", performer));
            }
            JSONArray gallery = video.getJSONArray("gallery");
            List<String> preview = new ArrayList<>();
            if (!gallery.isEmpty()){
                for (int i = 0; i < gallery.size(); i++) {
                    preview.add(gallery.getString(i));
                }
                demoDo2.setPreview(String.join("、", preview));
            }
            demoDo2.setDate(video.getDate("releaseDate"));
            demoDo2.setName(video.getString("zhTitle"));
            demoDo2.setImg(video.getString("image"));
            demoDo2.setDuration(video.getInteger("duration"));
            demoDo2.setCode(video.getString("dvdId"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String getVideoLike(String q) {
        OkHttpClient client = SpringUtil.getBean(OkHttpClient.class);
        Request request = new Request.Builder()
                .url("https://search.javtrailers.com/indexes/videos/search?q=" + q + "&sort=releaseDate:desc&limit=1")
                .method("GET", null)
                .addHeader("authorization", "Bearer 6b4bd3e560e994a5b009023e1d21f51e95dbb86ca3f47cb03f34f8a2cb9a93f2")
                .build();
        try (Response response = client.newCall(request).execute()) {
            JSONObject jsonObject = JSONObject.parseObject(response.body().string());
            JSONArray hits = jsonObject.getJSONArray("hits");
            if (hits.isEmpty()){
                return null;
            }
            return hits.getJSONObject(0).getString("contentId");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
