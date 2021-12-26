package cyou.wssy001.cloud.bot.service;

import com.alibaba.fastjson.JSON;
import cyou.wssy001.cloud.bot.entity.BotAccount;
import cyou.wssy001.cloud.bot.entity.DynamicProperty;
import cyou.wssy001.cloud.bot.entity.UnhandledHttpRequest;
import cyou.wssy001.cloud.bot.handler.BotOfflineHandler;
import cyou.wssy001.cloud.bot.handler.BotOnlineHandler;
import cyou.wssy001.cloud.bot.handler.GroupMessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.event.events.BotOfflineEvent;
import net.mamoe.mirai.event.events.BotOnlineEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.network.LoginFailedException;
import net.mamoe.mirai.utils.BotConfiguration;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RobotService {
    private final DynamicProperty dynamicProperty;
    private final UnhandledHttpRequestService unhandledHttpRequestService;
    private final RocketMQTemplate rocketMQTemplate;
    private final LogSendCallbackService logSendCallbackService;

    private final GroupMessageHandler groupMessageHandler;
    private final BotOnlineHandler botOnlineHandler;
    private final BotOfflineHandler botOfflineHandler;

    @PostConstruct
    private void init() {
        login(stringToBotAccountList(dynamicProperty.getAccounts()));
        handleStoredHttpRequest();
    }

    public void refreshBot() {
        List<BotAccount> botAccounts = stringToBotAccountList(dynamicProperty.getAccounts())
                .stream()
                .filter(v -> Bot.getInstanceOrNull(v.getAccount()) == null)
                .collect(Collectors.toList());

        login(botAccounts);
    }

    private void login(List<BotAccount> botAccounts) {

        BotConfiguration botConfiguration = new BotConfiguration();
        botConfiguration.fileBasedDeviceInfo("device.json");
//        botConfiguration.noNetworkLog();
        botConfiguration.noBotLog();

        for (BotAccount botAccountInfo : botAccounts) {

            Bot bot = BotFactory.INSTANCE.newBot(botAccountInfo.getAccount(), botAccountInfo.getPassword(), botConfiguration);
            try {
                bot.getEventChannel()
                        .subscribeAlways(GroupMessageEvent.class, groupMessageHandler::handle);
                bot.getEventChannel()
                        .subscribeAlways(BotOnlineEvent.class, botOnlineHandler::handle);
                bot.getEventChannel()
                        .subscribeAlways(BotOfflineEvent.class, botOfflineHandler::handle);
                bot.login();
                log.info("******RobotService QQ：{} 登陆成功", botAccountInfo.getAccount());

                break;
            } catch (LoginFailedException e) {
                bot.close();
                log.error("******Bot LoginFailedException：{} ，QQ：{}", e.getMessage(), botAccountInfo.getAccount());
            }
        }

    }

    private void handleStoredHttpRequest() {
        List<Message<UnhandledHttpRequest>> messageList = unhandledHttpRequestService.getAll()
                .parallelStream()
                .filter(Objects::nonNull)
                .map(v -> MessageBuilder.withPayload(v).build())
                .collect(Collectors.toList());

        if (messageList.isEmpty()) return;

        rocketMQTemplate.asyncSend("unhandled-group-message", messageList, logSendCallbackService);
    }

    private List<BotAccount> stringToBotAccountList(String s) {
        return JSON.parseArray(s, BotAccount.class);
    }
}
