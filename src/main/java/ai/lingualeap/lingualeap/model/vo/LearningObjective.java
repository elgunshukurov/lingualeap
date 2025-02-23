package ai.lingualeap.lingualeap.model.vo;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class LearningObjective {
    private String objective;
    private String description;
    private Integer requiredScore;
}

