package cyou.wssy001.cloud.bot.service;

import cyou.wssy001.cloud.bot.entity.RepetitiveGroup;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Service
public class RepetitiveGroupService {
    @Resource
    private HashOperations<String, Long, RepetitiveGroup> hashOperations;
    public static final String HASH_KEY = RepetitiveGroup.class.getSimpleName();

    public RepetitiveGroup get(Long groupId) {
        return Optional.ofNullable(hashOperations.get(HASH_KEY, groupId))
                .orElseGet(RepetitiveGroup::new);
    }

    public List<RepetitiveGroup> getAll() {
        return hashOperations.values(HASH_KEY);
    }

    public Boolean upset(RepetitiveGroup repetitiveGroup) {
        return hashOperations.putIfAbsent(HASH_KEY, repetitiveGroup.getId(), repetitiveGroup);
    }

    public boolean delete(RepetitiveGroup repetitiveGroup) {
        return hashOperations.delete(HASH_KEY, repetitiveGroup.getId())
                .equals(1L);
    }
}
