package com.fn.stormdemo;

import com.fn.stormdemo.bean.GetSpringBean;
import com.fn.stormdemo.topology.TopologyApp;
import com.fn.stormdemo.topology.WordCountTopology;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class StormDemoApplication {
    public static void main(String[] args) {
        // 启动嵌入式的 Tomcat 并初始化 Spring 环境及其各 Spring 组件
        ConfigurableApplicationContext context = SpringApplication.run(StormDemoApplication.class, args);
        GetSpringBean springBean = new GetSpringBean();
        springBean.setApplicationContext(context);
        //application.yml
        //TopologyApp 例子,对应修改kakfa的topicname为tets
//        TopologyApp app = context.getBean(TopologyApp.class);
//        app.runStorm(args);

        //单词统计 例子,对应修改kakfa的topicname为count_name
        WordCountTopology wordCountTopology = context.getBean(WordCountTopology.class);
        wordCountTopology.runStorm(args);
    }

}
