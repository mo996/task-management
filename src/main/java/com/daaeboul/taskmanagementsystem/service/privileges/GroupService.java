package com.daaeboul.taskmanagementsystem.service.privileges;

import com.daaeboul.taskmanagementsystem.exceptions.privileges.group.DuplicateGroupNameException;
import com.daaeboul.taskmanagementsystem.exceptions.privileges.group.GroupNotFoundException;
import com.daaeboul.taskmanagementsystem.exceptions.user.user.UserNotFoundException;
import com.daaeboul.taskmanagementsystem.model.privileges.Group;
import com.daaeboul.taskmanagementsystem.model.user.User;
import com.daaeboul.taskmanagementsystem.repository.privileges.GroupRepository;
import com.daaeboul.taskmanagementsystem.repository.user.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GroupService {

    //@PersistenceContext
    //private EntityManager entityManager;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new Group entity. Performs validation on the group name before saving the entity to the database.
     *
     * @param group The Group object containing the data for the new group.
     * @return The newly created and saved Group object.
     * @throws RuntimeException (Or a more specific exception) if validation of the group name fails.
     */
    @Transactional
    public Group createGroup(Group group) {
        validateGroupName(group.getGroupName());
        return groupRepository.save(group);
    }

    /**
     * Retrieves a Group from the database by its ID.
     *
     * @param groupId The ID of the Group to retrieve.
     * @return An Optional containing the Group if found, otherwise an empty Optional.
     */
    public Optional<Group> findGroupById(Long groupId) {
        return groupRepository.findById(groupId);
    }

    /**
     * Retrieves a Group from the database by its name.
     *
     * @param groupName The name of the Group to retrieve.
     * @return An Optional containing the Group if found, otherwise an empty Optional.
     */
    public Optional<Group> findGroupByName(String groupName) {
        return groupRepository.findByGroupName(groupName);
    }

    /**
     * Retrieves a list of all Groups from the database.
     *
     * @return A List of Group entities representing all existing groups.
     */
    public List<Group> findAllGroups() {
        return groupRepository.findAll();
    }

    /**
     * Updates an existing Group with new data.
     *
     * @param updatedGroup The Group object containing the updated information.
     * @return The updated and saved Group object.
     * @throws GroupNotFoundException if a Group with the provided ID doesn't exist.
     * @throws RuntimeException (Or a more specific exception) if validation of the group name fails.
     */
    @Transactional
    public Group updateGroup(Group updatedGroup) {
        Group existingGroup = groupRepository.findById(updatedGroup.getId())
                .orElseThrow(() -> new GroupNotFoundException(updatedGroup.getId()));

        existingGroup.setGroupName(updatedGroup.getGroupName());
        existingGroup.setUsers(updatedGroup.getUsers());
        existingGroup.setRole(updatedGroup.getRole());
        existingGroup.setDescription(updatedGroup.getDescription());

        validateGroupName(existingGroup.getGroupName());

        return groupRepository.save(existingGroup);
    }

    /**
     * Deletes a Group from the database by its ID.
     *
     * @param groupId The ID of the Group to delete.
     * @throws GroupNotFoundException if a Group with the provided ID doesn't exist.
     */
    @Transactional
    public void deleteGroup(Long groupId) {
        groupRepository.deleteById(groupId);
    }

    /**
     * Adds a list of Users to an existing Group, establishing a many-to-many relationship between them.
     *
     * @param groupId  The ID of the Group to add users to.
     * @param userIds  A List of IDs representing the Users to associate with the Group.
     * @throws GroupNotFoundException if a Group with the provided ID doesn't exist.
     * @throws UserNotFoundException if one or more Users with the provided IDs are not found.
     */
    @Transactional
    public void addUsersToGroup(Long groupId, List<Long> userIds) {
        //Fetch Entities
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        List<User> users = userRepository.findAllById(userIds);
        if (users.size() != userIds.size()) {
            throw new UserNotFoundException("One or more users not found.");
        }

        //Establish the Relationship
        group.getUsers().addAll(users);
        users.forEach(user -> user.getGroups().add(group));

        //Persist
        //Save both the group and users to ensure updates are reflected in the join table
        userRepository.saveAll(users);
        groupRepository.save(group);
    }

    /**
     * Removes a list of Users from an existing Group, disassociating them within a many-to-many relationship.
     *
     * @param groupId  The ID of the Group to remove users from.
     * @param userIds  A List of IDs representing the Users to remove from the Group.
     * @throws GroupNotFoundException if a Group with the provided ID doesn't exist.
     * @throws UserNotFoundException if one or more Users with the provided IDs are not found.
     */
    @Transactional
    public void removeUsersFromGroup(Long groupId, List<Long> userIds) {
        // 1. Fetch Entities
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        List<User> users = userRepository.findAllById(userIds);
        if (users.size() != userIds.size()) {
            throw new UserNotFoundException("One or more users not found.");
        }

        // 2. Remove the Relationship (Both Sides)
        users.forEach(group.getUsers()::remove); // Remove from Group side
        users.forEach(user -> user.getGroups().remove(group)); // Remove from User side

        // 3. Persist
        // Explicitly delete from the join table
//        String deleteQuery = "DELETE FROM group_user WHERE group_id = :groupId AND user_id IN (:userIds)";
//        Query query = entityManager.createQuery(deleteQuery); // Assuming you have an entityManager injected
//        query.setParameter("groupId", groupId);
//        query.setParameter("userIds", userIds);
//        query.executeUpdate();

        // 3. Persist
        userRepository.saveAll(users); // Saving users will update the join table
        groupRepository.save(group);
    }

    /**
     * Helper method used for validating the uniqueness of a Group name.
     *
     * @param groupName The Group name to check for uniqueness.
     * @throws DuplicateGroupNameException if a Group with the given name already exists.
     */
    private void validateGroupName(String groupName) {
        if (groupRepository.existsByGroupName(groupName)) {
            throw new DuplicateGroupNameException(groupName);
        }
    }
}
