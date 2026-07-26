package com.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserRoleTest {

    @Test
    public void displayNames() {
        assertEquals("经销商", UserRole.DEALER.getDisplayName());
        assertEquals("内部员工", UserRole.INTERNAL_STAFF.getDisplayName());
        assertEquals("仓库管理员", UserRole.WAREHOUSE_ADMIN.getDisplayName());
    }

    @Test
    public void fromNameRoundTrip() {
        for (UserRole role : UserRole.values()) {
            assertEquals(role, UserRole.fromName(role.name()));
            assertTrue(UserRole.isValid(role.name()));
        }
    }

    @Test
    public void legacyPlatformAdminMapsToInternalStaff() {
        assertEquals(UserRole.INTERNAL_STAFF, UserRole.fromName("PLATFORM_ADMIN"));
    }

    @Test
    public void invalidNames() {
        assertNull(UserRole.fromName(null));
        assertNull(UserRole.fromName("UNKNOWN_ROLE_XYZ"));
        assertFalse(UserRole.isValid(null));
        assertFalse(UserRole.isValid("PLATFORM_ADMIN"));
    }
}
