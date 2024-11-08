package com.qxiaohu.upload;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@SpringBootApplication
@EnableAsync
@Slf4j
public class XiaohuVideoApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext application = SpringApplication.run(XiaohuVideoApplication.class, args);
        Environment env = application.getEnvironment();
        String port = env.getProperty("server.port");
        String path = env.getProperty("server.servlet.context-path");
        if (StringUtils.isEmpty(path)) {
            path = "";
        }
        System.out.println("\n----------------------------------------------------------\n\t" +
                "访问网址: \t\thttp://localhost:" + port + path + "\n" +
                "----------------------------------------------------------");
//        //初始化配置
//        ProtectionDomain protectionDomain = HWChatGPTApplication.class.getProtectionDomain();
//        CodeSource codeSource = protectionDomain.getCodeSource();
//        //jar:file:/{{path}}/hwchatgpt-cutting.jar!/BOOT-INF/classes!/
//        String location = codeSource.getLocation().getPath();
//        try {
//            location = java.net.URLDecoder.decode(location, "utf-8");
//        } catch (UnsupportedEncodingException e) {
//            log.error("路径解码失败", e);
//        }
//        location = location.replaceAll(
//                "jar:file:/|file:/|upload-excel.jar!/BOOT-INF/classes!/", "");
//        File file = new File(location + "config.json");
        // 使用InputStreamReader指定编码为UTF-8
//        try (InputStreamReader isr = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
//             JSONReader reader = JSONReader.of(isr);
//        ) {
//            JSONObject config = reader.readJSONObject();
//            if (config.containsKey("file")) {
//                List<UploadFileConfig> uploadFileConfigList = config.getList("file", UploadFileConfig.class);
//                uploadFileConfigList.forEach(e ->
//                        SpringBeanUtils.registerBeanConstruct(
//                                UploadFileConfig.KEY + e.getName(), UploadFileConfig.class,
//                                e.getName(), e.getUrl(), e.getAuthorization())
//                );
//            }
//        } catch (IOException e) {
//            log.error("读取配置文件失败", e);
//        }
        //打开网页
        try {
            Runtime runtime = Runtime.getRuntime();
            String command = "cmd /c start http://localhost:" + port + path;
            Process process = runtime.exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                log.info(line);
            }
            process.waitFor();
        } catch (Exception e) {
            log.info("打开网页失败");
        }
    }

}
