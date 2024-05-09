package com.daaeboul.taskmanagementsystem.service.privileges;

import com.daaeboul.taskmanagementsystem.exceptions.privileges.group.DuplicateGroupNameException;
import com.daaeboul.taskmanagementsystem.model.privileges.Group;
import com.daaeboul.taskmanagementsystem.repository.privileges.GroupRepository;
import com.daaeboul.taskmanagementsystem.service.privileges.GroupService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class GroupServiceTest {

    @Autowired
    private GroupService groupService;

    @MockBean
    private GroupRepository groupRepository;

    @Test
    void shouldCreateGroup() {
        Group newGroup = new Group();
        newGroup.setGroupName("Test Group");
        when(groupRepository.save(newGroup)).thenReturn(newGroup);

        Group createdGroup = groupService.createGroup(newGroup);

        Assertions.assertNotNull(createdGroup);
        assertEquals("Test Group", createdGroup.getGroupName());
        Mockito.verify(groupRepository).save(newGroup);
    }

    @Test
    void shouldThrowExceptionWhenGroupNameIsDuplicate() {
        Group duplicateNameGroup = new Group();
        duplicateNameGroup.setGroupName("Existing Group");
        when(groupRepository.existsByGroupName(duplicateNameGroup.getGroupName())).thenReturn(true);

        assertThrows(DuplicateGroupNameException.class, () -> groupService.createGroup(duplicateNameGroup));
    }


    @Test
    void shouldFindGroupById() {
        Group expectedGroup = new Group();
        Long id = expectedGroup.getId();
        when(groupRepository.findById(id)).thenReturn(Optional.of(expectedGroup));

        Optional<Group> foundGroup = groupService.findGroupById(id);

        assertTrue(foundGroup.isPresent());
        assertEquals(expectedGroup, foundGroup.get());
    }

    @Test
    void shouldReturnEmptyOptionalWhenGroupNotFoundById() {
        Long nonExistentId = 999L;
        when(groupRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        Optional<Group> foundGroup = groupService.findGroupById(nonExistentId);

        assertTrue(foundGroup.isEmpty());
    }

    @Test
    void shouldFindAllGroups() {
        List<Group> expectedGroups = Arrays.asList(
                new Group("Administrators"),
                new Group("Managers"),
                new Group("Standard Users")
        );
        when(groupRepository.findAll()).thenReturn(expectedGroups);

        List<Group> foundGroups = groupService.findAllGroups();

        assertEquals(foundGroups, expectedGroups);
    }
}
