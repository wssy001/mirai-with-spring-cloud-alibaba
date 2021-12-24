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
public class TImage {

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
     * url
     */
    private String url;

    /**
     * 本地路径
     */
    private String path;

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
        TImage image = (TImage) o;
        return Objects.equals(id, image.id) &&
                Objects.equals(miraiId, image.miraiId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, miraiId);
    }
}
