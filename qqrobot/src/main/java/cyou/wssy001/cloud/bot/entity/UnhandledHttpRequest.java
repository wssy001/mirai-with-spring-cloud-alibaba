package cyou.wssy001.cloud.bot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnhandledHttpRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    private String msg;
    private Long botId;
    private Long groupId;
    private Long qq;
    private String method;

    public Long getQQ() {
        return qq;
    }

    public void setQQ(Long qq) {
        this.qq = qq;
    }
}
