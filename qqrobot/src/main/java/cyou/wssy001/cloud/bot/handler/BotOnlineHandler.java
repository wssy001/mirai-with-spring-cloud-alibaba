package cyou.wssy001.cloud.bot.handler;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import cyou.wssy001.cloud.bot.service.SentinelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.events.BotOnlineEvent;
import org.springframework.data.redis.core.ReactiveSetOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotOnlineHandler {
    private final SentinelService sentinelService;
    @Resource
    private ReactiveSetOperations<String, Long> reactiveSetOperations;

    public void handle(@NotNull BotOnlineEvent event) {
        Bot bot = event.getBot();
        long id = bot.getId();

        log.info("******BotOnlineHandler：QQ：{}", id);

        List<Bot> instances = Bot.getInstances();
        if (instances.size() == 1) {
            FlowRule rule = new FlowRule("/send/msg");
            rule.setCount(10);
            rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
            rule.setLimitApp("default");

            sentinelService.saveOrUpdateFlowRule(rule);
            rule.setResource("/collect/msg");
            sentinelService.saveOrUpdateFlowRule(rule);
        }

    }
}
