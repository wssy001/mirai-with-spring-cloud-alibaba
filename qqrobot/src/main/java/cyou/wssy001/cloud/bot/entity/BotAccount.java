package cyou.wssy001.cloud.bot.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class BotAccount implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long account;
    private String password;
}
