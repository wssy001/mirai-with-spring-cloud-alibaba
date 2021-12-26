package cyou.wssy001.cloud.bot.controller;

import cn.hutool.core.date.DateUtil;
import cyou.wssy001.cloud.bot.entity.DynamicProperty;
import cyou.wssy001.cloud.bot.vo.SendMsgVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {
    private final DynamicProperty dynamicProperty;
    @Resource
    private ZSetOperations<Long, Integer> zSetOperations;

    @GetMapping("/get")
    public String getAccounts(
            @RequestBody SendMsgVo sendMsgVo
    ) {
        return dynamicProperty.getAccounts();
    }


    @GetMapping("/test")
    public void test() {
//        Boolean block = reactiveZSetOperations.add(-1001L, -10, 1.0)
//                .share()
//                .block();
//        log.info("******{}", block);
//        block = reactiveZSetOperations.add(-1001L, -10, 2.0)
//                .share()
//                .block();
//        log.info("******{}", block);

        int[] ids = {1, 2, 3, 4, 5};
        int second = DateUtil.thisSecond();
        Set<ZSetOperations.TypedTuple<Integer>> list = new HashSet<>();
        for (int id : ids) {
            list.add(new DefaultTypedTuple<>(id, second + 10.0));
        }

        Long unExist = zSetOperations.add(100L, list);

        log.info("******{}", unExist);
    }
}