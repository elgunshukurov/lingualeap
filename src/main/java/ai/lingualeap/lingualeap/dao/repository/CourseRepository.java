package ai.lingualeap.lingualeap.dao.repository;

import ai.lingualeap.lingualeap.dao.entity.Course;
import ai.lingualeap.lingualeap.model.enums.CourseLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {
    List<Course> findByTargetLanguageAndSourceLanguage(String targetLanguage, String sourceLanguage);

    List<Course> findByLevel(CourseLevel level);
}
