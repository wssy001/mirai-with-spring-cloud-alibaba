package cyou.wssy001.cloud.bot.config;

import cyou.wssy001.cloud.bot.consumer.ImageMessageDBConsumer;
import cyou.wssy001.cloud.bot.consumer.PlainTextMessageDBConsumer;
import cyou.wssy001.cloud.bot.sorter.UnhandledSendMessageSorter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Setter
@Configuration
@RequiredArgsConstructor
public class RocketMQConfig {
    @Value("${rocketmq.name-server}")
    private String nameServer;

    private final ImageMessageDBConsumer imageMessageDBConsumer;
    private final PlainTextMessageDBConsumer plainTextMessageDBConsumer;
    private final UnhandledSendMessageSorter unhandledSendMessageSorter;

//    private final TestConsumer testConsumer;

//    @Bean
//    public DefaultMQPushConsumer testBatchConsumer() {
//        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
//        consumer.setNamesrvAddr(nameServer);
//        consumer.registerMessageListener(testConsumer);
//        consumer.setConsumerGroup("test-db");
//        consumer.setPullInterval(2000);
//        consumer.setConsumeThreadMax(2);
//        consumer.setConsumeThreadMin(1);
//        consumer.setPullBatchSize(16);
//        consumer.setConsumeMessageBatchMaxSize(16);
//        try {
//            consumer.subscribe("springboot-mq", "");
//            consumer.start();
//        } catch (Exception e) {
//            log.info("******Exception：{}", e.getMessage());
//        }
//        return consumer;
//    }

    @Bean
    public DefaultMQPushConsumer imageMessageDBBatchConsumer() {
        DefaultMQPushConsumer consumer = getDefaultBatchConsumer();
        consumer.setNamesrvAddr(nameServer);
        consumer.registerMessageListener(imageMessageDBConsumer);
        consumer.setConsumerGroup("image-db");
        try {
            consumer.subscribe("image-message", "");
            consumer.start();
        } catch (Exception e) {
            log.info("******Exception：{}", e.getMessage());
        }
        return consumer;
    }

    @Bean
    public DefaultMQPushConsumer plainTextMessageDBBatchConsumer() {
        DefaultMQPushConsumer consumer = getDefaultBatchConsumer();
        consumer.registerMessageListener(plainTextMessageDBConsumer);
        consumer.setConsumerGroup("plain-text-db");
        try {
            consumer.subscribe("plain-text-message", "");
            consumer.start();
        } catch (Exception e) {
            log.info("******Exception：{}", e.getMessage());
        }
        return consumer;
    }

    @Bean
    public DefaultMQPushConsumer unhandledSendMessageSorter() {
        DefaultMQPushConsumer consumer = getDefaultBatchConsumer();
        consumer.registerMessageListener(unhandledSendMessageSorter);
        consumer.setConsumerGroup("group-message");
        try {
            consumer.subscribe("unhandled-send-message", "");
            consumer.start();
        } catch (Exception e) {
            log.info("******Exception：{}", e.getMessage());
        }
        return consumer;
    }

    private DefaultMQPushConsumer getDefaultBatchConsumer() {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
        consumer.setNamesrvAddr(nameServer);
        consumer.setPullInterval(2000);
        consumer.setConsumeThreadMax(2);
        consumer.setConsumeThreadMin(1);
        consumer.setPullBatchSize(16);
        consumer.setConsumeMessageBatchMaxSize(16);
        return consumer;
    }
}
