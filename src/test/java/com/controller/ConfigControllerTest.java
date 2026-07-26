package com.controller;

import com.entity.ConfigEntity;
import com.service.ConfigService;
import com.utils.R;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ConfigControllerTest {

    @Mock
    private ConfigService configService;

    @InjectMocks
    private ConfigController controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPage() {
        when(configService.queryPage(any(), any())).thenReturn(ControllerTestSupport.emptyPage());
        R result = controller.page(ControllerTestSupport.pageParams(), new ConfigEntity());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testList() {
        when(configService.queryPage(any(), any())).thenReturn(ControllerTestSupport.emptyPage());
        R result = controller.list(ControllerTestSupport.pageParams(), new ConfigEntity());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testInfoById() {
        when(configService.selectById("1")).thenReturn(new ConfigEntity());
        R result = controller.info("1");
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testDetail() {
        when(configService.selectById("1")).thenReturn(new ConfigEntity());
        R result = controller.detail("1");
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testInfoByName() {
        when(configService.selectOne(any())).thenReturn(new ConfigEntity());
        R result = controller.infoByName("faceFile");
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testSave() {
        R result = controller.save(new ConfigEntity());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testUpdate() {
        R result = controller.update(new ConfigEntity());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testDelete() {
        R result = controller.delete(new Long[]{1L});
        assertEquals(0, result.get("code"));
    }
}
