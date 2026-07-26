package com.ai.service;

import com.ai.dto.TimeRange;
import com.dao.ChukuxinxiDao;
import com.dao.DinghuoxinxiDao;
import com.dao.RukuxinxiDao;
import com.entity.ChukuxinxiEntity;
import com.entity.DinghuoxinxiEntity;
import com.entity.RukuxinxiEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TimeRangeResolverTest {

    @Mock
    private ChukuxinxiDao chukuxinxiDao;
    @Mock
    private RukuxinxiDao rukuxinxiDao;
    @Mock
    private DinghuoxinxiDao dinghuoxinxiDao;

    @InjectMocks
    private TimeRangeResolver timeRangeResolver;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        stubLatestBusinessDate(daysAgo(10));
    }

    @Test
    public void resolveDefaultMonth() {
        TimeRange range = timeRangeResolver.resolve(null, null, null);

        assertEquals("近月", range.getLabel());
        assertNotNull(range.getStart());
        assertNotNull(range.getEnd());
        assertTrue(range.days() >= 1);
    }

    @Test
    public void resolveQuarter() {
        TimeRange range = timeRangeResolver.resolve("QUARTER", null, null);
        assertEquals("近季", range.getLabel());
    }

    @Test
    public void resolveQuarterChinese() {
        TimeRange range = timeRangeResolver.resolve("近季", null, null);
        assertEquals("近季", range.getLabel());
    }

    @Test
    public void resolveYear() {
        TimeRange range = timeRangeResolver.resolve("YEAR", null, null);
        assertEquals("近一年", range.getLabel());
    }

    @Test
    public void resolveYearChinese() {
        TimeRange range = timeRangeResolver.resolve("近一年", null, null);
        assertEquals("近一年", range.getLabel());
    }

    @Test
    public void resolveCustomValid() {
        TimeRange range = timeRangeResolver.resolve("CUSTOM", "2025-01-01", "2025-01-31");

        assertEquals("自定义", range.getLabel());
        assertNotNull(range.getStart());
        assertNotNull(range.getEnd());
    }

    @Test
    public void resolveCustomInvalidFallsBackMonth() {
        TimeRange range = timeRangeResolver.resolve("CUSTOM", "bad", "2025-01-31");
        assertEquals("近月", range.getLabel());
    }

    @Test
    public void lastDays() {
        TimeRange range = timeRangeResolver.lastDays(7);
        assertEquals("近7天", range.getLabel());
    }

    @Test
    public void resolveUsesOldBusinessDateAsEnd() {
        Date old = daysAgo(60);
        ChukuxinxiEntity chuku = new ChukuxinxiEntity();
        chuku.setJiaohuoshijian(old);
        when(chukuxinxiDao.selectList(any())).thenReturn(Collections.singletonList(chuku));
        when(rukuxinxiDao.selectList(any())).thenReturn(Collections.<RukuxinxiEntity>emptyList());
        when(dinghuoxinxiDao.selectList(any())).thenReturn(Collections.<DinghuoxinxiEntity>emptyList());

        TimeRange range = timeRangeResolver.resolve("MONTH", null, null);

        assertNotNull(range.getEnd());
        long diffDays = Math.abs(range.getEnd().getTime() - old.getTime()) / (24L * 3600 * 1000);
        assertTrue(diffDays <= 1);
    }

    @Test
    public void resolveNoBusinessDataUsesNow() {
        when(chukuxinxiDao.selectList(any())).thenReturn(Collections.<ChukuxinxiEntity>emptyList());
        when(rukuxinxiDao.selectList(any())).thenReturn(Collections.<RukuxinxiEntity>emptyList());
        when(dinghuoxinxiDao.selectList(any())).thenReturn(Collections.<DinghuoxinxiEntity>emptyList());

        TimeRange range = timeRangeResolver.resolve("MONTH", null, null);
        assertNotNull(range.getEnd());
    }

    @Test
    public void resolveQuarterAliasChinese() {
        assertEquals("近季", timeRangeResolver.resolve("季度", null, null).getLabel());
    }

    @Test
    public void resolveYearAliasChinese() {
        assertEquals("近一年", timeRangeResolver.resolve("年", null, null).getLabel());
    }

    @Test
    public void resolveCustomPartialDatesFallsBack() {
        TimeRange range = timeRangeResolver.resolve("CUSTOM", "2025-01-01", null);
        assertEquals("近月", range.getLabel());
    }

    @Test
    public void resolveCustomWithSlashFormat() {
        TimeRange range = timeRangeResolver.resolve("CUSTOM", "2025/01/01", "2025/01/31");
        assertEquals("自定义", range.getLabel());
    }

    @Test
    public void resolveCustomWithDateTimeFormat() {
        TimeRange range = timeRangeResolver.resolve("CUSTOM", "2025-01-01 00:00:00", "2025-01-31 23:59:59");
        assertEquals("自定义", range.getLabel());
    }

    @Test
    public void resolveBusinessEndUsesFutureLatest() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 10);
        Date future = cal.getTime();
        ChukuxinxiEntity chuku = new ChukuxinxiEntity();
        chuku.setJiaohuoshijian(future);
        when(chukuxinxiDao.selectList(any())).thenReturn(Collections.singletonList(chuku));
        when(rukuxinxiDao.selectList(any())).thenReturn(Collections.<RukuxinxiEntity>emptyList());
        when(dinghuoxinxiDao.selectList(any())).thenReturn(Collections.<DinghuoxinxiEntity>emptyList());

        TimeRange range = timeRangeResolver.resolve("MONTH", null, null);
        long diff = Math.abs(range.getEnd().getTime() - future.getTime());
        assertTrue(diff < 2000);
    }

    @Test
    public void resolveBusinessEndUsesOldLatestWhenGapLarge() {
        Date old = daysAgo(60);
        when(chukuxinxiDao.selectList(any())).thenReturn(Collections.<ChukuxinxiEntity>emptyList());
        RukuxinxiEntity ruku = new RukuxinxiEntity();
        ruku.setRukushijian(old);
        when(rukuxinxiDao.selectList(any())).thenReturn(Collections.singletonList(ruku));
        when(dinghuoxinxiDao.selectList(any())).thenReturn(Collections.<DinghuoxinxiEntity>emptyList());

        TimeRange range = timeRangeResolver.resolve("MONTH", null, null);
        long diffDays = Math.abs(range.getEnd().getTime() - old.getTime()) / (24L * 3600 * 1000);
        assertTrue(diffDays <= 1);
    }

    @Test
    public void resolveUsesLatestDinghuoWhenNewest() {
        Date newest = daysAgo(1);
        when(chukuxinxiDao.selectList(any())).thenReturn(Collections.<ChukuxinxiEntity>emptyList());
        when(rukuxinxiDao.selectList(any())).thenReturn(Collections.<RukuxinxiEntity>emptyList());
        DinghuoxinxiEntity dinghuo = new DinghuoxinxiEntity();
        dinghuo.setDinghuoshijian(newest);
        when(dinghuoxinxiDao.selectList(any())).thenReturn(Collections.singletonList(dinghuo));

        TimeRange range = timeRangeResolver.resolve("MONTH", null, null);
        long diffDays = Math.abs(range.getEnd().getTime() - newest.getTime()) / (24L * 3600 * 1000);
        assertTrue(diffDays <= 1);
    }

    @Test
    public void resolveNullDaoLists() {
        when(chukuxinxiDao.selectList(any())).thenReturn(null);
        when(rukuxinxiDao.selectList(any())).thenReturn(null);
        when(dinghuoxinxiDao.selectList(any())).thenReturn(null);

        TimeRange range = timeRangeResolver.resolve("MONTH", null, null);
        assertNotNull(range.getEnd());
    }

    private void stubLatestBusinessDate(Date date) {
        ChukuxinxiEntity chuku = new ChukuxinxiEntity();
        chuku.setJiaohuoshijian(date);
        when(chukuxinxiDao.selectList(any())).thenReturn(Collections.singletonList(chuku));

        RukuxinxiEntity ruku = new RukuxinxiEntity();
        ruku.setRukushijian(daysAgo(15));
        when(rukuxinxiDao.selectList(any())).thenReturn(Collections.singletonList(ruku));

        DinghuoxinxiEntity dinghuo = new DinghuoxinxiEntity();
        dinghuo.setDinghuoshijian(daysAgo(20));
        when(dinghuoxinxiDao.selectList(any())).thenReturn(Collections.singletonList(dinghuo));
    }

    private Date daysAgo(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -days);
        return cal.getTime();
    }
}
