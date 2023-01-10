package com.ims.ui;

import com.ims.dao.StockDAO;
import com.ims.dao.SupplierDAO;
import com.ims.database.DBConnection;
import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Stock {
    private JTable tableStock;
    private JButton buttonSearchID;
    private JFormattedTextField formattedTextFieldSearchID;
    private JButton buttonHome;
    private JPanel stock;
    private JFormattedTextField formattedTextFieldProductID;
    private JFormattedTextField formattedTextFieldProductName;
    private JFormattedTextField formattedTextFieldStock;
    private JLabel labelProductName;
    private JLabel labelStock;

    private Connection conn;
    private PreparedStatement loadTable;
    private PreparedStatement pstmt;

    JFrame frame = new JFrame();

    public void loadTable() {
        try {
            loadTable = conn.prepareStatement("SELECT * FROM stock_view");
            ResultSet results = loadTable.executeQuery();

            tableStock.setModel(DbUtils.resultSetToTableModel(results));
        }catch (SQLException e) {
            System.out.println("Could not load table " + e.getMessage());
        }
    }

    public Stock() {
        frame.setContentPane(stock);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        conn = new DBConnection().getDBConnection();
        loadTable();
        buttonSearchID.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    clickSearchButton();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
        tableStock.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                clickTableRow();
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

    private void clickTableRow() {
        int row = tableStock.getSelectedRow();
        int column = tableStock.getColumnCount();
        Object[] val = new Object[column];
        for(int i = 0; i < column; i++) {
            val[i]=tableStock.getValueAt(row, i);
        }

        formattedTextFieldSearchID.setText(val[0].toString());
        formattedTextFieldProductName.setText((String) val[1]);
        formattedTextFieldStock.setText(val[2].toString());

    }

    private void clickSearchButton() throws SQLException {

        try {
            var list = new StockDAO().searchProductID(Integer.parseInt(formattedTextFieldSearchID.getText()));


            if (list != null) {
                formattedTextFieldProductName.setText(list.get(0));
                formattedTextFieldStock.setText(list.get(1));
            } else {
                clearTextFields();
                JOptionPane.showMessageDialog(null, "Invalid Product ID");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "Only number is allowed for 'Search ID' button");
            clearTextFields();
        }
    }

    private void clearTextFields() {
        formattedTextFieldSearchID.setText("");
        formattedTextFieldProductName.setText("");
        formattedTextFieldStock.setText("");
    }
}
