package cyou.wssy001.cloud.bot.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import cyou.wssy001.cloud.bot.controller.BotController;
import cyou.wssy001.cloud.bot.entity.BotAccount;
import cyou.wssy001.cloud.bot.entity.DynamicProperty;
import cyou.wssy001.cloud.bot.entity.UnhandledHttpRequest;
import cyou.wssy001.cloud.bot.handler.GroupMessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.network.LoginFailedException;
import net.mamoe.mirai.utils.BotConfiguration;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RobotService {
    private final DynamicProperty dynamicProperty;
    private final UnhandledHttpRequestService unhandledHttpRequestService;
    private final BotController botController;

    private final GroupMessageHandler groupMessageHandler;

    @PostConstruct
    private void init() {
        login(stringToBotAccountList(dynamicProperty.getAccounts()));
//        handleStoredHttpRequest();
    }


    public void refreshBot() {
        List<BotAccount> botAccounts = stringToBotAccountList(dynamicProperty.getAccounts())
                .stream()
                .filter(v -> Bot.getInstanceOrNull(v.getAccount()) == null)
                .collect(Collectors.toList());

//        login(botAccounts);
    }

    private void login(List<BotAccount> botAccounts) {

        BotConfiguration botConfiguration = new BotConfiguration();
        botConfiguration.fileBasedDeviceInfo("device.json");
//        botConfiguration.noNetworkLog();
        botConfiguration.noBotLog();

        for (BotAccount botAccountInfo : botAccounts) {

            Bot bot = BotFactory.INSTANCE.newBot(botAccountInfo.getAccount(), botAccountInfo.getPassword(), botConfiguration);
            try {
                bot.getEventChannel().subscribeAlways(GroupMessageEvent.class, groupMessageHandler::handle);
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
        List<JSONObject> block = unhandledHttpRequestService.getAll()
                .map(v -> JSON.parseObject(v.getBody()))
                .filter(v -> v.containsKey("groupId"))
                .share()
                .collectList()
                .block();

        if (block == null) return;

        Map<Long, List<UnhandledHttpRequest>> temp = new HashMap<>();

        block.forEach(v -> {
            List<UnhandledHttpRequest> list;
            if (!temp.containsKey(v.getLong("groupId"))) {
                list = new ArrayList<>();
            } else {
                list = temp.get(v.getLong("groupId"));
            }

            list.add(JSON.toJavaObject(v, UnhandledHttpRequest.class));
            temp.put(v.getLong("groupId"), list);
        });

        temp.forEach((k, v) -> {
//            RepetitiveGroup repetitiveGroup = repetitiveGroupService.get(k).share().block();
//            List<Long> botIds = repetitiveGroup.getBotIds();
//
//            v.parallelStream()
//                    .forEach(b -> {
//                        JSONObject jsonObject = JSON.parseObject(b.getBody());
//                        Random random = new Random();
//
//                        if (b.getMethod().equals("/send/msg")) {
//                            botController.sendMsg(jsonObject.getString("msg"), botIds.get(random.nextInt(botIds.size())),
//                                    jsonObject.getLong("groupId"), jsonObject.getLong("qq"));
//                        } else {
//                            botController.collectMsg(botIds.get(random.nextInt(botIds.size())),
//                                    jsonObject.getLong("groupId"), jsonObject.getLong("qq"));
//                        }
//
//                        unhandledHttpRequestService.delete(b);
//                    });

        });
    }

    private List<BotAccount> stringToBotAccountList(String s) {
        return JSON.parseArray(s, BotAccount.class);
    }
}
