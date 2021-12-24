package cyou.wssy001.cloud.bot.sorter;

import com.alibaba.fastjson.JSON;
import cyou.wssy001.cloud.bot.dto.ImageDto;
import cyou.wssy001.cloud.bot.dto.MessageChainDto;
import cyou.wssy001.cloud.bot.service.LogSendCallbackService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
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

@Component
@RequiredArgsConstructor
@RocketMQMessageListener(topic = "group-message", consumerGroup = "image-message")
public class ImageMessageSorter implements RocketMQListener<String> {
    private final RocketMQTemplate rocketMQTemplate;
    private final LogSendCallbackService logSendCallbackService;

    @Override
    public void onMessage(String message) {
        MessageChainDto messageChainDto = JSON.parseObject(message, MessageChainDto.class);
        int[] ids = messageChainDto.getIds();

        MessageChain messageChain = MiraiCode.deserializeMiraiCode(messageChainDto.getMiraiCode());
        ImageDto imageDto = new ImageDto();
        imageDto.setBotAccount(messageChainDto.getBotAccount());
        imageDto.setGroupNumber(messageChainDto.getGroupNumber());

        List<ImageDto> imageDtoList = new ArrayList<>();
        for (int i = 0; i < messageChain.size(); i++) {
            SingleMessage singleMessage = messageChain.get(i);
            if (singleMessage instanceof Image)
                imageDtoList.add(saveImage(imageDto, (Image) singleMessage, ids[i]));
        }

        if (imageDtoList.isEmpty()) return;
        send(imageDtoList);
    }


    //    存储图片
    private ImageDto saveImage(ImageDto imageDto, Image image, Integer miraiId) {
        imageDto.setMiraiId(miraiId);
        String url = Image.queryUrl(image);

        return imageDto;
    }

    @SneakyThrows
    private void send(List<ImageDto> imageDtoList) {
        List<Message<ImageDto>> messageList = imageDtoList.parallelStream()
                .map(v -> MessageBuilder.withPayload(v).setHeader("KEYS", "ImageDto_" + v.getId()).build())
                .collect(Collectors.toList());

        rocketMQTemplate.asyncSend("image-message", messageList, logSendCallbackService);
    }
}
