package com.hncboy.tmall;

import com.hncboy.tmall.util.PortUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Created by IntelliJ IDEA.
 * User: hncboy
 * Date: 2018/11/1
 * Time: 19:27
 */
@SpringBootApplication
@EnableCaching // 用于启动缓存
public class Application {

    static {
        //检查端口6379是否启动
        PortUtil.checkPort(6379, "Redis 服务端", true);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
