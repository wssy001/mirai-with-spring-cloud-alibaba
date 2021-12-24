package cyou.wssy001.cloud.bot.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Objects;


@Data
@Builder
@Document
@AllArgsConstructor
public class TPlainText {

    /**
     * 自增ID
     */
    @Id
    private Long id;

    /**
     * mirai消息ID
     */
    private Integer miraiId;

    /**
     * 消息内容
     */
    private String text;

    /**
     * 机器人账号
     */
    private Integer botNumber;

    /**
     * 群号
     */
    private Integer groupNumber;

    /**
     * 好友账号
     */
    private Integer friendNumber;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否启用
     */
    private Boolean enable;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TPlainText plainText = (TPlainText) o;
        return Objects.equals(id, plainText.id) &&
                Objects.equals(miraiId, plainText.miraiId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, miraiId);
    }
}
