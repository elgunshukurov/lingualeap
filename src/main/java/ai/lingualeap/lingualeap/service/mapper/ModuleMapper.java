package ai.lingualeap.lingualeap.service.mapper;

import ai.lingualeap.lingualeap.dao.entity.Module;
import ai.lingualeap.lingualeap.model.request.ModuleCreateRequest;
import ai.lingualeap.lingualeap.model.response.ModuleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {CourseMapper.class})
public interface ModuleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "lessons", ignore = true)
    Module toEntity(ModuleCreateRequest request);

    ModuleResponse toResponse(Module module);

    List<ModuleResponse> toResponseList(List<Module> modules);
}
