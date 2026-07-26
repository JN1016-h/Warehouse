package com.ai.dto;

import java.util.Date;

/**
 * 时间窗
 */
public class TimeRange {

    private Date start;
    private Date end;
    private String label;

    public TimeRange() {
    }

    public TimeRange(Date start, Date end, String label) {
        this.start = start;
        this.end = end;
        this.label = label;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public long days() {
        if (start == null || end == null) {
            return 30L;
        }
        long diff = end.getTime() - start.getTime();
        long d = diff / (24L * 60L * 60L * 1000L);
        return d <= 0 ? 1L : d;
    }
}
