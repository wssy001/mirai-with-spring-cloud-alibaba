package cyou.wssy001.cloud.bot.service;

import cyou.wssy001.cloud.bot.entity.UnhandledHttpRequest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Service
public class UnhandledHttpRequestService {
    @Resource
    private HashOperations<String, String, UnhandledHttpRequest> hashOperations;
    public static final String HASH_KEY = UnhandledHttpRequest.class.getSimpleName();

    public UnhandledHttpRequest get(String id) {
        return Optional.ofNullable(hashOperations.get(HASH_KEY, id))
                .orElseGet(UnhandledHttpRequest::new);
    }

    public List<UnhandledHttpRequest> getAll() {
        return hashOperations.values(HASH_KEY);
    }

    public Boolean upset(UnhandledHttpRequest unhandledHttpRequest) {
        return hashOperations.putIfAbsent(HASH_KEY, unhandledHttpRequest.getId(), unhandledHttpRequest);
    }

    public Boolean delete(UnhandledHttpRequest unhandledHttpRequest) {
        return hashOperations.delete(HASH_KEY, unhandledHttpRequest.getId())
                .equals(1L);
    }
}
