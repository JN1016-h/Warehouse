package com.enums;

/**
 * 用户角色枚举
 * DEALER / INTERNAL_STAFF / WAREHOUSE_ADMIN
 */
public enum UserRole {
    /**
     * 经销商
     */
    DEALER("经销商"),

    /**
     * 内部员工（含财务 AI 数据域）
     */
    INTERNAL_STAFF("内部员工"),

    /**
     * 仓库管理员
     */
    WAREHOUSE_ADMIN("仓库管理员");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * 根据角色名称获取枚举值。
     * 历史值 PLATFORM_ADMIN 映射为 INTERNAL_STAFF。
     */
    public static UserRole fromName(String name) {
        if (name == null) {
            return null;
        }
        if ("PLATFORM_ADMIN".equals(name)) {
            return INTERNAL_STAFF;
        }
        for (UserRole role : UserRole.values()) {
            if (role.name().equals(name)) {
                return role;
            }
        }
        return null;
    }

    /**
     * 校验是否可作为新角色赋值（不含已删除的 PLATFORM_ADMIN）
     */
    public static boolean isValid(String name) {
        if (name == null) {
            return false;
        }
        for (UserRole role : UserRole.values()) {
            if (role.name().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
