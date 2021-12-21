package cyou.wssy001.cloud.bot.consumer;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import cyou.wssy001.cloud.bot.dto.PlainTextDto;
import cyou.wssy001.cloud.bot.entity.TPlainText;
import cyou.wssy001.cloud.bot.service.TPlainTextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlainTextMessageDBConsumer implements MessageListenerConcurrently {
    private final TPlainTextService tPlainTextService;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messageExtList, ConsumeConcurrentlyContext context) {

        List<TPlainText> plainTextList = messageExtList.parallelStream()
                .map(v -> JSON.parseObject(new String(v.getBody()), PlainTextDto.class))
                .map(this::toTPlainText)
                .collect(Collectors.toList());

        tPlainTextService.saveBatch(plainTextList);

        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    private TPlainText toTPlainText(PlainTextDto plainTextDto) {
        TPlainText plainText = new TPlainText();
        BeanUtil.copyProperties(plainTextDto, plainText);
        return plainText;
    }
}
