package cyou.wssy001.cloud.bot.dto;

import lombok.Data;

@Data
public abstract class BaseMessageDto {
    private Long id;
    private Integer miraiId;
    private Long botAccount;
    private Long groupNumber;
    private Long friendNumber;
}
