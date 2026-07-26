package com.ai.service;

import com.entity.YonghuEntity;
import com.enums.UserRole;
import com.service.YonghuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DataScopeResolverTest {

    @Mock
    private YonghuService yonghuService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @InjectMocks
    private DataScopeResolver dataScopeResolver;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(request.getSession()).thenReturn(session);
    }

    @Test
    public void resolveUsersTableHasFinanceScope() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(session.getAttribute("tableName")).thenReturn("users");
        when(session.getAttribute("username")).thenReturn("admin");

        DataScopeResolver.ResolvedUser user = dataScopeResolver.resolve(request);

        assertEquals(UserRole.INTERNAL_STAFF, user.role);
        assertEquals(DataScopeResolver.SCOPE_FINANCE_OK, user.dataScope);
        verify(yonghuService, never()).selectById(any());
    }

    @Test
    public void resolveDealerDenied() {
        when(session.getAttribute("userId")).thenReturn(2L);
        when(session.getAttribute("tableName")).thenReturn("yonghu");
        when(session.getAttribute("username")).thenReturn("dealer1");

        YonghuEntity yonghu = new YonghuEntity();
        yonghu.setUserRole("DEALER");
        when(yonghuService.selectById(2L)).thenReturn(yonghu);

        DataScopeResolver.ResolvedUser user = dataScopeResolver.resolve(request);

        assertEquals(UserRole.DEALER, user.role);
        assertEquals(DataScopeResolver.SCOPE_DENIED, user.dataScope);
        assertFalse(dataScopeResolver.canUseAi(user.dataScope));
    }

    @Test
    public void resolveWarehouseAdminScope() {
        when(session.getAttribute("userId")).thenReturn(3L);
        when(session.getAttribute("tableName")).thenReturn("yonghu");

        YonghuEntity yonghu = new YonghuEntity();
        yonghu.setUserRole("WAREHOUSE_ADMIN");
        when(yonghuService.selectById(3L)).thenReturn(yonghu);

        DataScopeResolver.ResolvedUser user = dataScopeResolver.resolve(request);

        assertEquals(UserRole.WAREHOUSE_ADMIN, user.role);
        assertEquals(DataScopeResolver.SCOPE_WAREHOUSE, user.dataScope);
        assertTrue(dataScopeResolver.canUseAi(user.dataScope));
        assertFalse(dataScopeResolver.canAccessFinance(user.dataScope));
    }

    @Test
    public void resolveInternalStaffFinanceOk() {
        when(session.getAttribute("userId")).thenReturn(4L);
        when(session.getAttribute("tableName")).thenReturn("yonghu");

        YonghuEntity yonghu = new YonghuEntity();
        yonghu.setUserRole("INTERNAL_STAFF");
        when(yonghuService.selectById(4L)).thenReturn(yonghu);

        DataScopeResolver.ResolvedUser user = dataScopeResolver.resolve(request);

        assertEquals(UserRole.INTERNAL_STAFF, user.role);
        assertEquals(DataScopeResolver.SCOPE_FINANCE_OK, user.dataScope);
        assertTrue(dataScopeResolver.canAccessFinance(user.dataScope));
    }

    @Test
    public void resolveNullUserIdDefaultsDealer() {
        when(session.getAttribute("userId")).thenReturn(null);
        when(session.getAttribute("tableName")).thenReturn("yonghu");

        DataScopeResolver.ResolvedUser user = dataScopeResolver.resolve(request);

        assertEquals(UserRole.DEALER, user.role);
        assertEquals(DataScopeResolver.SCOPE_DENIED, user.dataScope);
    }
}
