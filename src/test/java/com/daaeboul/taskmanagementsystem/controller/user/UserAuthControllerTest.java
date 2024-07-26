package com.daaeboul.taskmanagementsystem.controller.user;

import com.daaeboul.taskmanagementsystem.model.user.User;
import com.daaeboul.taskmanagementsystem.model.user.UserAuth;
import com.daaeboul.taskmanagementsystem.service.user.UserAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserAuthController.class)
public class UserAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserAuthService userAuthService;

    private UserAuth mockUserAuth;

    @BeforeEach
    void setUp() {
        User user = new User("testUser");
        ReflectionTestUtils.setField(user, "id", 1L);
        mockUserAuth = new UserAuth();
        mockUserAuth.setUser(user);
        mockUserAuth.setPasswordHash("hashedPassword");
        mockUserAuth.setAuthToken("testToken");
        mockUserAuth.setLastLoginAt(LocalDateTime.now());
        mockUserAuth.setFailedLoginAttempts(0);
        mockUserAuth.setLocked(false);
        ReflectionTestUtils.setField(mockUserAuth, "id", 1L);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testCreateUserAuth() throws Exception {
        Mockito.when(userAuthService.createUserAuth(any(UserAuth.class))).thenReturn(mockUserAuth);

        mockMvc.perform(post("/api/v1/user-auths")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"user\":{\"id\":1},\"passwordHash\":\"hashedPassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.passwordHash").value("hashedPassword"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindUserAuthById() throws Exception {
        Mockito.when(userAuthService.findUserAuthById(anyLong())).thenReturn(Optional.of(mockUserAuth));

        mockMvc.perform(get("/api/v1/user-auths/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.passwordHash").value("hashedPassword"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindUserAuthByAuthToken() throws Exception {
        Mockito.when(userAuthService.findUserAuthByAuthToken(anyString())).thenReturn(Optional.of(mockUserAuth));

        mockMvc.perform(get("/api/v1/user-auths/auth-token").param("authToken", "testToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.passwordHash").value("hashedPassword"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdateUserAuth() throws Exception {
        Mockito.when(userAuthService.findUserAuthById(anyLong())).thenReturn(Optional.of(mockUserAuth));

        mockMvc.perform(put("/api/v1/user-auths/1")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"passwordHash\":\"updatedPassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passwordHash").value("updatedPassword"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeleteUserAuth() throws Exception {
        Mockito.doNothing().when(userAuthService).deleteUserAuth(anyLong());

        mockMvc.perform(delete("/api/v1/user-auths/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testHardDeleteUserAuth() throws Exception {
        Mockito.doNothing().when(userAuthService).hardDeleteUserAuth(anyLong());

        mockMvc.perform(delete("/api/v1/user-auths/hard-delete/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindUserAuthByUserId() throws Exception {
        Mockito.when(userAuthService.findUserAuthByUserId(anyLong())).thenReturn(Optional.of(mockUserAuth));

        mockMvc.perform(get("/api/v1/user-auths/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.passwordHash").value("hashedPassword"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindLockedAccounts() throws Exception {
        Mockito.when(userAuthService.findLockedAccounts()).thenReturn(Collections.singletonList(mockUserAuth));

        mockMvc.perform(get("/api/v1/user-auths/locked-accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].passwordHash").value("hashedPassword"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdateAuthTokenByUserId() throws Exception {
        Mockito.doNothing().when(userAuthService).updateAuthTokenByUserId(anyLong(), anyString());

        mockMvc.perform(put("/api/v1/user-auths/update-auth-token/1")
                        .with(csrf())
                        .param("authToken", "newToken"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdatePassword() throws Exception {
        Mockito.doNothing().when(userAuthService).updatePassword(anyLong(), anyString());

        mockMvc.perform(put("/api/v1/user-auths/update-password/1")
                        .with(csrf())
                        .param("newPasswordHash", "newHashedPassword"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testLockUserAccount() throws Exception {
        Mockito.doNothing().when(userAuthService).lockUserAccount(anyLong());

        mockMvc.perform(put("/api/v1/user-auths/lock-account/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUnlockUserAccount() throws Exception {
        Mockito.doNothing().when(userAuthService).unlockUserAccount(anyLong());

        mockMvc.perform(put("/api/v1/user-auths/unlock-account/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdateLastLoginTime() throws Exception {
        Mockito.doNothing().when(userAuthService).updateLastLoginTime(anyLong());

        mockMvc.perform(put("/api/v1/user-auths/update-last-login/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testIncrementFailedLoginAttempts() throws Exception {
        Mockito.doNothing().when(userAuthService).incrementFailedLoginAttempts(anyLong());

        mockMvc.perform(put("/api/v1/user-auths/increment-failed-login-attempts/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testResetFailedLoginAttempts() throws Exception {
        Mockito.doNothing().when(userAuthService).resetFailedLoginAttempts(anyLong());

        mockMvc.perform(put("/api/v1/user-auths/reset-failed-login-attempts/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
