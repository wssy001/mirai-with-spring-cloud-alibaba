package cyou.wssy001.cloud.bot.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Accessors(chain = true)
@TableName("t_image")
public class TImage extends Model<TImage> {

    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * mirai消息ID
     */
    @TableField("mirai_id")
    private Integer miraiId;

    /**
     * url
     */
    @TableField("url")
    private String url;

    /**
     * 本地路径
     */
    @TableField("path")
    private String path;

    /**
     * 机器人账号
     */
    @TableField("bot_number")
    private Integer botNumber;

    /**
     * 群号
     */
    @TableField("group_number")
    private Integer groupNumber;

    /**
     * 好友账号
     */
    @TableField("friend_number")
    private Integer friendNumber;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 是否启用
     */
    @TableField("`enable`")
    @TableLogic
    private Boolean enable;


    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
