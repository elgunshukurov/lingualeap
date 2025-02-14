package ai.lingualeap.lingualeap.service.mapper;


import ai.lingualeap.lingualeap.dao.entity.User;
import ai.lingualeap.lingualeap.model.request.UserCreateRequest;
import ai.lingualeap.lingualeap.model.request.UserUpdateRequest;
import ai.lingualeap.lingualeap.model.response.UserResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.HashSet;
import java.util.Set;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {HashSet.class, Set.class})
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", expression = "java(new HashSet<>(Set.of(UserRole.USER)))")
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(UserCreateRequest request);

    UserResponse toResponse(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UserUpdateRequest request, @MappingTarget User user);
}
