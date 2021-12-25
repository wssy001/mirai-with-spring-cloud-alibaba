package cyou.wssy001.cloud.bot.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import cyou.wssy001.cloud.bot.Controller.BotController;
import cyou.wssy001.cloud.bot.entity.BotAccount;
import cyou.wssy001.cloud.bot.entity.DynamicProperty;
import cyou.wssy001.cloud.bot.entity.RepetitiveGroup;
import cyou.wssy001.cloud.bot.entity.UnhandledHttpRequest;
import cyou.wssy001.cloud.bot.handler.*;
import cyou.wssy001.cloud.bot.vo.CollectMsgVo;
import cyou.wssy001.cloud.bot.vo.SendMsgVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.event.events.*;
import net.mamoe.mirai.network.LoginFailedException;
import net.mamoe.mirai.utils.BotConfiguration;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RobotService {
    private final DynamicProperty dynamicProperty;
    private final UnhandledHttpRequestService unhandledHttpRequestService;
    private final RepetitiveGroupService repetitiveGroupService;
    private final BotController botController;

    private final BotOnlineHandler botOnlineHandler;
    private final BotOfflineHandler botOfflineHandler;
    private final GroupMessageHandler groupMessageHandler;
    private final MemberJoinHandler memberJoinHandler;
    private final MemberLeaveHandler memberLeaveHandler;

    //    @PostConstruct
    private void init() {
        login(stringToBotAccountList(dynamicProperty.getAccounts()));
        handleStoredHttpRequest();
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
                bot.getEventChannel().subscribeAlways(BotOnlineEvent.class, botOnlineHandler::handle);
                bot.getEventChannel().subscribeAlways(BotOfflineEvent.class, botOfflineHandler::handle);
                bot.getEventChannel().subscribeAlways(GroupMessageEvent.class, groupMessageHandler::handle);
                bot.getEventChannel().subscribeAlways(MemberJoinEvent.class, memberJoinHandler::handle);
                bot.getEventChannel().subscribeAlways(MemberLeaveEvent.class, memberLeaveHandler::handle);
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
                .parallelStream()
                .map(v -> JSON.parseObject(v.getBody()))
                .filter(v -> v.containsKey("groupId"))
                .collect(Collectors.toList());

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
            RepetitiveGroup repetitiveGroup = repetitiveGroupService.get(k);
            List<Long> botIds = repetitiveGroup.getBotIds();

            v.parallelStream()
                    .forEach(b -> {
                        JSONObject jsonObject = JSON.parseObject(b.getBody());
                        Random random = new Random();

                        if (b.getMethod().equals("/send/msg")) {
                            SendMsgVo sendMsgVo = jsonObject.toJavaObject(SendMsgVo.class);
                            int index = random.nextInt(botIds.size());
                            sendMsgVo.setBotId(botIds.get(index));

                            botController.sendMsg(sendMsgVo);
                        } else {
                            CollectMsgVo collectMsgVo = jsonObject.toJavaObject(CollectMsgVo.class);
                            int index = random.nextInt(botIds.size());
                            collectMsgVo.setBotId(botIds.get(index));

                            botController.collectMsg(collectMsgVo);
                        }

                        unhandledHttpRequestService.delete(b);
                    });

        });
    }

    private List<BotAccount> stringToBotAccountList(String s) {
        return JSON.parseArray(s, BotAccount.class);
    }
}
