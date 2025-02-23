package ai.lingualeap.lingualeap.dao.entity;

import ai.lingualeap.lingualeap.dao.entity.base.BaseEntity;
import ai.lingualeap.lingualeap.model.enums.LessonLevel;
import ai.lingualeap.lingualeap.model.enums.LessonStatus;
import ai.lingualeap.lingualeap.model.enums.LessonType;
import ai.lingualeap.lingualeap.model.vo.LearningObjective;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "lessons")
public class Lesson extends BaseEntity {
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LessonType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LessonLevel level;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LessonStatus status = LessonStatus.DRAFT;

    @Column(nullable = false)
    private Integer sequence;

    @Column(name = "min_required_score")
    private Integer minRequiredScore;

    @Column(name = "recommended_duration")
    private Integer recommendedDuration;

    @Column(name = "theory_content", columnDefinition = "TEXT")
    private String theoryContent;

    @Column(name = "has_ai_interaction")
    private Boolean hasAiInteraction = false;

    @Column(name = "ai_prompt_template", columnDefinition = "TEXT")
    private String aiPromptTemplate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL)
    private List<Exercise> exercises;

    @ManyToMany
    @JoinTable(
            name = "lesson_prerequisites",
            joinColumns = @JoinColumn(name = "lesson_id"),
            inverseJoinColumns = @JoinColumn(name = "prerequisite_id")
    )

    private Set<Lesson> prerequisites;

    @ElementCollection
    @CollectionTable(
            name = "lesson_objectives",
            joinColumns = @JoinColumn(name = "lesson_id")
    )

    private Set<LearningObjective> learningObjectives;

    @ManyToMany
    @JoinTable(
            name = "lesson_tags",
            joinColumns = @JoinColumn(name = "lesson_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();
}
