package cyou.wssy001.cloud.bot.consumer;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import cyou.wssy001.cloud.bot.dto.ImageDto;
import cyou.wssy001.cloud.bot.entity.TImage;
import cyou.wssy001.cloud.bot.service.TImageService;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ImageMessageDBConsumer implements MessageListenerConcurrently {
    private final TImageService tImageService;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messageExtList, ConsumeConcurrentlyContext context) {
        List<TImage> imageList = messageExtList.parallelStream()
                .map(v -> JSON.parseObject(new String(v.getBody()), ImageDto.class))
                .map(this::toTImage)
                .collect(Collectors.toList());

        tImageService.saveBatch(imageList)
                .share()
                .collectList()
                .block();

        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    private TImage toTImage(ImageDto imageDto) {
        TImage image = TImage.builder().build();
        BeanUtil.copyProperties(imageDto, image);
        return image;
    }
}
