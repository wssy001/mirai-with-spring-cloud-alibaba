package cyou.wssy001.cloud.bot.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LogSendCallbackService implements SendCallback {
    @Override
    public void onSuccess(SendResult sendResult) {
        log.info("******发送成功！，消息ID：{}", sendResult.getMsgId());
    }

    @Override
    public void onException(Throwable e) {
        log.info("******发送失败！，错误信息：{}", e.getMessage());
    }
}
