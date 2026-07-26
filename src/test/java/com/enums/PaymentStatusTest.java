package com.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentStatusTest {

    @Test
    public void displayNames() {
        assertEquals("未付款", PaymentStatus.UNPAID.getDisplayName());
        assertEquals("已付款", PaymentStatus.PAID.getDisplayName());
    }

    @Test
    public void fromNameRoundTrip() {
        for (PaymentStatus status : PaymentStatus.values()) {
            assertEquals(status, PaymentStatus.fromName(status.name()));
            assertTrue(PaymentStatus.isValid(status.name()));
        }
    }

    @Test
    public void invalidNames() {
        assertNull(PaymentStatus.fromName(null));
        assertNull(PaymentStatus.fromName("INVALID_STATUS"));
        assertFalse(PaymentStatus.isValid(null));
        assertFalse(PaymentStatus.isValid("INVALID_STATUS"));
    }

    @Test
    public void valueOfWorks() {
        assertEquals(PaymentStatus.UNPAID, PaymentStatus.valueOf("UNPAID"));
        assertEquals(PaymentStatus.PAID, PaymentStatus.valueOf("PAID"));
    }
}
