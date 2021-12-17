package cyou.wssy001.cloud.bot.entity;

import cn.hutool.core.util.IdUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class UnhandledHttpRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    private String body;
    private String method;

    public UnhandledHttpRequest() {
        id = IdUtil.fastSimpleUUID();
    }

    public UnhandledHttpRequest(String body) {
        id = IdUtil.fastSimpleUUID();
        this.body = body;
    }
}
