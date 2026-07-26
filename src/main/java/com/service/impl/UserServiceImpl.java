package com.service.impl;

import com.dto.UserDTO;
import com.entity.YonghuEntity;
import com.enums.UserRole;
import com.service.UserService;
import com.service.YonghuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 * 实现用户信息查询和角色管理功能
 * 
 * @author 
 * @email 
 * @date 2025-01-02
 */
@Service("userService")
public class UserServiceImpl implements UserService {
    
    @Autowired
    private YonghuService yonghuService;
    
    /**
     * 获取用户详细信息（包含角色）
     * 查询用户信息并转换为UserDTO
     * 
     * @param userId 用户ID
     * @return 用户信息DTO，如果用户不存在则返回null
     */
    @Override
    public UserDTO getUserInfo(Long userId) {
        if (userId == null) {
            return null;
        }
        
        // 查询用户实体
        YonghuEntity yonghuEntity = yonghuService.selectById(userId);
        if (yonghuEntity == null) {
            return null;
        }
        
        // 转换为UserDTO
        return convertToUserDTO(yonghuEntity);
    }
    
    /**
     * 更新用户角色
     * 
     * @param userId 用户ID
     * @param role 新的用户角色
     * @return 更新成功返回true，失败返回false
     */
    @Override
    public boolean updateUserRole(Long userId, UserRole role) {
        if (userId == null || role == null) {
            return false;
        }
        
        // 查询用户是否存在
        YonghuEntity yonghuEntity = yonghuService.selectById(userId);
        if (yonghuEntity == null) {
            return false;
        }
        
        // 更新角色
        yonghuEntity.setUserRole(role.name());
        return yonghuService.updateById(yonghuEntity);
    }
    
    /**
     * 验证角色是否有效
     * 
     * @param role 角色名称字符串
     * @return 如果角色有效返回true，否则返回false
     */
    @Override
    public boolean isValidRole(String role) {
        return UserRole.isValid(role);
    }
    
    /**
     * 将YonghuEntity转换为UserDTO
     * 
     * @param entity 用户实体
     * @return 用户DTO
     */
    private UserDTO convertToUserDTO(YonghuEntity entity) {
        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getYonghuzhanghao());
        dto.setName(entity.getYonghuxingming());
        dto.setPhone(entity.getLianxifangshi());
        // 注意：YonghuEntity中没有email字段，设置为null
        dto.setEmail(null);
        
        // 设置角色信息
        String roleStr = entity.getUserRole();
        if (roleStr != null && !roleStr.isEmpty()) {
            UserRole role = UserRole.fromName(roleStr);
            if (role != null) {
                dto.setRole(role);
                dto.setRoleDisplayName(role.getDisplayName());
            }
        }
        
        // 设置创建时间
        dto.setCreateTime(entity.getAddtime());
        
        return dto;
    }
}
