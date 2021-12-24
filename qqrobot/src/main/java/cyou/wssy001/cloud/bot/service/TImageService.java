package cyou.wssy001.cloud.bot.service;

import cyou.wssy001.cloud.bot.entity.TImage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TImageService {
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public Flux<TImage> saveBatch(List<TImage> list) {
        return reactiveMongoTemplate.insertAll(list);
    }
}
