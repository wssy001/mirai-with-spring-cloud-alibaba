package cyou.wssy001.cloud.bot.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class TestFallBackService {

    public Mono<String> get() {
        return Mono.just("fallback")
                .cache();
    }
}
