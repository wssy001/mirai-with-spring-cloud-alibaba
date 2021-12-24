package cyou.wssy001.cloud.bot.service;

import cyou.wssy001.cloud.bot.entity.TPlainText;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TPlainTextService {
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public Flux<TPlainText> saveBatch(List<TPlainText> list) {
        return reactiveMongoTemplate.insertAll(list);
    }
}
