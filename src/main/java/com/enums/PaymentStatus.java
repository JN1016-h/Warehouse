package com.enums;

/**
 * 付款状态枚举
 * 定义订单的付款状态类型
 */
public enum PaymentStatus {
    /**
     * 未付款
     */
    UNPAID("未付款"),
    
    /**
     * 已付款
     */
    PAID("已付款");
    
    private final String displayName;
    
    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * 获取付款状态的中文显示名称
     * @return 付款状态的中文显示名称
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 根据状态名称获取枚举值
     * @param name 状态名称
     * @return 对应的枚举值，如果不存在则返回null
     */
    public static PaymentStatus fromName(String name) {
        if (name == null) {
            return null;
        }
        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.name().equals(name)) {
                return status;
            }
        }
        return null;
    }
    
    /**
     * 验证状态名称是否有效
     * @param name 状态名称
     * @return 如果状态名称有效返回true，否则返回false
     */
    public static boolean isValid(String name) {
        return fromName(name) != null;
    }
}
