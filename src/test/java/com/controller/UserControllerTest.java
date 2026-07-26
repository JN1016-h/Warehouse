package com.controller;

import com.dto.UserDTO;
import com.entity.YonghuEntity;
import com.entity.view.YonghuView;
import com.enums.UserRole;
import com.service.TokenService;
import com.service.UserService;
import com.service.YonghuService;
import com.utils.EncryptUtil;
import com.utils.R;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

/**
 * UserController测试类
 * 测试用户信息查询和角色更新接口
 */
public class UserControllerTest {
    
    @Mock
    private UserService userService;

    @Mock
    private YonghuService yonghuService;

    @Mock
    private TokenService tokenService;
    
    @InjectMocks
    private YonghuController yonghuController;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    public void testGetUserInfo_Success() {
        // 准备测试数据
        Long userId = 1L;
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userId);
        userDTO.setUsername("testuser");
        userDTO.setName("测试用户");
        userDTO.setRole(UserRole.DEALER);
        userDTO.setRoleDisplayName("经销商");
        
        // 模拟服务层返回
        when(userService.getUserInfo(userId)).thenReturn(userDTO);
        
        // 调用控制器方法
        R result = yonghuController.getUserInfo(userId);
        
        // 验证结果
        assertEquals(0, result.get("code"));
        assertNotNull(result.get("data"));
        UserDTO returnedDTO = (UserDTO) result.get("data");
        assertEquals(userId, returnedDTO.getId());
        assertEquals("testuser", returnedDTO.getUsername());
    }
    
    @Test
    public void testGetUserInfo_UserNotFound() {
        // 准备测试数据
        Long userId = 999L;
        
        // 模拟服务层返回null
        when(userService.getUserInfo(userId)).thenReturn(null);
        
        // 调用控制器方法
        R result = yonghuController.getUserInfo(userId);
        
        // 验证结果
        assertEquals(500, result.get("code"));
        assertEquals("用户不存在", result.get("msg"));
    }
    
    @Test
    public void testGetUserInfo_NullId() {
        // 调用控制器方法
        R result = yonghuController.getUserInfo(null);
        
        // 验证结果
        assertEquals(500, result.get("code"));
        assertEquals("用户ID不能为空", result.get("msg"));
    }
    
    @Test
    public void testUpdateUserRole_Success() {
        // 准备测试数据
        Map<String, Object> params = new HashMap<>();
        params.put("userId", "1");
        params.put("role", "INTERNAL_STAFF");
        
        // 模拟服务层返回
        when(userService.isValidRole("INTERNAL_STAFF")).thenReturn(true);
        when(userService.updateUserRole(eq(1L), any(UserRole.class))).thenReturn(true);
        
        // 调用控制器方法
        R result = yonghuController.updateUserRole(params);
        
        // 验证结果
        assertEquals(0, result.get("code"));
        assertEquals("角色更新成功", result.get("msg"));
    }
    
    @Test
    public void testUpdateUserRole_InvalidRole() {
        // 准备测试数据
        Map<String, Object> params = new HashMap<>();
        params.put("userId", "1");
        params.put("role", "INVALID_ROLE");
        
        // 模拟服务层返回
        when(userService.isValidRole("INVALID_ROLE")).thenReturn(false);
        
        // 调用控制器方法
        R result = yonghuController.updateUserRole(params);
        
        // 验证结果
        assertEquals(500, result.get("code"));
        assertEquals("无效的用户角色", result.get("msg"));
    }
    
    @Test
    public void testLoginSuccess() {
        YonghuEntity user = new YonghuEntity();
        user.setId(1L);
        user.setYonghuzhanghao("testuser");
        user.setMima(EncryptUtil.md5("pass123"));
        when(yonghuService.selectOne(any())).thenReturn(user);
        when(tokenService.generateToken(anyLong(), anyString(), anyString(), anyString())).thenReturn("token");

        R result = yonghuController.login("testuser", "pass123", null, null, ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
        assertNotNull(result.get("token"));
    }

    @Test
    public void testLoginBlankCredentials() {
        R result = yonghuController.login("", "", null, null, ControllerTestSupport.mockAdminRequest());
        assertEquals(500, result.get("code"));
    }

    @Test
    public void testRegisterSuccess() {
        YonghuEntity yonghu = new YonghuEntity();
        yonghu.setYonghuzhanghao("newuser");
        yonghu.setMima("pass123");
        when(yonghuService.selectOne(any())).thenReturn(null);

        R result = yonghuController.register(yonghu);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testRegisterDuplicate() {
        YonghuEntity yonghu = new YonghuEntity();
        yonghu.setYonghuzhanghao("exists");
        yonghu.setMima("pass123");
        when(yonghuService.selectOne(any())).thenReturn(new YonghuEntity());

        R result = yonghuController.register(yonghu);
        assertEquals(500, result.get("code"));
    }

    @Test
    public void testLogout() {
        R result = yonghuController.logout(ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testSession() {
        when(yonghuService.selectById(1L)).thenReturn(new YonghuEntity());
        R result = yonghuController.getCurrUser(ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testResetPass() {
        YonghuEntity user = new YonghuEntity();
        user.setId(1L);
        when(yonghuService.selectOne(any())).thenReturn(user);
        R result = yonghuController.resetPass("testuser", ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testPage() {
        when(yonghuService.queryPage(any(), any())).thenReturn(ControllerTestSupport.emptyPage());
        R result = yonghuController.page(ControllerTestSupport.pageParams(), new YonghuEntity(),
                ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testList() {
        when(yonghuService.queryPage(any(), any())).thenReturn(ControllerTestSupport.emptyPage());
        R result = yonghuController.list(ControllerTestSupport.pageParams(), new YonghuEntity(),
                ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testLists() {
        when(yonghuService.selectListView(any())).thenReturn(Collections.emptyList());
        R result = yonghuController.list(new YonghuEntity());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testQuery() {
        when(yonghuService.selectView(any())).thenReturn(new YonghuView());
        R result = yonghuController.query(new YonghuEntity());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testInfo() {
        when(yonghuService.selectById(1L)).thenReturn(new YonghuEntity());
        R result = yonghuController.info(1L);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testDetail() {
        when(yonghuService.selectById(1L)).thenReturn(new YonghuEntity());
        R result = yonghuController.detail(1L);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testSave() {
        YonghuEntity yonghu = new YonghuEntity();
        yonghu.setYonghuzhanghao("saveuser");
        yonghu.setMima("pass123");
        when(yonghuService.selectCount(any())).thenReturn(0);
        when(yonghuService.selectOne(any())).thenReturn(null);

        R result = yonghuController.save(yonghu, ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testAdd() {
        YonghuEntity yonghu = new YonghuEntity();
        yonghu.setYonghuzhanghao("adduser");
        yonghu.setMima("pass123");
        when(yonghuService.selectCount(any())).thenReturn(0);
        when(yonghuService.selectOne(any())).thenReturn(null);

        R result = yonghuController.add(yonghu, ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testUpdate() {
        YonghuEntity yonghu = new YonghuEntity();
        yonghu.setId(1L);
        yonghu.setYonghuzhanghao("updateuser");
        when(yonghuService.selectCount(any())).thenReturn(0);

        R result = yonghuController.update(yonghu, ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testDelete() {
        R result = yonghuController.delete(new Long[]{1L});
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testUpdateUserRole_UpdateFailed() {
        // 准备测试数据
        Map<String, Object> params = new HashMap<>();
        params.put("userId", "999");
        params.put("role", "DEALER");
        
        // 模拟服务层返回
        when(userService.isValidRole("DEALER")).thenReturn(true);
        when(userService.updateUserRole(eq(999L), any(UserRole.class))).thenReturn(false);
        
        // 调用控制器方法
        R result = yonghuController.updateUserRole(params);
        
        // 验证结果
        assertEquals(500, result.get("code"));
        assertEquals("角色更新失败", result.get("msg"));
    }

    @Test
    public void testLoginPlaintextPasswordMigration() {
        YonghuEntity user = new YonghuEntity();
        user.setId(1L);
        user.setYonghuzhanghao("legacy");
        user.setMima("plainpass");
        when(yonghuService.selectOne(any())).thenReturn(user);
        when(tokenService.generateToken(anyLong(), anyString(), anyString(), anyString())).thenReturn("token");

        R result = yonghuController.login("legacy", "plainpass", null, null, ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testLoginAccountNotFound() {
        when(yonghuService.selectOne(any())).thenReturn(null);
        R result = yonghuController.login("missing", "pass", null, null, ControllerTestSupport.mockAdminRequest());
        assertEquals(500, result.get("code"));
    }

    @Test
    public void testResetPassNotFound() {
        when(yonghuService.selectOne(any())).thenReturn(null);
        R result = yonghuController.resetPass("missing", ControllerTestSupport.mockAdminRequest());
        assertEquals(500, result.get("code"));
    }

    @Test
    public void testLoginWrongPasswordHashed() {
        YonghuEntity user = new YonghuEntity();
        user.setMima(EncryptUtil.md5("correct"));
        when(yonghuService.selectOne(any())).thenReturn(user);

        R result = yonghuController.login("user", "wrong", null, null, ControllerTestSupport.mockAdminRequest());
        assertEquals(500, result.get("code"));
    }

    @Test
    public void testLoginViaRequestBody() {
        YonghuEntity user = new YonghuEntity();
        user.setId(1L);
        user.setYonghuzhanghao("bodyuser");
        user.setMima(EncryptUtil.md5("pass123"));
        when(yonghuService.selectOne(any())).thenReturn(user);
        when(tokenService.generateToken(anyLong(), anyString(), anyString(), anyString())).thenReturn("token");

        com.dto.LoginRequest body = new com.dto.LoginRequest();
        body.setUsername("bodyuser");
        body.setPassword("pass123");

        R result = yonghuController.login(null, null, null, body, ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testRegisterNullEntity() {
        R result = yonghuController.register(null);
        assertEquals(500, result.get("code"));
    }

    @Test
    public void testRegisterBlankFields() {
        YonghuEntity yonghu = new YonghuEntity();
        yonghu.setYonghuzhanghao("  ");
        yonghu.setMima("");
        R result = yonghuController.register(yonghu);
        assertEquals(500, result.get("code"));
    }

    @Test
    public void testRegisterInsertFailure() {
        YonghuEntity yonghu = new YonghuEntity();
        yonghu.setYonghuzhanghao("failuser");
        yonghu.setMima("pass123");
        when(yonghuService.selectOne(any())).thenReturn(null);
        doThrow(new RuntimeException("db")).when(yonghuService).insert(any());

        R result = yonghuController.register(yonghu);
        assertEquals(500, result.get("code"));
    }

    @Test
    public void testRegisterTrimsName() {
        YonghuEntity yonghu = new YonghuEntity();
        yonghu.setYonghuzhanghao(" trimuser ");
        yonghu.setMima("pass123");
        yonghu.setYonghuxingming(" 张三 ");
        when(yonghuService.selectOne(any())).thenReturn(null);

        R result = yonghuController.register(yonghu);
        assertEquals(0, result.get("code"));
        assertEquals("trimuser", yonghu.getYonghuzhanghao());
        assertEquals("张三", yonghu.getYonghuxingming());
    }

    @Test
    public void testSaveDuplicateByCount() {
        YonghuEntity yonghu = new YonghuEntity();
        yonghu.setYonghuzhanghao("dup");
        when(yonghuService.selectCount(any())).thenReturn(1);

        R result = yonghuController.save(yonghu, ControllerTestSupport.mockAdminRequest());
        assertEquals(500, result.get("code"));
    }

    @Test
    public void testSaveDuplicateBySelectOne() {
        YonghuEntity yonghu = new YonghuEntity();
        yonghu.setYonghuzhanghao("dup2");
        when(yonghuService.selectCount(any())).thenReturn(0);
        when(yonghuService.selectOne(any())).thenReturn(new YonghuEntity());

        R result = yonghuController.save(yonghu, ControllerTestSupport.mockAdminRequest());
        assertEquals(500, result.get("code"));
    }

    @Test
    public void testSaveWithoutPassword() {
        YonghuEntity yonghu = new YonghuEntity();
        yonghu.setYonghuzhanghao("nopass");
        yonghu.setMima("  ");
        when(yonghuService.selectCount(any())).thenReturn(0);
        when(yonghuService.selectOne(any())).thenReturn(null);

        R result = yonghuController.save(yonghu, ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testAddDuplicate() {
        YonghuEntity yonghu = new YonghuEntity();
        yonghu.setYonghuzhanghao("adddup");
        when(yonghuService.selectCount(any())).thenReturn(1);

        R result = yonghuController.add(yonghu, ControllerTestSupport.mockAdminRequest());
        assertEquals(500, result.get("code"));
    }

    @Test
    public void testUpdateDuplicateUsername() {
        YonghuEntity yonghu = new YonghuEntity();
        yonghu.setId(1L);
        yonghu.setYonghuzhanghao("taken");
        when(yonghuService.selectCount(any())).thenReturn(1);

        R result = yonghuController.update(yonghu, ControllerTestSupport.mockAdminRequest());
        assertEquals(500, result.get("code"));
    }

    @Test
    public void testUpdateWithoutUsernameSkipsToken() {
        YonghuEntity yonghu = new YonghuEntity();
        yonghu.setId(1L);
        yonghu.setYonghuzhanghao(null);
        when(yonghuService.selectCount(any())).thenReturn(0);

        R result = yonghuController.update(yonghu, ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testUpdateUserRoleMissingParams() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", "1");
        params.put("role", "");
        R result = yonghuController.updateUserRole(params);
        assertEquals(500, result.get("code"));
    }

    @Test
    public void testLoginStoredPasswordNull() {
        YonghuEntity user = new YonghuEntity();
        user.setId(1L);
        user.setYonghuzhanghao("nullpass");
        user.setMima(null);
        when(yonghuService.selectOne(any())).thenReturn(user);

        R result = yonghuController.login("nullpass", "any", null, null, ControllerTestSupport.mockAdminRequest());
        assertEquals(500, result.get("code"));
    }

    @Test
    public void testRegisterWithoutDisplayName() {
        YonghuEntity yonghu = new YonghuEntity();
        yonghu.setYonghuzhanghao("noname");
        yonghu.setMima("pass123");
        yonghu.setYonghuxingming(null);
        when(yonghuService.selectOne(any())).thenReturn(null);

        R result = yonghuController.register(yonghu);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testAddDuplicateBySelectOne() {
        YonghuEntity yonghu = new YonghuEntity();
        yonghu.setYonghuzhanghao("adddup2");
        when(yonghuService.selectCount(any())).thenReturn(0);
        when(yonghuService.selectOne(any())).thenReturn(new YonghuEntity());

        R result = yonghuController.add(yonghu, ControllerTestSupport.mockAdminRequest());
        assertEquals(500, result.get("code"));
    }

    @Test
    public void testUpdateWithUsernameUpdatesToken() {
        YonghuEntity yonghu = new YonghuEntity();
        yonghu.setId(1L);
        yonghu.setYonghuzhanghao("renamed");
        when(yonghuService.selectCount(any())).thenReturn(0);

        R result = yonghuController.update(yonghu, ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }
}
