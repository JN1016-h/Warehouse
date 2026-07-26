package com.controller;

import com.entity.UsersEntity;
import com.service.TokenService;
import com.service.UsersService;
import com.utils.EncryptUtil;
import com.utils.R;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class UsersControllerTest {

    @Mock
    private UsersService userService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private UsersController controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoginSuccess() {
        UsersEntity user = new UsersEntity();
        user.setId(1L);
        user.setUsername("admin");
        user.setPassword(EncryptUtil.md5("123456"));
        user.setRole("管理员");
        when(userService.selectOne(any())).thenReturn(user);
        when(tokenService.generateToken(anyLong(), anyString(), anyString(), anyString())).thenReturn("token");

        R result = controller.login("admin", "123456", null, null, ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
        assertNotNull(result.get("token"));
    }

    @Test
    public void testLoginBlankCredentials() {
        R result = controller.login("", "", null, null, ControllerTestSupport.mockAdminRequest());
        assertEquals(500, result.get("code"));
    }

    @Test
    public void testRegister() {
        when(userService.selectOne(any())).thenReturn(null);
        R result = controller.register(new UsersEntity());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testRegisterDuplicate() {
        when(userService.selectOne(any())).thenReturn(new UsersEntity());
        R result = controller.register(new UsersEntity());
        assertEquals(500, result.get("code"));
    }

    @Test
    public void testLogout() {
        R result = controller.logout(ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testResetPass() {
        UsersEntity user = new UsersEntity();
        user.setId(1L);
        when(userService.selectOne(any())).thenReturn(user);
        R result = controller.resetPass("admin", ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testResetPassNotFound() {
        when(userService.selectOne(any())).thenReturn(null);
        R result = controller.resetPass("missing", ControllerTestSupport.mockAdminRequest());
        assertEquals(500, result.get("code"));
    }

    @Test
    public void testPage() {
        when(userService.queryPage(any(), any())).thenReturn(ControllerTestSupport.emptyPage());
        R result = controller.page(ControllerTestSupport.pageParams(), new UsersEntity());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testList() {
        when(userService.selectListView(any())).thenReturn(Collections.emptyList());
        R result = controller.list(new UsersEntity());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testInfo() {
        when(userService.selectById("1")).thenReturn(new UsersEntity());
        R result = controller.info("1");
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testSession() {
        when(userService.selectById(1L)).thenReturn(new UsersEntity());
        R result = controller.getCurrUser(ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testSave() {
        when(userService.selectOne(any())).thenReturn(null);
        R result = controller.save(new UsersEntity());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testUpdate() {
        UsersEntity user = new UsersEntity();
        user.setId(1L);
        user.setUsername("admin");
        when(userService.selectOne(any())).thenReturn(null);
        R result = controller.update(user);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testDelete() {
        R result = controller.delete(new Long[]{1L});
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testLoginWrongPassword() {
        UsersEntity user = new UsersEntity();
        user.setPassword(EncryptUtil.md5("correct"));
        when(userService.selectOne(any())).thenReturn(user);

        R result = controller.login("admin", "wrong", null, null, ControllerTestSupport.mockAdminRequest());
        assertEquals(500, result.get("code"));
    }

    @Test
    public void testLoginUserNotFound() {
        when(userService.selectOne(any())).thenReturn(null);
        R result = controller.login("ghost", "pass", null, null, ControllerTestSupport.mockAdminRequest());
        assertEquals(500, result.get("code"));
    }

    @Test
    public void testSaveDuplicateUsername() {
        UsersEntity entity = new UsersEntity();
        entity.setUsername("dup");
        when(userService.selectOne(any())).thenReturn(new UsersEntity());
        R result = controller.save(entity);
        assertEquals(500, result.get("code"));
    }

    @Test
    public void testUpdateDuplicateUsername() {
        UsersEntity existing = new UsersEntity();
        existing.setId(2L);
        existing.setUsername("taken");
        UsersEntity user = new UsersEntity();
        user.setId(1L);
        user.setUsername("taken");
        when(userService.selectOne(any())).thenReturn(existing);
        R result = controller.update(user);
        assertEquals(500, result.get("code"));
    }

    @Test
    public void testLoginPlaintextPasswordMigration() {
        UsersEntity user = new UsersEntity();
        user.setId(1L);
        user.setUsername("legacy");
        user.setPassword("plainpass");
        user.setRole("管理员");
        when(userService.selectOne(any())).thenReturn(user);
        when(tokenService.generateToken(anyLong(), anyString(), anyString(), anyString())).thenReturn("token");

        R result = controller.login("legacy", "plainpass", null, null, ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testRegisterWithPassword() {
        UsersEntity user = new UsersEntity();
        user.setUsername("newadmin");
        user.setPassword("secret");
        when(userService.selectOne(any())).thenReturn(null);

        R result = controller.register(user);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testRegisterWithoutPassword() {
        UsersEntity user = new UsersEntity();
        user.setUsername("nopass");
        when(userService.selectOne(any())).thenReturn(null);

        R result = controller.register(user);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testLoginViaBody() {
        UsersEntity user = new UsersEntity();
        user.setId(1L);
        user.setUsername("admin2");
        user.setPassword(EncryptUtil.md5("123456"));
        user.setRole("管理员");
        when(userService.selectOne(any())).thenReturn(user);
        when(tokenService.generateToken(anyLong(), anyString(), anyString(), anyString())).thenReturn("token");

        com.dto.LoginRequest body = new com.dto.LoginRequest();
        body.setUsername("admin2");
        body.setPassword("123456");

        R result = controller.login(null, null, null, body, ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testLoginBlankUsernameOnly() {
        R result = controller.login("", "pass", null, null, ControllerTestSupport.mockAdminRequest());
        assertEquals(500, result.get("code"));
    }

    @Test
    public void testLoginBlankPasswordOnly() {
        R result = controller.login("admin", "", null, null, ControllerTestSupport.mockAdminRequest());
        assertEquals(500, result.get("code"));
    }

    @Test
    public void testLoginWrongPasswordAfterPlaintextMigration() {
        UsersEntity user = new UsersEntity();
        user.setUsername("admin");
        user.setPassword(EncryptUtil.md5("correct"));
        when(userService.selectOne(any())).thenReturn(user);

        R result = controller.login("admin", "wrong", null, null, ControllerTestSupport.mockAdminRequest());
        assertEquals(500, result.get("code"));
    }

    @Test
    public void testLoginStoredPasswordNull() {
        UsersEntity user = new UsersEntity();
        user.setUsername("ghost");
        user.setPassword(null);
        when(userService.selectOne(any())).thenReturn(user);

        R result = controller.login("ghost", "pass", null, null, ControllerTestSupport.mockAdminRequest());
        assertEquals(500, result.get("code"));
    }

    @Test
    public void testSaveWithoutPassword() {
        UsersEntity entity = new UsersEntity();
        entity.setUsername("nopass");
        entity.setPassword(null);
        when(userService.selectOne(any())).thenReturn(null);

        R result = controller.save(entity);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testSaveWithEmptyPassword() {
        UsersEntity entity = new UsersEntity();
        entity.setUsername("empty");
        entity.setPassword("");
        when(userService.selectOne(any())).thenReturn(null);

        R result = controller.save(entity);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testUpdateSameUserNoConflict() {
        UsersEntity user = new UsersEntity();
        user.setId(1L);
        user.setUsername("self");
        UsersEntity existing = new UsersEntity();
        existing.setId(1L);
        existing.setUsername("self");
        when(userService.selectOne(any())).thenReturn(existing);

        R result = controller.update(user);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testUpdateWhenNoDuplicateFound() {
        UsersEntity user = new UsersEntity();
        user.setId(1L);
        user.setUsername("unique");
        when(userService.selectOne(any())).thenReturn(null);

        R result = controller.update(user);
        assertEquals(0, result.get("code"));
    }
}
