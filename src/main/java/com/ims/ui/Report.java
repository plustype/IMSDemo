package com.ims.ui;

import com.ims.dao.ReportDAO;
import com.ims.database.DBConnection;
import com.ims.dto.ReportDTO;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Report {
    private JFormattedTextField formattedTextFieldEndDate;
    private JFormattedTextField formattedTextFieldPeriodRecenue;
    private JFormattedTextField formattedTextFieldPeriodCost;
    private JFormattedTextField formattedTextFieldPeriodIncome;
    private JLabel labelTitle;
    private JLabel labelOpenDate;
    private JLabel labelEndDate;
    private JLabel labelPeriodRevenue;
    private JLabel labelPeriodCost;
    private JLabel labelPeriodIncome;
    private JPanel report;
    private JButton buttonSearchDate;
    private JFormattedTextField formattedTextFieldOpenDate;
    private JButton buttonHome;

    private Connection conn;
    private PreparedStatement loadTable;
    private PreparedStatement pstmt;

    JFrame frame = new JFrame();

    public Report() {
        frame.setContentPane(report);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        conn = new DBConnection().getDBConnection();
        buttonSearchDate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    clickSearchPeriodButton();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
        buttonHome.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new Dashboard();
            }
        });
    }

    private void clickSearchPeriodButton() throws SQLException {
        try {
            ReportDAO reportDAO = new ReportDAO();
            formattedTextFieldPeriodCost.setText(String.valueOf(reportDAO.periodCost(transferTextFieldToReportDTO())));
            formattedTextFieldPeriodRecenue.setText(String.valueOf(reportDAO.periodRevenue(transferTextFieldToReportDTO())));
            formattedTextFieldPeriodIncome.setText(String.valueOf(reportDAO.periodIncome(transferTextFieldToReportDTO())));
        } catch (Exception e) {
            //JOptionPane.showConfirmDialog(null, "Invalid Date");

        }
    }

    private ReportDTO transferTextFieldToReportDTO() {
        try {
            ReportDTO reportDTO = new ReportDTO();
            reportDTO.setOpenDate(Date.valueOf(formattedTextFieldOpenDate.getText()));
            reportDTO.setEndDate(Date.valueOf(formattedTextFieldEndDate.getText()));
            return reportDTO;
        }catch (Exception e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "Invalid Date");
            formattedTextFieldOpenDate.setText("");
            formattedTextFieldEndDate.setText("");
        }
        return null;
    }
}
