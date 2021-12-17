package cyou.wssy001.cloud.bot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepetitiveGroup implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;
    private List<Long> botIds;
}
