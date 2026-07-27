package com.controller;

import com.entity.GongyingshangEntity;
import com.entity.view.GongyingshangView;
import com.service.GongyingshangService;
import com.utils.R;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class GongyingshangControllerTest {

    @Mock
    private GongyingshangService gongyingshangService;

    @InjectMocks
    private GongyingshangController controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPage() {
        when(gongyingshangService.queryPage(any(), any())).thenReturn(ControllerTestSupport.emptyPage());
        R result = controller.page(ControllerTestSupport.pageParams(), new GongyingshangEntity(),
                ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testList() {
        when(gongyingshangService.queryPage(any(), any())).thenReturn(ControllerTestSupport.emptyPage());
        R result = controller.list(ControllerTestSupport.pageParams(), new GongyingshangEntity(),
                ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testLists() {
        when(gongyingshangService.selectListView(any())).thenReturn(Collections.emptyList());
        R result = controller.list(new GongyingshangEntity());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testQuery() {
        when(gongyingshangService.selectView(any())).thenReturn(new GongyingshangView());
        R result = controller.query(new GongyingshangEntity());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testInfo() {
        when(gongyingshangService.getById(1L)).thenReturn(new GongyingshangEntity());
        R result = controller.info(1L);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testDetail() {
        when(gongyingshangService.getById(1L)).thenReturn(new GongyingshangEntity());
        R result = controller.detail(1L);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testSave() {
        R result = controller.save(new GongyingshangEntity(), ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testAdd() {
        GongyingshangEntity entity = new GongyingshangEntity();
        entity.setId(1L);
        R result = controller.add(entity, ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
        assertNotNull(result.get("data"));
    }

    @Test
    public void testUpdate() {
        R result = controller.update(new GongyingshangEntity(), ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testDelete() {
        R result = controller.delete(new Long[]{1L});
        assertEquals(0, result.get("code"));
    }
}
