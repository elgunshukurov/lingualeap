package ai.lingualeap.lingualeap.service.mapper;

import ai.lingualeap.lingualeap.dao.entity.Tag;
import ai.lingualeap.lingualeap.model.response.TagResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.Set;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TagMapper {

    TagResponse toResponse(Tag tag);

    Set<TagResponse> toResponseSet(Set<Tag> tags);
}
