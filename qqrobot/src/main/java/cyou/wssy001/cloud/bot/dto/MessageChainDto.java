package cyou.wssy001.cloud.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MessageChainDto extends BaseMessageDto implements Serializable {
    static final long serialVersionUID = 1L;

    private int[] ids;
    private String miraiCode;

}
