package cyou.wssy001.cloud.bot.entity;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@Component
@RefreshScope
public class DynamicProperty {

    @Value("${accounts}")
    private String accounts;

}
