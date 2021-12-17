package cyou.wssy001.cloud.bot.handler;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import cyou.wssy001.cloud.bot.entity.RepetitiveGroup;
import cyou.wssy001.cloud.bot.service.RepetitiveGroupService;
import cyou.wssy001.cloud.bot.service.SentinelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.BotOnlineEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.ReactiveSetOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotOnlineHandler {
    private final SentinelService sentinelService;
    private final RepetitiveGroupService repetitiveGroupService;
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

        Set<Long> groupIds = bot.getGroups()
                .parallelStream()
                .map(Group::getId)
                .collect(Collectors.toSet());

        groupIds.parallelStream()
                .forEach(v -> reactiveSetOperations.add(id + "", v));

        instances.removeIf(v -> v.getId() == id);
        instances.parallelStream()
                .forEach(v -> sinter(id, v.getId()));

    }

    private void sinter(Long self, Long target) {
        ArrayList<Long> list = new ArrayList<>();
        list.add(self);
        list.add(target);

        reactiveSetOperations.intersect(self + "", target + "")
                .subscribe(v -> insert(v, list));
    }

    private void insert(Long groupId, List<Long> botIds) {
        RepetitiveGroup repetitiveGroup = repetitiveGroupService.get(groupId)
                .share()
                .block();

        if (repetitiveGroup.getId() == null)
            repetitiveGroup.setBotIds(new ArrayList<>());

        Set<Long> set = new HashSet<>(botIds);
        set.addAll(repetitiveGroup.getBotIds());
        repetitiveGroup.setBotIds(new ArrayList<>(set));
        repetitiveGroupService.upset(repetitiveGroup);
    }
}
