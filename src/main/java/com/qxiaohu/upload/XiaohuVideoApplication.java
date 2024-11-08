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
