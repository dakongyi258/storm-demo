package com.fn.stormdemo.spout;

import com.fn.stormdemo.bean.GetSpringBean;
import com.fn.stormdemo.config.ApplicationConfiguration;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;


public class KafkaWordCountDataSpout extends BaseRichSpout {

    /**
     *
     */
    private static final long serialVersionUID = -2548451744178936478L;

    private static final Logger logger = LoggerFactory.getLogger(KafkaWordCountDataSpout.class);

    private SpoutOutputCollector collector;

    private KafkaConsumer<String, String> consumer;

    private ConsumerRecords<String, String> msgList;


    private ApplicationConfiguration app;


    @SuppressWarnings("rawtypes")
    @Override
    public void open(Map map, TopologyContext arg1, SpoutOutputCollector collector) {
        app = GetSpringBean.getBean(ApplicationConfiguration.class);
        kafkaInit();
        this.collector = collector;
    }


    @Override
    public void nextTuple() {
        for (; ; ) {
            try {
                msgList = consumer.poll(1000);
                if (null != msgList && !msgList.isEmpty()) {
                    String msg = "";
                    for (ConsumerRecord<String, String> record : msgList) {
                        // 原始数据
                        msg = record.value();
                        if (null == msg || "".equals(msg.trim())) {
                            continue;
                        }
                        logger.info("接收到数据：{}", msg);
                    }
                    //发送到bolt中
                    this.collector.emit(new Values(msg));
                    consumer.commitAsync();
                } else {
                    TimeUnit.SECONDS.sleep(10);
                    logger.info("未拉取到数据...");
                }
            } catch (Exception e) {
                logger.error("消息队列处理异常!", e);
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e1) {
                    logger.error("暂停失败!", e1);
                }
            }
        }
    }


    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word"));
    }

    /**
     * 初始化kafka配置
     */
    private void kafkaInit() {
        Properties props = new Properties();
        props.put("bootstrap.servers", app.getServers());
        props.put("max.poll.records", app.getMaxPollRecords());
        props.put("enable.auto.commit", app.getAutoCommit());
        props.put("group.id", app.getGroupId());
        props.put("auto.offset.reset", app.getCommitRule());
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumer = new KafkaConsumer<String, String>(props);
        String topic = app.getTopicName();
        this.consumer.subscribe(Arrays.asList(topic));
        logger.info("消息队列[" + topic + "] 开始初始化...");
    }
}
