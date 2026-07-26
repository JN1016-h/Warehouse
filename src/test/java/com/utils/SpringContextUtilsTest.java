package com.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * SpringContextUtils unit tests.
 */
public class SpringContextUtilsTest {

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        SpringContextUtils.applicationContext = null;
    }

    @Test
    public void testSetApplicationContextAndDelegates() {
        ApplicationContext context = mock(ApplicationContext.class);
        SpringContextUtils utils = new SpringContextUtils();
        utils.setApplicationContext(context);

        assertSame(context, SpringContextUtils.applicationContext);

        when(context.getBean("demo")).thenReturn("bean");
        when(context.getBean("typed", String.class)).thenReturn("typed");
        when(context.containsBean("demo")).thenReturn(true);
        when(context.isSingleton("demo")).thenReturn(true);
        when(context.getType("demo")).thenReturn((Class) String.class);

        assertEquals("bean", SpringContextUtils.getBean("demo"));
        assertEquals("typed", SpringContextUtils.getBean("typed", String.class));
        assertTrue(SpringContextUtils.containsBean("demo"));
        assertTrue(SpringContextUtils.isSingleton("demo"));
        assertEquals(String.class, SpringContextUtils.getType("demo"));
    }
}
