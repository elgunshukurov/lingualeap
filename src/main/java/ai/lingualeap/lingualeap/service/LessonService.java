package ai.lingualeap.lingualeap.service;

import ai.lingualeap.lingualeap.model.enums.LessonLevel;
import ai.lingualeap.lingualeap.model.enums.LessonStatus;
import ai.lingualeap.lingualeap.model.enums.LessonType;
import ai.lingualeap.lingualeap.model.request.LessonCreateRequest;
import ai.lingualeap.lingualeap.model.request.LessonUpdateRequest;
import ai.lingualeap.lingualeap.model.response.LessonResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface LessonService {
    LessonResponse createLesson(LessonCreateRequest request);
    LessonResponse updateLesson(Long id, LessonUpdateRequest request);
    LessonResponse getLessonById(Long id);
    Page<LessonResponse> searchLessons(LessonType type,
                                       LessonLevel level,
                                       LessonStatus status,
                                       Long moduleId,
                                       Pageable pageable);
    void deleteLesson(Long id);
    LessonResponse updateLessonStatus(Long id, LessonStatus status);
    void reorderLessons(Long moduleId, Map<Long, Integer> lessonSequences);
    List<LessonResponse> getLessonsByModuleId(Long moduleId);
    void addPrerequisite(Long lessonId, Long prerequisiteId);
    void removePrerequisite(Long lessonId, Long prerequisiteId);
    boolean isLessonCompletedByUser(Long lessonId, Long userId);
    List<LessonResponse> getAvailableLessonsForUser(Long userId, Long moduleId);
}
