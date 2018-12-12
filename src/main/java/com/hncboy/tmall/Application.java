package com.hncboy.tmall;

import com.hncboy.tmall.util.PortUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Created by IntelliJ IDEA.
 * User: hncboy
 * Date: 2018/11/1
 * Time: 19:27
 */
@SpringBootApplication
@ServletComponentScan
@EnableCaching // 用于启动缓存
@EnableElasticsearchRepositories(basePackages = "com.hncboy.tmall.es")
@EnableJpaRepositories(basePackages = {"com.hncboy.tmall.dao", "com.hncboy.tmall.pojo"})
public class Application extends SpringBootServletInitializer {

    static {
        //检查端口6379是否启动
        PortUtil.checkPort(6379, "Redis 服务端", true);
        PortUtil.checkPort(9300, "ElasticSearch 服务端", true);
        PortUtil.checkPort(5601, "Kibana 工具", true);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
