package com.daaeboul.taskmanagementsystem.repository.privileges;

import com.daaeboul.taskmanagementsystem.model.privileges.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    /**
     * Retrieves a Group from the database based on its unique group name.
     *
     * @param groupName The name of the group to search for.
     * @return An Optional containing the Group if found, otherwise an empty Optional.
     */
    Optional<Group> findByGroupName(String groupName);

    /**
     * Checks if a Group with the specified group name exists in the database.
     *
     * @param groupName The name of the group to check for existence.
     * @return True if a Group with the given name exists, otherwise false.
     */
    boolean existsByGroupName(String groupName);

}
