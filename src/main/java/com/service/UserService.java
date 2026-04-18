package com.service;

import com.dto.UserDTO;
import com.enums.UserRole;

/**
 * 用户服务接口
 * 提供用户信息查询和角色管理功能
 * 
 * @author 
 * @email 
 * @date 2025-01-02
 */
public interface UserService {
    
    /**
     * 获取用户详细信息（包含角色）
     * 
     * @param userId 用户ID
     * @return 用户信息DTO，如果用户不存在则返回null
     */
    UserDTO getUserInfo(Long userId);
    
    /**
     * 更新用户角色
     * 
     * @param userId 用户ID
     * @param role 新的用户角色
     * @return 更新成功返回true，失败返回false
     */
    boolean updateUserRole(Long userId, UserRole role);
    
    /**
     * 验证角色是否有效
     * 
     * @param role 角色名称字符串
     * @return 如果角色有效返回true，否则返回false
     */
    boolean isValidRole(String role);
}
