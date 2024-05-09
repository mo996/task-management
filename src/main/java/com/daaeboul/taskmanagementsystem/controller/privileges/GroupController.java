package com.daaeboul.taskmanagementsystem.controller.privileges;

import com.daaeboul.taskmanagementsystem.exceptions.privileges.group.DuplicateGroupNameException;
import com.daaeboul.taskmanagementsystem.exceptions.privileges.group.GroupNotFoundException;
import com.daaeboul.taskmanagementsystem.model.privileges.Group;
import com.daaeboul.taskmanagementsystem.service.privileges.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/groups")
public class GroupController {

    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    /**
     * Creates a new group.
     *
     * @param group The group to create.
     * @return The created group with HTTP status 201 (Created).
     */
    @PostMapping
    public ResponseEntity<Group> createGroup(@RequestBody Group group) {
        Group createdGroup = groupService.createGroup(group);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGroup);
    }

    /**
     * Retrieves a group by its ID.
     *
     * @param groupId The ID of the group to retrieve.
     * @return The group with HTTP status 200 (OK) if found, otherwise 404 (Not Found).
     */
    @GetMapping("/{groupId}")
    public ResponseEntity<Group> getGroupById(@PathVariable Long groupId) {
        Optional<Group> group = groupService.findGroupById(groupId);
        return group.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Retrieves a group by its name.
     *
     * @param groupName The name of the group to retrieve.
     * @return The group with HTTP status 200 (OK) if found, otherwise 404 (Not Found).
     */
    @GetMapping("/name/{groupName}")
    public ResponseEntity<Group> getGroupByName(@PathVariable String groupName) {
        Optional<Group> group = groupService.findGroupByName(groupName);
        return group.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Retrieves all groups.
     *
     * @return A list of all groups with HTTP status 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<Group>> getAllGroups() {
        List<Group> groups = groupService.findAllGroups();
        return ResponseEntity.ok(groups);
    }

    /**
     * Updates a group with the given ID.
     *
     * @param groupId  The ID of the group to update.
     * @param group The updated group data.
     * @return The updated group with HTTP status 200 (OK) if found, otherwise 404 (Not Found).
     */
    @PutMapping("/{groupId}")
    public ResponseEntity<Group> updateGroup(@PathVariable Long groupId, @RequestBody Group group) {
        // 1. Fetch the existing group (to get its ID)
        Group existingGroup = groupService.findGroupById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        // 2. Check for duplicate group name before updating
        if (!existingGroup.getGroupName().equalsIgnoreCase(group.getGroupName()) &&
                groupService.findGroupByName(group.getGroupName()).isPresent()) {
            throw new DuplicateGroupNameException(group.getGroupName());
        }

        // 3. Update the fetched group with data from the request body
        existingGroup.setGroupName(group.getGroupName());
        existingGroup.setDescription(group.getDescription());
        existingGroup.setRole(group.getRole());
        existingGroup.setUsers(group.getUsers());

        // 4. Save and return the updated group
        Group updatedGroup = groupService.updateGroup(existingGroup);
        return ResponseEntity.ok(updatedGroup);
    }

    /**
     * Deletes a group by its ID.
     *
     * @param groupId The ID of the group to delete.
     * @return HTTP status 204 (No Content) on successful deletion.
     */
    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long groupId) {
        groupService.deleteGroup(groupId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Adds users to a group.
     *
     * @param groupId The ID of the group.
     * @param userIds The IDs of the users to add.
     * @return HTTP status 204 (No Content) on successful update.
     */
    @PostMapping("/{groupId}/users")
    public ResponseEntity<Void> addUsersToGroup(@PathVariable Long groupId, @RequestBody List<Long> userIds) {
        groupService.addUsersToGroup(groupId, userIds);
        return ResponseEntity.noContent().build();
    }

    /**
     * Removes users from a group.
     *
     * @param groupId The ID of the group.
     * @param userIds The IDs of the users to remove.
     * @return HTTP status 204 (No Content) on successful update.
     */
    @DeleteMapping("/{groupId}/users")
    public ResponseEntity<Void> removeUsersFromGroup(@PathVariable Long groupId, @RequestBody List<Long> userIds) {
        groupService.removeUsersFromGroup(groupId, userIds);
        return ResponseEntity.noContent().build();
    }

}