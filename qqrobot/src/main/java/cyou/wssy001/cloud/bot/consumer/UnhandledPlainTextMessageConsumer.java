package cyou.wssy001.cloud.bot.consumer;

import com.alibaba.fastjson.JSON;
import cyou.wssy001.cloud.bot.dto.UnhandledHttpRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.MessageChain;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;

@Component
@RequiredArgsConstructor
@RocketMQMessageListener(topic = "unhandled-group-plain-text-message", consumerGroup = "plain-text-message")
public class UnhandledPlainTextMessageConsumer implements RocketMQListener<String> {
    @Resource
    private ReactiveRedisOperations<String, Long> reactiveRedisOperations;

    @Override
    @SneakyThrows
    public void onMessage(String message) {
        if (Bot.getInstances().isEmpty()) return;

        UnhandledHttpRequestDto unhandledHttpRequestDto = JSON.parseObject(message, UnhandledHttpRequestDto.class);

        Bot bot = Bot.getInstances()
                .stream()
                .filter(v -> v.getGroup(unhandledHttpRequestDto.getGroupId()) != null)
                .findFirst()
                .orElse(null);

        if (bot == null) throw new RuntimeException("无Bot在目标群");

        String msgId = unhandledHttpRequestDto.getId();
        Boolean nonExist = reactiveRedisOperations.opsForValue()
                .setIfAbsent(msgId, bot.getId())
                .share()
                .block();

        if (!nonExist) throw new RuntimeException("重复消费");

        Duration ttl = Duration.ofSeconds(5);
        reactiveRedisOperations.expire(msgId, ttl);

        MessageChain messageChain = MiraiCode.deserializeMiraiCode(unhandledHttpRequestDto.getMiraiCode());
        bot.getGroup(unhandledHttpRequestDto.getGroupId())
                .sendMessage(messageChain);

        Thread.sleep(500);
    }
}
