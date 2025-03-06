package ai.lingualeap.lingualeap.service;

import ai.lingualeap.lingualeap.model.request.ModuleCreateRequest;
import ai.lingualeap.lingualeap.model.response.ModuleResponse;

import java.util.List;

public interface ModuleService {
    ModuleResponse createModule(ModuleCreateRequest request);

    ModuleResponse getModuleById(Long id);

    List<ModuleResponse> getModulesByCourseId(Long courseId);

    void deleteModule(Long id);
}
