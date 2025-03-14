package ai.lingualeap.lingualeap.dao.repository;

import ai.lingualeap.lingualeap.dao.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long>, JpaSpecificationExecutor<Tag> {

    Optional<Tag> findByName(String name);

    boolean existsByName(String name);

    List<Tag> findByNameContainingIgnoreCase(String namePart);

    List<Tag> findByDescriptionContainingIgnoreCase(String descriptionPart);
}
