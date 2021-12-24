package cyou.wssy001.cloud.bot.handler;

import cyou.wssy001.cloud.bot.dto.MessageChainDto;
import cyou.wssy001.cloud.bot.service.LogSendCallbackService;
import lombok.RequiredArgsConstructor;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.code.MiraiCode;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class GroupMessageHandler {
    @Resource
    private ReactiveRedisOperations<String, MessageChainDto> reactiveRedisOperations;

    private final RocketMQTemplate rocketMQTemplate;
    private final LogSendCallbackService logSendCallbackService;

    public void handle(@NotNull GroupMessageEvent event) {
        String miraiCode = MiraiCode.serializeToMiraiCode(event.getMessage().iterator());

        StringBuffer stringBuffer = new StringBuffer();
        int[] ids = event.getSource().getIds();
        Arrays.stream(ids)
                .forEach(stringBuffer::append);

        String key = stringBuffer.toString();

        MessageChainDto messageChainDto = new MessageChainDto();
        messageChainDto.setIds(ids);
        messageChainDto.setBotAccount(event.getBot().getId());
        messageChainDto.setGroupNumber(event.getGroup().getId());
        messageChainDto.setMiraiCode(miraiCode);

        Boolean nonExist = reactiveRedisOperations.opsForValue()
                .setIfAbsent(key, messageChainDto)
                .share()
                .block();

        if (!nonExist) return;

        Duration ttl = Duration.ofSeconds(5);
        reactiveRedisOperations.expire(key, ttl);

        Message<MessageChainDto> message = MessageBuilder.withPayload(messageChainDto).setHeader("KEYS", "GroupMessage_" + key).build();
        rocketMQTemplate.asyncSend("group-message", message, logSendCallbackService);
    }

}
