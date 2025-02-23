package ai.lingualeap.lingualeap.dao.entity;

import ai.lingualeap.lingualeap.dao.entity.base.BaseEntity;
import ai.lingualeap.lingualeap.model.enums.CourseLevel;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "courses")
public class Course extends BaseEntity {
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String targetLanguage;

    @Column(nullable = false)
    private String sourceLanguage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseLevel level;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<Module> modules;
}

