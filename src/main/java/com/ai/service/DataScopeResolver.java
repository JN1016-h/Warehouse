package com.ai.service;

import com.entity.YonghuEntity;
import com.enums.UserRole;
import com.service.YonghuService;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * FR10 角色数据域：仓管不可看财务；内部员工/系统管理员可看；经销商关闭 AI
 */
@Component
public class DataScopeResolver {

    public static final String SCOPE_WAREHOUSE = "WAREHOUSE";
    public static final String SCOPE_FINANCE_OK = "FINANCE_OK";
    public static final String SCOPE_DENIED = "DENIED";

    private final YonghuService yonghuService;

    public DataScopeResolver(YonghuService yonghuService) {
        this.yonghuService = yonghuService;
    }

    public ResolvedUser resolve(HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        String tableName = (String) request.getSession().getAttribute("tableName");
        String username = (String) request.getSession().getAttribute("username");

        ResolvedUser user = new ResolvedUser();
        user.userId = userId;
        user.tableName = tableName;
        user.username = username;

        // users 表登录为系统管理员，财务域开放（不再使用 PLATFORM_ADMIN）
        if ("users".equalsIgnoreCase(tableName)) {
            user.role = UserRole.INTERNAL_STAFF;
            user.dataScope = SCOPE_FINANCE_OK;
            return user;
        }

        UserRole role = UserRole.DEALER;
        if (userId != null) {
            YonghuEntity yonghu = yonghuService.selectById(userId);
            if (yonghu != null && yonghu.getUserRole() != null) {
                UserRole parsed = UserRole.fromName(yonghu.getUserRole());
                if (parsed != null) {
                    role = parsed;
                }
            }
        }
        user.role = role;
        if (role == UserRole.DEALER) {
            user.dataScope = SCOPE_DENIED;
        } else if (role == UserRole.WAREHOUSE_ADMIN) {
            user.dataScope = SCOPE_WAREHOUSE;
        } else {
            // INTERNAL_STAFF
            user.dataScope = SCOPE_FINANCE_OK;
        }
        return user;
    }

    public boolean canAccessFinance(String dataScope) {
        return SCOPE_FINANCE_OK.equals(dataScope);
    }

    public boolean canUseAi(String dataScope) {
        return !SCOPE_DENIED.equals(dataScope);
    }

    public static class ResolvedUser {
        public Long userId;
        public String tableName;
        public String username;
        public UserRole role;
        public String dataScope;
    }
}
