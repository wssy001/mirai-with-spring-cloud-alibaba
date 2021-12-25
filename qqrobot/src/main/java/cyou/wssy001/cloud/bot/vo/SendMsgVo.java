package cyou.wssy001.cloud.bot.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMsgVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String msg;
    private Long botId;
    private Long groupId;
    private Long qq;

    public Long getQQ() {
        return qq;
    }

    public void setQQ(Long qq) {
        this.qq = qq;
    }
}
