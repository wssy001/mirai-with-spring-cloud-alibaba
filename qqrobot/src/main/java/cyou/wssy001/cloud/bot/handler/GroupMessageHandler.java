package cyou.wssy001.cloud.bot.handler;

import cn.hutool.core.util.IdUtil;
import cyou.wssy001.cloud.bot.entity.RepetitiveGroup;
import cyou.wssy001.cloud.bot.service.RepetitiveGroupService;
import lombok.RequiredArgsConstructor;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class GroupMessageHandler {
    private final RedissonClient redissonClient;
    private final RepetitiveGroupService repetitiveGroupService;
    @Resource
    private ZSetOperations<Long, Integer> zSetOperations;

    public void handle(@NotNull GroupMessageEvent event) {
        long groupId = event.getGroup().getId();
        long botId = event.getBot().getId();

        RepetitiveGroup repetitiveGroup = repetitiveGroupService.get(groupId);

        RLock lock = null;
        if (hasBeenConsumed(event, groupId)) return;

        if (containMultiBots(repetitiveGroup, botId)) {
            lock = redissonClient.getLock(IdUtil.fastSimpleUUID());
            lock.lock(20, TimeUnit.SECONDS);
        }

        consume();

        if (lock != null) lock.unlock();
    }

    //    检查事件是否以及被消费过
    private boolean hasBeenConsumed(@NotNull GroupMessageEvent event, long groupId) {

        long second = new Date().getTime();
        zSetOperations.removeRangeByScore(groupId, 0, second);

        int[] ids = event.getSource().getIds();
        Set<ZSetOperations.TypedTuple<Integer>> list = new HashSet<>();
        for (int id : ids) {
            list.add(new DefaultTypedTuple<>(id, second + 10.0));
        }

        Long unExist = zSetOperations.add(groupId, list);

        return unExist == null || unExist == 0;
    }

    //    检查这个群是否有多个机器人监听群消息
    private boolean containMultiBots(RepetitiveGroup repetitiveGroup, Long botId) {
        return repetitiveGroup.getId() != null && repetitiveGroup.getBotIds().contains(botId);
    }

    //    消费事件
    private void consume() {

    }
}
