package com.controller;

import com.entity.ShangpinfenleiEntity;
import com.entity.view.ShangpinfenleiView;
import com.service.ShangpinfenleiService;
import com.utils.R;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ShangpinfenleiControllerTest {

    @Mock
    private ShangpinfenleiService shangpinfenleiService;

    @InjectMocks
    private ShangpinfenleiController controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPage() {
        when(shangpinfenleiService.queryPage(any(), any())).thenReturn(ControllerTestSupport.emptyPage());
        R result = controller.page(ControllerTestSupport.pageParams(), new ShangpinfenleiEntity(),
                ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testList() {
        when(shangpinfenleiService.queryPage(any(), any())).thenReturn(ControllerTestSupport.emptyPage());
        R result = controller.list(ControllerTestSupport.pageParams(), new ShangpinfenleiEntity(),
                ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testLists() {
        when(shangpinfenleiService.selectListView(any())).thenReturn(Collections.emptyList());
        R result = controller.list(new ShangpinfenleiEntity());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testQuery() {
        when(shangpinfenleiService.selectView(any())).thenReturn(new ShangpinfenleiView());
        R result = controller.query(new ShangpinfenleiEntity());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testInfo() {
        when(shangpinfenleiService.selectById(1L)).thenReturn(new ShangpinfenleiEntity());
        R result = controller.info(1L);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testDetail() {
        when(shangpinfenleiService.selectById(1L)).thenReturn(new ShangpinfenleiEntity());
        R result = controller.detail(1L);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testSave() {
        HttpServletRequest request = ControllerTestSupport.mockAdminRequest();
        R result = controller.save(new ShangpinfenleiEntity(), request);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testAdd() {
        ShangpinfenleiEntity entity = new ShangpinfenleiEntity();
        entity.setId(1L);
        R result = controller.add(entity, ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
        assertNotNull(result.get("data"));
    }

    @Test
    public void testUpdate() {
        R result = controller.update(new ShangpinfenleiEntity(), ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testDelete() {
        R result = controller.delete(new Long[]{1L, 2L});
        assertEquals(0, result.get("code"));
    }
}
