package cyou.wssy001.cloud.bot.Controller;

import cn.hutool.core.date.DateUtil;
import cyou.wssy001.cloud.bot.entity.DynamicProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ReactiveZSetOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {
    private final DynamicProperty dynamicProperty;
    @Resource
    private ReactiveZSetOperations<Long, Integer> reactiveZSetOperations;

    @GetMapping("/get")
    public Mono<String> getAccounts() {
        return Mono.just(dynamicProperty.getAccounts())
                .cache();
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
        List<DefaultTypedTuple<Integer>> list = new ArrayList<>();
        for (int id : ids) {
            list.add(new DefaultTypedTuple<>(id, second + 10.0));
        }

        Long unExist = reactiveZSetOperations.addAll(100L, list)
                .share()
                .block();

        log.info("******{}", unExist);
    }
}
