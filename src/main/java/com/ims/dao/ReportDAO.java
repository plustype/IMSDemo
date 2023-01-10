package com.ims.dao;

import com.ims.database.DBConnection;
import com.ims.dto.ReportDTO;

import java.sql.*;
import java.text.DecimalFormat;

public class ReportDAO {
    Connection conn;
    Statement stmt;
    PreparedStatement pstmt;
    ResultSet rs;

    public ReportDAO() throws SQLException {
        conn = new DBConnection().getDBConnection();
        stmt = conn.createStatement();
    }

    public Float periodCost(ReportDTO reportDTO) {
        try {
            String query = "SELECT SUM(cost) FROM purchase WHERE date BETWEEN ? AND ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setDate(1, reportDTO.getOpenDate());
            pstmt.setDate(2, reportDTO.getEndDate());
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getFloat(1);
            }
            else
                return null;
        } catch (SQLException e) {
            System.out.println("Calculate period cost failed " +e.getMessage());
        }
        return null;
    }

    public Float periodRevenue(ReportDTO reportDTO) {
        try {
            String query = "SELECT SUM(revenue) FROM sell WHERE date BETWEEN ? AND ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setDate(1, reportDTO.getOpenDate());
            pstmt.setDate(2, reportDTO.getEndDate());
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getFloat(1);
            }
            else
                return null;
        } catch (SQLException e) {
            System.out.println("Calculate period revenue failed " +e.getMessage());
        }
        return null;
    }

    public float periodIncome(ReportDTO reportDTO) {
        DecimalFormat decimalFormat= new DecimalFormat( ".00" );
        float income = Float.parseFloat(decimalFormat.format(periodRevenue(reportDTO) - periodCost(reportDTO)));
        return income;
    }
}
