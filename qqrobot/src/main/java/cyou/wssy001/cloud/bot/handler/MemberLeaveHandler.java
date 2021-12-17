package cyou.wssy001.cloud.bot.handler;

import cn.hutool.core.util.IdUtil;
import cyou.wssy001.cloud.bot.entity.RepetitiveGroup;
import cyou.wssy001.cloud.bot.service.RepetitiveGroupService;
import lombok.RequiredArgsConstructor;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.MemberLeaveEvent;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MemberLeaveHandler {
    private final RedissonClient redissonClient;
    private final RepetitiveGroupService repetitiveGroupService;

    public void handle(@NotNull MemberLeaveEvent event) {
        long memberId = event.getMember().getId();
        Group group = event.getGroup();
        if (event.getBot().getId() == memberId) return;

        RLock lock = redissonClient.getLock(IdUtil.fastSimpleUUID());
        lock.lock(30, TimeUnit.SECONDS);

        List<Long> idList = Bot.getInstances()
                .parallelStream()
                .map(Bot::getId)
                .collect(Collectors.toList());

        if (!idList.contains(memberId)) return;

        RepetitiveGroup repetitiveGroup = repetitiveGroupService.get(group.getId())
                .share()
                .block();

        if (repetitiveGroup.getId() == null) return;

        List<Long> botIds = idList.parallelStream()
                .filter(group::contains)
                .collect(Collectors.toList());

        if (botIds.size() > 1) {
            repetitiveGroup.setId(group.getId());
            repetitiveGroup.setBotIds(botIds);
            repetitiveGroupService.upset(repetitiveGroup);
        }

        if (botIds.size() == 1)
            repetitiveGroupService.delete(repetitiveGroup);

        lock.unlock();
    }
}
