package com.fn.stormdemo.bolt;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;

public class SplitSentenceBolt implements IRichBolt {
    private OutputCollector _collector;

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word"));
    }

    public Map<String, Object> getComponentConfiguration() {
        return null;
    }

    public void prepare(Map stormConf, TopologyContext context,
                        OutputCollector collector) {
        _collector = collector;
    }

    public void execute(Tuple input) {
        String sentence = input.getStringByField("word");
        String[] words = sentence.split(" ");
        for (String word : words) {
            this._collector.emit(new Values(word));
        }
    }

    public void cleanup() {
        // TODO Auto-generated method stub

    }
}