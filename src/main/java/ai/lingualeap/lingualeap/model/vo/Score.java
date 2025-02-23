package ai.lingualeap.lingualeap.model.vo;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Embeddable
public class Score {
    private Integer value;
    private Integer maxPossible;
    private LocalDateTime achievedAt;
}
