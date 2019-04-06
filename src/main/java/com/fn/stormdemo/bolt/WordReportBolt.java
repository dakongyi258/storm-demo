package com.fn.stormdemo.bolt;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class WordReportBolt extends BaseBasicBolt {
    private static final Logger logger = LoggerFactory.getLogger(WordReportBolt.class);
    Map<String, Integer> counts = new HashMap<String, Integer>();

    public void execute(Tuple tuple, BasicOutputCollector collector) {
        String word = tuple.getStringByField("word");
        Integer count = tuple.getIntegerByField("count");
        logger.info("word:\t{} 的 count:\t{}", word, count);
        this.counts.put(word, count);
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }

    @Override
    public void cleanup() {
        System.out.println("-----------------FINAL COUNTS  START-----------------------");
        List<String> keys = new ArrayList<String>();
        keys.addAll(this.counts.keySet());
        Collections.sort(keys);

        for (String key : keys) {
            System.out.println(key + " : " + this.counts.get(key));
        }

        System.out.println("-----------------FINAL COUNTS  END-----------------------");
    }

}
