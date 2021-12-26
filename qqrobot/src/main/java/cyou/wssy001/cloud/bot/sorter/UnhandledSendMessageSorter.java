package cyou.wssy001.cloud.bot.sorter;

import com.alibaba.fastjson.JSON;
import cyou.wssy001.cloud.bot.dto.UnhandledHttpRequestDto;
import cyou.wssy001.cloud.bot.entity.UnhandledHttpRequest;
import cyou.wssy001.cloud.bot.service.LogSendCallbackService;
import lombok.RequiredArgsConstructor;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UnhandledSendMessageSorter implements MessageListenerConcurrently {
    private final RocketMQTemplate rocketMQTemplate;
    private final LogSendCallbackService logSendCallbackService;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messageExtList, ConsumeConcurrentlyContext context) {
        List<Message<UnhandledHttpRequestDto>> messageList = messageExtList.parallelStream()
                .map(v -> JSON.parseObject(new String(v.getBody()), UnhandledHttpRequest.class))
                .map(this::toUnhandledHttpRequestDto)
                .filter(Objects::nonNull)
                .map(v -> MessageBuilder.withPayload(v)
                        .setHeader("KEYS", "UnhandledHttpRequestDto_" + v.getId())
                        .build())
                .collect(Collectors.toList());

        rocketMQTemplate.asyncSend("unhandled-group-plain-text-message", messageList, logSendCallbackService);

        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    private UnhandledHttpRequestDto toUnhandledHttpRequestDto(UnhandledHttpRequest unhandledHttpRequest) {
        if (!unhandledHttpRequest.getMethod().equals("/send/msg") || unhandledHttpRequest.getGroupId() == null)
            return null;
        MessageChainBuilder append = new MessageChainBuilder()
                .append(new PlainText(unhandledHttpRequest.getMsg()))
                .append(new At(unhandledHttpRequest.getQQ()));

        return new UnhandledHttpRequestDto(unhandledHttpRequest.getId(), MiraiCode.serializeToMiraiCode(append), unhandledHttpRequest.getGroupId());

    }

}
