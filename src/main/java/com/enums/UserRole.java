package com.enums;

/**
 * 用户角色枚举
 * 定义系统中的四种用户角色类型
 */
public enum UserRole {
    /**
     * 经销商
     */
    DEALER("经销商"),
    
    /**
     * 内部员工
     */
    INTERNAL_STAFF("内部员工"),
    
    /**
     * 平台管理员
     */
    PLATFORM_ADMIN("平台管理员"),
    
    /**
     * 仓库管理员
     */
    WAREHOUSE_ADMIN("仓库管理员");
    
    private final String displayName;
    
    UserRole(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * 获取角色的中文显示名称
     * @return 角色的中文显示名称
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 根据角色名称获取枚举值
     * @param name 角色名称
     * @return 对应的枚举值，如果不存在则返回null
     */
    public static UserRole fromName(String name) {
        if (name == null) {
            return null;
        }
        for (UserRole role : UserRole.values()) {
            if (role.name().equals(name)) {
                return role;
            }
        }
        return null;
    }
    
    /**
     * 验证角色名称是否有效
     * @param name 角色名称
     * @return 如果角色名称有效返回true，否则返回false
     */
    public static boolean isValid(String name) {
        return fromName(name) != null;
    }
}
