package com.controller;

import com.dto.UserDTO;
import com.enums.UserRole;
import com.service.UserService;
import com.utils.R;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * UserController测试类
 * 测试用户信息查询和角色更新接口
 */
public class UserControllerTest {
    
    @Mock
    private UserService userService;
    
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
        params.put("role", "PLATFORM_ADMIN");
        
        // 模拟服务层返回
        when(userService.isValidRole("PLATFORM_ADMIN")).thenReturn(true);
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
}
