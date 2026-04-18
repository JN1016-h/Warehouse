package com.service;

import com.dto.UserDTO;
import com.entity.YonghuEntity;
import com.enums.UserRole;
import com.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UserService单元测试
 * 测试用户信息查询和角色管理功能
 */
public class UserServiceTest {
    
    @Mock
    private YonghuService yonghuService;
    
    @InjectMocks
    private UserServiceImpl userService;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    /**
     * 测试getUserInfo - 成功场景
     * 验证能够正确查询用户信息并转换为UserDTO
     */
    @Test
    public void testGetUserInfo_Success() {
        // 准备测试数据
        Long userId = 1L;
        YonghuEntity yonghuEntity = new YonghuEntity();
        yonghuEntity.setId(userId);
        yonghuEntity.setYonghuzhanghao("testuser");
        yonghuEntity.setYonghuxingming("测试用户");
        yonghuEntity.setLianxifangshi("13800138000");
        yonghuEntity.setUserRole("DEALER");
        yonghuEntity.setAddtime(new Date());
        
        // 模拟YonghuService行为
        when(yonghuService.selectById(userId)).thenReturn(yonghuEntity);
        
        // 执行测试
        UserDTO result = userService.getUserInfo(userId);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("测试用户", result.getName());
        assertEquals("13800138000", result.getPhone());
        assertEquals(UserRole.DEALER, result.getRole());
        assertEquals("经销商", result.getRoleDisplayName());
        assertNotNull(result.getCreateTime());
        
        // 验证方法调用
        verify(yonghuService, times(1)).selectById(userId);
    }
    
    /**
     * 测试getUserInfo - 用户不存在
     * 验证当用户不存在时返回null
     */
    @Test
    public void testGetUserInfo_UserNotFound() {
        // 准备测试数据
        Long userId = 999L;
        
        // 模拟YonghuService行为
        when(yonghuService.selectById(userId)).thenReturn(null);
        
        // 执行测试
        UserDTO result = userService.getUserInfo(userId);
        
        // 验证结果
        assertNull(result);
        
        // 验证方法调用
        verify(yonghuService, times(1)).selectById(userId);
    }
    
    /**
     * 测试getUserInfo - userId为null
     * 验证当userId为null时返回null
     */
    @Test
    public void testGetUserInfo_NullUserId() {
        // 执行测试
        UserDTO result = userService.getUserInfo(null);
        
        // 验证结果
        assertNull(result);
        
        // 验证方法未被调用
        verify(yonghuService, never()).selectById(any());
    }
    
    /**
     * 测试getUserInfo - 用户角色为null
     * 验证当用户角色为null时能正确处理
     */
    @Test
    public void testGetUserInfo_NullRole() {
        // 准备测试数据
        Long userId = 1L;
        YonghuEntity yonghuEntity = new YonghuEntity();
        yonghuEntity.setId(userId);
        yonghuEntity.setYonghuzhanghao("testuser");
        yonghuEntity.setYonghuxingming("测试用户");
        yonghuEntity.setLianxifangshi("13800138000");
        yonghuEntity.setUserRole(null);
        yonghuEntity.setAddtime(new Date());
        
        // 模拟YonghuService行为
        when(yonghuService.selectById(userId)).thenReturn(yonghuEntity);
        
        // 执行测试
        UserDTO result = userService.getUserInfo(userId);
        
        // 验证结果
        assertNotNull(result);
        assertNull(result.getRole());
        assertNull(result.getRoleDisplayName());
    }
    
    /**
     * 测试updateUserRole - 成功场景
     * 验证能够成功更新用户角色
     */
    @Test
    public void testUpdateUserRole_Success() {
        // 准备测试数据
        Long userId = 1L;
        UserRole newRole = UserRole.PLATFORM_ADMIN;
        YonghuEntity yonghuEntity = new YonghuEntity();
        yonghuEntity.setId(userId);
        yonghuEntity.setUserRole("DEALER");
        
        // 模拟YonghuService行为
        when(yonghuService.selectById(userId)).thenReturn(yonghuEntity);
        when(yonghuService.updateById(any(YonghuEntity.class))).thenReturn(true);
        
        // 执行测试
        boolean result = userService.updateUserRole(userId, newRole);
        
        // 验证结果
        assertTrue(result);
        assertEquals("PLATFORM_ADMIN", yonghuEntity.getUserRole());
        
        // 验证方法调用
        verify(yonghuService, times(1)).selectById(userId);
        verify(yonghuService, times(1)).updateById(yonghuEntity);
    }
    
