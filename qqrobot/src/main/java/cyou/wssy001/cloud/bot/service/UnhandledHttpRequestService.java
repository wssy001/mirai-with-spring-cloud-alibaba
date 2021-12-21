package cyou.wssy001.cloud.bot.service;

import cyou.wssy001.cloud.bot.entity.UnhandledHttpRequest;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

@Service
public class UnhandledHttpRequestService {
    @Resource
    private ReactiveRedisOperations<String, UnhandledHttpRequest> reactiveRedisOperations;
    public static final String HASH_KEY = UnhandledHttpRequestService.class.getSimpleName();

    public Mono<UnhandledHttpRequest> get(String id) {
        return reactiveRedisOperations.opsForHash()
                .get(HASH_KEY, id)
                .map(v -> (UnhandledHttpRequest) v)
                .switchIfEmpty(Mono.defer(() -> Mono.just(new UnhandledHttpRequest())));
    }

    public Flux<UnhandledHttpRequest> getAll() {
        return reactiveRedisOperations.opsForHash()
                .values(HASH_KEY)
                .map(v -> (UnhandledHttpRequest) v);
    }

    public Mono<Boolean> upset(UnhandledHttpRequest unhandledHttpRequest) {
        return reactiveRedisOperations.opsForHash()
                .put(HASH_KEY, unhandledHttpRequest.getId(), unhandledHttpRequest);
    }

    public Mono<Boolean> delete(UnhandledHttpRequest unhandledHttpRequest) {
        return reactiveRedisOperations.opsForHash()
                .remove(HASH_KEY, unhandledHttpRequest.getId())
                .map(v -> v.equals(1L));
    }
}
