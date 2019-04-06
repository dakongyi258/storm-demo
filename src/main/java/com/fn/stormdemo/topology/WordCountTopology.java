package com.fn.stormdemo.topology;

import com.fn.stormdemo.bolt.SplitSentenceBolt;
import com.fn.stormdemo.bolt.WordCountBolt;
import com.fn.stormdemo.bolt.WordReportBolt;
import com.fn.stormdemo.spout.KafkaInsertDataSpout;
import com.fn.stormdemo.spout.KafkaWordCountDataSpout;
import com.fn.stormdemo.spout.RandomSentenceSpout;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WordCountTopology {
    private final Logger logger = LoggerFactory.getLogger(WordCountTopology.class);

    public void runStorm(String[] args) {

        TopologyBuilder builder = new TopologyBuilder();

        // builder.setSpout("spout", new RandomSentenceSpout(), 5);
        //kafka的spout
        builder.setSpout("spout", new KafkaWordCountDataSpout(), 5);

        //ShuffleGrouping：随机选择一个Task来发送。
        builder.setBolt("split", new SplitSentenceBolt(), 8).shuffleGrouping("spout");
        //FiledGrouping：根据Tuple中Fields来做一致性hash，相同hash值的Tuple被发送到相同的Task。
        builder.setBolt("count", new WordCountBolt(), 12).fieldsGrouping("split", new Fields("word"));
        //GlobalGrouping：所有的Tuple会被发送到某个Bolt中的id最小的那个Task。
        builder.setBolt("report", new WordReportBolt(), 6).globalGrouping("count");

        Config conf = new Config();
        conf.setDebug(true);

        if (args != null && args.length > 0) {
            conf.setNumWorkers(1);

            try {
                StormSubmitter.submitTopologyWithProgressBar(args[0], conf, builder.createTopology());
            } catch (AlreadyAliveException e) {
                e.printStackTrace();
            } catch (InvalidTopologyException e) {
                e.printStackTrace();
            } catch (AuthorizationException e) {
                e.printStackTrace();
            }
        } else {
            conf.setMaxTaskParallelism(3);
            logger.info("运行本地模式");
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("word-count", conf, builder.createTopology());

//            cluster.shutdown();
        }
        logger.info("storm启动成功...");
    }
}