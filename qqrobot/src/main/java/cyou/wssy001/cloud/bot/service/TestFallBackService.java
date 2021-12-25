package cyou.wssy001.cloud.bot.service;

import org.springframework.stereotype.Service;

@Service
public class TestFallBackService {

    public String get() {
        return "fallback";
    }
}
