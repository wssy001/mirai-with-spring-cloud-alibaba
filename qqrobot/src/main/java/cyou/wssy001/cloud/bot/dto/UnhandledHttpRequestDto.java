package cyou.wssy001.cloud.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnhandledHttpRequestDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    private String miraiCode;
    private Long groupId;
}
