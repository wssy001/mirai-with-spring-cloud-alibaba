package cyou.wssy001.cloud.bot.sorter;

import com.alibaba.fastjson.JSON;
import cyou.wssy001.cloud.bot.dto.MessageChainDto;
import cyou.wssy001.cloud.bot.dto.PlainTextDto;
import cyou.wssy001.cloud.bot.service.LogSendCallbackService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.SingleMessage;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(topic = "group-message", consumerGroup = "plain-text-message")
public class PlainTextMessageSorter implements RocketMQListener<String> {
    private final RocketMQTemplate rocketMQTemplate;
    private final LogSendCallbackService logSendCallbackService;

    @Override
    public void onMessage(String message) {
        MessageChainDto messageChainDto = JSON.parseObject(message, MessageChainDto.class);
        int[] ids = messageChainDto.getIds();

        MessageChain messageChain = MiraiCode.deserializeMiraiCode(messageChainDto.getMiraiCode());
        PlainTextDto plainTextDto = new PlainTextDto();
        plainTextDto.setBotAccount(messageChainDto.getBotAccount());
        plainTextDto.setGroupNumber(messageChainDto.getGroupNumber());

        List<PlainTextDto> plainTextDtoList = new ArrayList<>();
        for (int i = 0; i < messageChain.size(); i++) {
            SingleMessage singleMessage = messageChain.get(i);
            if (singleMessage instanceof PlainText)
                plainTextDtoList.add(box(plainTextDto, (PlainText) singleMessage, ids[i]));
        }

        if (plainTextDtoList.isEmpty()) return;
        send(plainTextDtoList);
    }

    private PlainTextDto box(PlainTextDto plainTextDto, PlainText plainText, Integer miraiCode) {
        plainTextDto.setMiraiId(miraiCode);
        plainTextDto.setText(plainText.getContent());
        return plainTextDto;
    }

    @SneakyThrows
    private void send(List<PlainTextDto> plainTextDto) {

        List<Message<PlainTextDto>> messageList = plainTextDto.parallelStream()
                .map(v -> MessageBuilder.withPayload(v)
                        .setHeader("KEYS", "PlainTextDto_" + v.getId())
                        .build())
                .collect(Collectors.toList());

        rocketMQTemplate.asyncSend("plain-text-message", messageList, logSendCallbackService);
    }
}
