package com.ims.dto;

import java.sql.Date;
import java.util.PrimitiveIterator;

public class ReportDTO {
    private Float cost;
    private Float revenue;
    private Date openDate;
    private Date endDate;

    public Float getCost() {
        return cost;
    }

    public void setCost(Float cost) {
        this.cost = cost;
    }

    public Float getRevenue() {
        return revenue;
    }

    public void setRevenue(Float revenue) {
        this.revenue = revenue;
    }

    public Date getOpenDate() {
        return openDate;
    }

    public void setOpenDate(Date openDate) {
        this.openDate = openDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
