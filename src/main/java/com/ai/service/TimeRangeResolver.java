package com.ai.service;

import com.ai.dto.TimeRange;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.dao.ChukuxinxiDao;
import com.dao.DinghuoxinxiDao;
import com.dao.RukuxinxiDao;
import com.entity.ChukuxinxiEntity;
import com.entity.DinghuoxinxiEntity;
import com.entity.RukuxinxiEntity;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 时间窗解析：月 / 季 / 年 / 自定义。
 * 若演示数据日期早于「今天」，自动以业务库最近业务日为终点，避免近月筛空。
 */
@Component
public class TimeRangeResolver {

    private final ChukuxinxiDao chukuxinxiDao;
    private final RukuxinxiDao rukuxinxiDao;
    private final DinghuoxinxiDao dinghuoxinxiDao;

    public TimeRangeResolver(ChukuxinxiDao chukuxinxiDao,
                             RukuxinxiDao rukuxinxiDao,
                             DinghuoxinxiDao dinghuoxinxiDao) {
        this.chukuxinxiDao = chukuxinxiDao;
        this.rukuxinxiDao = rukuxinxiDao;
        this.dinghuoxinxiDao = dinghuoxinxiDao;
    }

    public TimeRange resolve(String timeRange, String startDate, String endDate) {
        Date end = resolveBusinessEnd();
        Calendar cal = Calendar.getInstance();
        cal.setTime(end);
        String label = "近月";
        int days = 30;

        String tr = timeRange == null ? "MONTH" : timeRange.trim().toUpperCase();
        if ("CUSTOM".equals(tr) && startDate != null && endDate != null) {
            Date s = parseDate(startDate);
            Date e = parseDate(endDate);
            if (s != null && e != null) {
                return new TimeRange(s, e, "自定义");
            }
        }
        if ("QUARTER".equals(tr) || "近季".equals(timeRange) || "季度".equals(timeRange)) {
            days = 90;
            label = "近季";
        } else if ("YEAR".equals(tr) || "近一年".equals(timeRange) || "年".equals(timeRange)) {
            days = 365;
            label = "近一年";
        } else {
            days = 30;
            label = "近月";
        }
        cal.add(Calendar.DAY_OF_MONTH, -days);
        return new TimeRange(cal.getTime(), end, label);
    }

    public TimeRange lastDays(int days) {
        Date end = resolveBusinessEnd();
        Calendar cal = Calendar.getInstance();
        cal.setTime(end);
        cal.add(Calendar.DAY_OF_MONTH, -days);
        return new TimeRange(cal.getTime(), end, "近" + days + "天");
    }

    /**
     * 业务终点：取今天与出库/入库/订货最近日期的较大者，保证样例库旧日期仍能进「近月」。
     */
    private Date resolveBusinessEnd() {
        Date now = new Date();
        Date latest = latestOf(
                latestChuku(),
                latestRuku(),
                latestDinghuo());
        if (latest == null) {
            return now;
        }
        return latest.after(now) ? latest : (daysBetween(latest, now) > 45 ? latest : now);
    }

    private Date latestChuku() {
        List<ChukuxinxiEntity> list = chukuxinxiDao.selectList(
                new EntityWrapper<ChukuxinxiEntity>().orderBy("jiaohuoshijian", false).last("limit 1"));
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0).getJiaohuoshijian();
    }

    private Date latestRuku() {
        List<RukuxinxiEntity> list = rukuxinxiDao.selectList(
                new EntityWrapper<RukuxinxiEntity>().orderBy("rukushijian", false).last("limit 1"));
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0).getRukushijian();
    }

    private Date latestDinghuo() {
        List<DinghuoxinxiEntity> list = dinghuoxinxiDao.selectList(
                new EntityWrapper<DinghuoxinxiEntity>().orderBy("dinghuoshijian", false).last("limit 1"));
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0).getDinghuoshijian();
    }

    private Date latestOf(Date a, Date b, Date c) {
        Date r = a;
        if (b != null && (r == null || b.after(r))) {
            r = b;
        }
        if (c != null && (r == null || c.after(r))) {
            r = c;
        }
        return r;
    }

    private long daysBetween(Date from, Date to) {
        return Math.abs(to.getTime() - from.getTime()) / (24L * 60L * 60L * 1000L);
    }

    private Date parseDate(String text) {
        String[] patterns = {"yyyy-MM-dd", "yyyy/MM/dd", "yyyy-MM-dd HH:mm:ss"};
        for (String p : patterns) {
            try {
                return new SimpleDateFormat(p).parse(text.trim());
            } catch (ParseException ignored) {
            }
        }
        return null;
    }
}
