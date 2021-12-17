package cyou.wssy001.cloud.bot.config;

import com.alibaba.csp.sentinel.adapter.spring.webflux.SentinelWebFluxFilter;
import com.alibaba.csp.sentinel.adapter.spring.webflux.exception.SentinelBlockExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SentinelConfig {
    private final List<ViewResolver> viewResolvers;
    private final ServerCodecConfigurer serverCodecConfigurer;

    @Bean
    @Order(-1)
    public SentinelBlockExceptionHandler sentinelBlockExceptionHandler() {
        return new SentinelBlockExceptionHandler(viewResolvers, serverCodecConfigurer);
    }

    @Bean
    @Order(-1)
    public SentinelWebFluxFilter sentinelWebFluxFilter() {
        return new SentinelWebFluxFilter();
    }
}