    /**
     * 测试updateUserRole - 用户不存在
     * 验证当用户不存在时返回false
     */
    @Test
    public void testUpdateUserRole_UserNotFound() {
        // 准备测试数据
        Long userId = 999L;
        UserRole newRole = UserRole.PLATFORM_ADMIN;
        
        // 模拟YonghuService行为
        when(yonghuService.selectById(userId)).thenReturn(null);
        
        // 执行测试
        boolean result = userService.updateUserRole(userId, newRole);
        
        // 验证结果
        assertFalse(result);
        
        // 验证方法调用
        verify(yonghuService, times(1)).selectById(userId);
        verify(yonghuService, never()).updateById(any());
    }
    
    /**
     * 测试updateUserRole - userId为null
     * 验证当userId为null时返回false
     */
    @Test
    public void testUpdateUserRole_NullUserId() {
        // 执行测试
        boolean result = userService.updateUserRole(null, UserRole.DEALER);
        
        // 验证结果
        assertFalse(result);
        
        // 验证方法未被调用
        verify(yonghuService, never()).selectById(any());
        verify(yonghuService, never()).updateById(any());
    }
    
    /**
     * 测试updateUserRole - role为null
     * 验证当role为null时返回false
     */
    @Test
    public void testUpdateUserRole_NullRole() {
        // 执行测试
        boolean result = userService.updateUserRole(1L, null);
        
        // 验证结果
        assertFalse(result);
        
        // 验证方法未被调用
        verify(yonghuService, never()).selectById(any());
        verify(yonghuService, never()).updateById(any());
    }
    
    /**
     * 测试isValidRole - 有效角色
     * 验证所有有效角色都能通过验证
     */
    @Test
    public void testIsValidRole_ValidRoles() {
        assertTrue(userService.isValidRole("DEALER"));
        assertTrue(userService.isValidRole("INTERNAL_STAFF"));
        assertTrue(userService.isValidRole("PLATFORM_ADMIN"));
        assertTrue(userService.isValidRole("WAREHOUSE_ADMIN"));
    }
    
    /**
     * 测试isValidRole - 无效角色
     * 验证无效角色不能通过验证
     */
    @Test
    public void testIsValidRole_InvalidRoles() {
        assertFalse(userService.isValidRole("INVALID_ROLE"));
        assertFalse(userService.isValidRole("dealer"));
        assertFalse(userService.isValidRole(""));
        assertFalse(userService.isValidRole(null));
    }
    
    /**
     * 测试getUserInfo - 所有角色类型
     * 验证所有角色类型都能正确转换和显示
     */
    @Test
    public void testGetUserInfo_AllRoleTypes() {
        Long userId = 1L;
        
        // 测试DEALER角色
        testRoleConversion(userId, "DEALER", UserRole.DEALER, "经销商");
        
        // 测试INTERNAL_STAFF角色
        testRoleConversion(userId, "INTERNAL_STAFF", UserRole.INTERNAL_STAFF, "内部员工");
        
        // 测试PLATFORM_ADMIN角色
        testRoleConversion(userId, "PLATFORM_ADMIN", UserRole.PLATFORM_ADMIN, "平台管理员");
        
        // 测试WAREHOUSE_ADMIN角色
        testRoleConversion(userId, "WAREHOUSE_ADMIN", UserRole.WAREHOUSE_ADMIN, "仓库管理员");
    }
    
    /**
     * 辅助方法：测试角色转换
     */
    private void testRoleConversion(Long userId, String roleStr, UserRole expectedRole, String expectedDisplayName) {
        YonghuEntity yonghuEntity = new YonghuEntity();
        yonghuEntity.setId(userId);
        yonghuEntity.setYonghuzhanghao("testuser");
        yonghuEntity.setYonghuxingming("测试用户");
        yonghuEntity.setUserRole(roleStr);
        yonghuEntity.setAddtime(new Date());
        
        when(yonghuService.selectById(userId)).thenReturn(yonghuEntity);
        
        UserDTO result = userService.getUserInfo(userId);
        
        assertNotNull(result);
        assertEquals(expectedRole, result.getRole());
        assertEquals(expectedDisplayName, result.getRoleDisplayName());
    }
}
