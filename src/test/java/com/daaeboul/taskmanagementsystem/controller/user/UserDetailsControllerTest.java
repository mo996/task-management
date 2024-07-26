package com.daaeboul.taskmanagementsystem.controller.user;

import com.daaeboul.taskmanagementsystem.model.user.User;
import com.daaeboul.taskmanagementsystem.model.user.UserDetails;
import com.daaeboul.taskmanagementsystem.service.user.UserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserDetailsController.class)
public class UserDetailsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDetailsService userDetailsService;

    private UserDetails mockUserDetails;
    private User mockUser;

    @BeforeEach
    public void setup() {
        mockUser = new User("testUser");
        ReflectionTestUtils.setField(mockUser, "id", 1L);
        mockUserDetails = new UserDetails("John", "Doe", "john.doe@example.com", mockUser);
        ReflectionTestUtils.setField(mockUserDetails, "id", 1L);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testCreateUserDetails() throws Exception {
        Mockito.when(userDetailsService.createUserDetails(any(UserDetails.class))).thenReturn(mockUserDetails);

        mockMvc.perform(post("/api/v1/user-details")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"john.doe@example.com\", \"firstName\": \"John\", \"lastName\": \"Doe\", \"user\": {\"id\": 1}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindUserDetailsById() throws Exception {
        Mockito.when(userDetailsService.findUserDetailsById(anyLong())).thenReturn(Optional.of(mockUserDetails));

        mockMvc.perform(get("/api/v1/user-details/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindUserDetailsById_NotFound() throws Exception {
        Mockito.when(userDetailsService.findUserDetailsById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/user-details/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindUserDetailsByEmailIgnoreCase() throws Exception {
        Mockito.when(userDetailsService.findUserDetailsByEmailIgnoreCase(anyString())).thenReturn(Optional.of(mockUserDetails));

        mockMvc.perform(get("/api/v1/user-details/email")
                        .param("email", "john.doe@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindUserDetailsByFirstNameContainingIgnoreCase() throws Exception {
        List<UserDetails> userDetailsList = Arrays.asList(mockUserDetails);
        Mockito.when(userDetailsService.findUserDetailsByFirstNameContainingIgnoreCase(anyString())).thenReturn(userDetailsList);

        mockMvc.perform(get("/api/v1/user-details/search/first-name")
                        .param("firstNamePart", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindUserDetailsByLastNameContainingIgnoreCase() throws Exception {
        List<UserDetails> userDetailsList = Arrays.asList(mockUserDetails);
        Mockito.when(userDetailsService.findUserDetailsByLastNameContainingIgnoreCase(anyString())).thenReturn(userDetailsList);

        mockMvc.perform(get("/api/v1/user-details/search/last-name")
                        .param("lastNamePart", "Doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindUserDetailsByFirstNameIgnoreCaseAndLastNameIgnoreCase() throws Exception {
        Mockito.when(userDetailsService.findUserDetailsByFirstNameIgnoreCaseAndLastNameIgnoreCase(anyString(), anyString())).thenReturn(Optional.of(mockUserDetails));

        mockMvc.perform(get("/api/v1/user-details/search/full-name")
                        .param("firstName", "John")
                        .param("lastName", "Doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdateUserDetails() throws Exception {
        UserDetails updatedUserDetails = new UserDetails("Jane", "Doe", "jane.doe@example.com", mockUser);
        ReflectionTestUtils.setField(updatedUserDetails, "id", 1L);

        Mockito.when(userDetailsService.findUserDetailsById(anyLong())).thenReturn(Optional.of(mockUserDetails));

        // Since updateUserDetails does not return a value, we should not expect a return value from it.
        Mockito.doNothing().when(userDetailsService).updateUserDetails(any(UserDetails.class));

        mockMvc.perform(put("/api/v1/user-details/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"jane.doe@example.com\", \"firstName\": \"Jane\", \"lastName\": \"Doe\", \"phoneNumber\": \"123456789\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("jane.doe@example.com"))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }


    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeleteUserDetails() throws Exception {
        Mockito.doNothing().when(userDetailsService).deleteUserDetails(anyLong());

        mockMvc.perform(delete("/api/v1/user-details/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testHardDeleteUserDetails() throws Exception {
        Mockito.doNothing().when(userDetailsService).hardDeleteUserDetails(anyLong());

        mockMvc.perform(delete("/api/v1/user-details/hard-delete/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}
