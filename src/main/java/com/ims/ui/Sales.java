package com.ims.ui;

import com.ims.dao.PurchaseDAO;
import com.ims.dao.SalesDAO;
import com.ims.database.DBConnection;
import com.ims.dto.SalesDTO;
import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.Objects;

public class Sales {
    private JTable tableSales;
    private JPanel sales;
    private JLabel labelTitle;
    private JFormattedTextField formattedTextFieldDate;
    private JComboBox comboBoxProduct;
    private JComboBox comboBoxCustomer;
    private JFormattedTextField formattedTextFieldQuantity;
    private JFormattedTextField formattedTextFieldPrice;
    private JFormattedTextField formattedTextFieldRevenue;
    private JButton buttonAdd;
    private JButton buttonUpdate;
    private JButton buttonDelete;
    private JButton buttonClear;
    private JButton buttonHome;
    private JButton buttonSearchID;
    private JFormattedTextField formattedTextFieldSearchID;
    private JButton buttonCalculate;
    private JLabel lableDate;
    private JLabel labelProduct;
    private JLabel labelCustomer;
    private JLabel labelQuantity;
    private JLabel labelPrice;
    private JLabel labelRevenue;

    private Connection conn;
    private PreparedStatement loadTable;
    private PreparedStatement pstmt;

    JFrame frame = new JFrame();

    public void loadTable() {
        try {
            loadTable = conn.prepareStatement("SELECT * FROM sell_view");
            ResultSet results = loadTable.executeQuery();

            tableSales.setModel(DbUtils.resultSetToTableModel(results));
        }catch (SQLException e) {
            System.out.println("Could not load table " + e.getMessage());
        }
    }

