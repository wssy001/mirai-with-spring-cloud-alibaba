package cyou.wssy001.cloud.bot.handler;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import cyou.wssy001.cloud.bot.entity.RepetitiveGroup;
import cyou.wssy001.cloud.bot.service.RepetitiveGroupService;
import cyou.wssy001.cloud.bot.service.SentinelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.events.BotOfflineEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotOfflineHandler {
    private final SentinelService sentinelService;
    private final RepetitiveGroupService repetitiveGroupService;

    public void handle(@NotNull BotOfflineEvent event) {
        Bot bot = event.getBot();
        long id = bot.getId();
        String reason = "未知";

        if (event instanceof BotOfflineEvent.Active)
            reason = "主动下线";

        if (event instanceof BotOfflineEvent.Force)
            reason = "被挤下线";

        if (event instanceof BotOfflineEvent.Dropped)
            reason = "被服务器断开或因网络问题而掉线";

        if (event instanceof BotOfflineEvent.RequireReconnect)
            reason = "服务器主动要求更换另一个服务器";

        log.info("******BotOfflineHandler：QQ：{}，Reason：{}", id, reason);

        if (Bot.getInstances().isEmpty()) {
            FlowRule rule = new FlowRule("/send/msg");
            rule.setCount(0);
            rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
            rule.setLimitApp("default");

            sentinelService.saveOrUpdateFlowRule(rule);
            rule.setResource("/collect/msg");
            sentinelService.saveOrUpdateFlowRule(rule);
        }

        repetitiveGroupService.getAll()
                .parallelStream()
                .filter(v -> v.getBotIds().contains(id))
                .map(v -> updateBotIdList(id, v))
                .forEach(this::updateOrDeleteRepetitiveGroup);

        bot.close();
    }

    @NotNull
    private RepetitiveGroup updateBotIdList(long id, RepetitiveGroup repetitiveGroup) {
        List<Long> botIds = repetitiveGroup.getBotIds();
        botIds.remove(id);
        repetitiveGroup.setBotIds(botIds);
        return repetitiveGroup;
    }

    private void updateOrDeleteRepetitiveGroup(RepetitiveGroup repetitiveGroup) {
        if (repetitiveGroup.getBotIds().size() < 2) {
            repetitiveGroupService.delete(repetitiveGroup);
        } else {
            repetitiveGroupService.upset(repetitiveGroup);
        }
    }
}
