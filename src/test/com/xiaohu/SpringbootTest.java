package com.xiaohu;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.qxiaohu.upload.XiaohuVideoApplication;
import com.qxiaohu.upload.db.XiaohuVideo;
import com.qxiaohu.upload.mapper.XiaohuVideoMapper;
import com.qxiaohu.upload.service.MainService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaxh
 * @date 2024/10/25
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = XiaohuVideoApplication.class)
//@Transactional
@Slf4j
public class SpringbootTest {

    @Autowired
    XiaohuVideoMapper xiaohuVideoMapper;

    @Autowired
    MainService mainService;


    @Test
    public void getVideoInfo(){
        mainService.getVideoInfo("D:\\dome\\数据");
    }

    @Test
    public void outJson(){
        List<XiaohuVideo> xiaohuVideos = xiaohuVideoMapper.selectList(null);
        System.out.println("xiaohuVideos = " + xiaohuVideos);
    }

    @Test
    public void T1(){
        List<XiaohuVideo> demoDo2s = xiaohuVideoMapper.selectList(null);
        for (XiaohuVideo demoDo2 : demoDo2s) {
            if (!StringUtils.hasText(demoDo2.getName())){
                getInfo(demoDo2);
                xiaohuVideoMapper.updateById(demoDo2);
            }
        }
    }


    public void getInfo(XiaohuVideo demoDo2){
        OkHttpClient client = getInstance();
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
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static OkHttpClient okHttpClient;

    public static OkHttpClient getInstance() {
        if (okHttpClient == null) {
            synchronized (SpringbootTest.class) {
                if (okHttpClient == null) {
                    okHttpClient = new OkHttpClient.Builder()
                            .proxy(
                                    new Proxy(Proxy.Type.HTTP,
                                            new InetSocketAddress("192.168.30.66", 7890)))
                            .connectTimeout(1000L, TimeUnit.SECONDS)
                            .readTimeout(1000L, TimeUnit.SECONDS)
                            .writeTimeout(1000L, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
        return okHttpClient;
    }

}