    public Sales() {
        frame.setContentPane(sales);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        conn = new DBConnection().getDBConnection();
        loadTable();

        tableSales.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                clickTableRow();
            }
        });

        buttonAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    clickAddButton();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });

        buttonCalculate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clickCalculateButton();
            }
        });

        buttonSearchID.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clickSearchIDButton();
            }
        });

        buttonUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    clickUpdateButton();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });

        buttonDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clickDeleteButton();
            }
        });

        buttonClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearTextFields();
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

    private void clickAddButton() throws SQLException {
        if (checkAllTextFieldsAreFilled())
            JOptionPane.showMessageDialog(null, "Please fill all the information");
        else {
            if (transferTextFieldToSalesDTOWithoutSearchID() == null)
                return;
            else
                new SalesDAO().addSales(transferTextFieldToSalesDTOWithoutSearchID());
        }
        clearTextFields();
        loadTable();
    }

    private void clickUpdateButton() throws SQLException {
        try {

            if (checkAllTextFieldsAreFilled())
                JOptionPane.showMessageDialog(null, "Please fill all the information");
            else {
                new SalesDAO().updateSales(transferTextFieldToSalesDTO());
                clearTextFields();
                loadTable();
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "Only number is allowed for 'Search ID' button");
            clearTextFields();
        }
    }

    private void clickDeleteButton() {
        try {
            if (formattedTextFieldSearchID.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Please input purchase ID");
            }
            else {
                new SalesDAO().deleteSales(Integer.parseInt(formattedTextFieldSearchID.getText()));
                clearTextFields();
                loadTable();
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "Only number is allowed for Search ID");
            clearTextFields();
        }
    }

    private void clickSearchIDButton() {
        try {
            var list = new SalesDAO().searchSalesID(Integer.parseInt(formattedTextFieldSearchID.getText()));

            if (list != null) {
                formattedTextFieldDate.setText(list.get(0));
                comboBoxProduct.setSelectedItem(list.get(1));
                formattedTextFieldQuantity.setText(list.get(2));
                formattedTextFieldPrice.setText((list.get(3)));
                formattedTextFieldRevenue.setText(list.get(4));
                comboBoxCustomer.setSelectedItem(list.get(5));

            }else {
                clearTextFields();
                JOptionPane.showMessageDialog(null, "Invalid Sale ID");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "Only number is allowed for 'Search ID' button");
            clearTextFields();
        }

    }

    private void clickCalculateButton() {
        if (formattedTextFieldQuantity.getText().equals("") || formattedTextFieldPrice.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Missing quantity or price");
            formattedTextFieldRevenue.setText("");
        }
        else {
            float cost = Float.parseFloat(formattedTextFieldQuantity.getText()) * Float.parseFloat(formattedTextFieldPrice.getText());
            //cost keeps two decimal digits
            DecimalFormat decimalFormat= new DecimalFormat( ".00" );
            formattedTextFieldRevenue.setText(decimalFormat.format(cost));
        }
    }

    private void clickTableRow() {
        int row = tableSales.getSelectedRow();
        int column = tableSales.getColumnCount();
        Object[] val = new Object[column];
        for(int i = 0; i < column; i++) {
            val[i]=tableSales.getValueAt(row, i);
        }
        formattedTextFieldSearchID.setText(val[0].toString());
        formattedTextFieldDate.setText(val[1].toString());
        comboBoxProduct.setSelectedItem(val[2]);
        formattedTextFieldQuantity.setText(val[3].toString());
        formattedTextFieldPrice.setText(val[4].toString());
        formattedTextFieldRevenue.setText(val[5].toString());
        comboBoxCustomer.setSelectedItem(val[6]);
    }

    private boolean checkAllTextFieldsAreFilled() {
        return !formattedTextFieldDate.getText().equals("") && !Objects.equals(comboBoxProduct.getSelectedItem(), "") &&
                !Objects.equals(comboBoxCustomer.getSelectedItem(), "") && formattedTextFieldPrice.getText().equals("") &&
                formattedTextFieldQuantity.getText().equals("") && formattedTextFieldRevenue.getText().equals("");
    }

    private SalesDTO transferTextFieldToSalesDTO() {
        try {
            SalesDTO salesDTO = new SalesDTO();
            salesDTO.setDate(Date.valueOf(formattedTextFieldDate.getText()));
            salesDTO.setQuantity(Integer.parseInt(formattedTextFieldQuantity.getText()));
            salesDTO.setPrice(Float.valueOf(formattedTextFieldPrice.getText()));
            salesDTO.setRevenue(Float.valueOf(formattedTextFieldRevenue.getText()));
            salesDTO.setSaleId(Integer.parseInt(formattedTextFieldSearchID.getText()));
            //Convert product/supplier name to product/supplier id
            salesDTO.setProductId(new SalesDAO().getProductID((String) comboBoxProduct.getSelectedItem()));
            salesDTO.setCustomerId(new SalesDAO().getCustomerID((String) comboBoxCustomer.getSelectedItem()));
            return salesDTO;
        }catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Please input correct information");
            System.out.println(e.getMessage());
        }
        return null;
    }

    private SalesDTO transferTextFieldToSalesDTOWithoutSearchID() throws SQLException {

        try {
            SalesDTO salesDTO = new SalesDTO();

            salesDTO.setDate(Date.valueOf(formattedTextFieldDate.getText()));
            salesDTO.setQuantity(Integer.parseInt(formattedTextFieldQuantity.getText()));
            salesDTO.setPrice(Float.valueOf(formattedTextFieldPrice.getText()));
            salesDTO.setRevenue(Float.valueOf(formattedTextFieldRevenue.getText()));
            //Convert product/supplier name to product/supplier id
            salesDTO.setProductId(new SalesDAO().getProductID((String) comboBoxProduct.getSelectedItem()));
            salesDTO.setCustomerId(new SalesDAO().getCustomerID((String) comboBoxCustomer.getSelectedItem()));
            return salesDTO;
        }catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Please input correct information");
            System.out.println(e.getMessage());
        }
        return null;
    }

    private void clearTextFields() {
        formattedTextFieldDate.setText("");
        formattedTextFieldQuantity.setText("");
        formattedTextFieldPrice.setText("");
        formattedTextFieldRevenue.setText("");
        formattedTextFieldSearchID.setText("");
        comboBoxProduct.setSelectedItem("");
        comboBoxCustomer.setSelectedItem("");
    }
}
