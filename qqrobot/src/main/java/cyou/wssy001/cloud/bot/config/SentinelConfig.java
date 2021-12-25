package cyou.wssy001.cloud.bot.config;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.SentinelWebInterceptor;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.config.SentinelWebMvcConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SentinelConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        SentinelWebMvcConfig config = new SentinelWebMvcConfig();
        config.setHttpMethodSpecify(true);
        registry.addInterceptor(new SentinelWebInterceptor(config))
                .addPathPatterns("/**");
    }
}
