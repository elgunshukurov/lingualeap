package ai.lingualeap.lingualeap.dao.repository.specification;

import ai.lingualeap.lingualeap.dao.entity.Lesson;
import ai.lingualeap.lingualeap.model.enums.LessonLevel;
import ai.lingualeap.lingualeap.model.enums.LessonStatus;
import ai.lingualeap.lingualeap.model.enums.LessonType;
import org.springframework.data.jpa.domain.Specification;

public class LessonSpecifications {
    public static Specification<Lesson> hasType(LessonType type) {
        return (root, query, cb) -> type == null ? null : cb.equal(root.get("type"), type);
    }

    public static Specification<Lesson> hasLevel(LessonLevel level) {
        return (root, query, cb) -> level == null ? null : cb.equal(root.get("level"), level);
    }

    public static Specification<Lesson> hasStatus(LessonStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Lesson> hasAiInteraction(Boolean hasAiInteraction) {
        return (root, query, cb) -> hasAiInteraction == null ? null : cb.equal(root.get("hasAiInteraction"), hasAiInteraction);
    }
}
