package com.ims.ui;

import com.ims.dao.PurchaseDAO;
import com.ims.database.DBConnection;
import com.ims.dto.PurchaseDTO;
import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.Objects;

public class Purchase {
    private JTable tablePurchase;
    private JPanel purchase;
    private JLabel labelTitle;
    private JComboBox comboBoxProduct;
    private JComboBox comboBoxSupplier;
    private JFormattedTextField formattedTextFieldQuantity;
    private JFormattedTextField formattedTextFieldPrice;
    private JFormattedTextField formattedTextFieldCost;
    private JButton buttonAdd;
    private JButton buttonUpdate;
    private JButton buttonDelete;
    private JButton buttonClear;
    private JButton buttonHome;
    private JFormattedTextField formattedTextFieldSearchID;
    private JFormattedTextField formattedTextFieldDate;
    private JLabel labelDate;
    private JLabel labelProduct;
    private JLabel labelSupplier;
    private JLabel labelQuantity;
    private JLabel labelPrice;
    private JLabel labelCost;
    private JButton buttonCalculate;
    private JButton buttonSearchID;

    private Connection conn;
    private PreparedStatement loadTable;
    private PreparedStatement pstmt;

    JFrame frame = new JFrame();

    public void loadTable() {
        try {
            loadTable = conn.prepareStatement("SELECT * FROM purchase_view");
            ResultSet results = loadTable.executeQuery();

            tablePurchase.setModel(DbUtils.resultSetToTableModel(results));
        }catch (SQLException e) {
            System.out.println("Could not load table " + e.getMessage());
        }
    }

    public Purchase() {
        frame.setContentPane(purchase);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        conn = new DBConnection().getDBConnection();
        loadTable();

        tablePurchase.addMouseListener(new MouseAdapter() {
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
            if (transferTextFieldToPurchaseDTOWithoutSearchID() == null)
                return;
            else
                new PurchaseDAO().addPurchase(transferTextFieldToPurchaseDTOWithoutSearchID());
            }
            clearTextFields();
            loadTable();
        }

        private void clickUpdateButton() throws SQLException {
        try {

            if (checkAllTextFieldsAreFilled())
                JOptionPane.showMessageDialog(null, "Please fill all the information");
            else {
                new PurchaseDAO().updatePurchase(transferTextFieldToPurchaseDTO());
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
                new PurchaseDAO().deletePurchase(Integer.parseInt(formattedTextFieldSearchID.getText()));
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
            var list = new PurchaseDAO().searchPurchaseID(Integer.parseInt(formattedTextFieldSearchID.getText()));

            if (list != null) {
                formattedTextFieldDate.setText(list.get(0));
                comboBoxProduct.setSelectedItem(list.get(1));
                formattedTextFieldQuantity.setText(list.get(2));
                formattedTextFieldPrice.setText((list.get(3)));
                formattedTextFieldCost.setText(list.get(4));
                comboBoxSupplier.setSelectedItem(list.get(5));

            }else {
                clearTextFields();
                JOptionPane.showMessageDialog(null, "Invalid Supplier ID");
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
            formattedTextFieldCost.setText("");
        }
        else {
            float cost = Float.parseFloat(formattedTextFieldQuantity.getText()) * Float.parseFloat(formattedTextFieldPrice.getText());
            //cost keeps two decimal digits
            DecimalFormat decimalFormat= new DecimalFormat( ".00" );
            formattedTextFieldCost.setText(decimalFormat.format(cost));
        }
    }

        private void clickTableRow() {
        int row = tablePurchase.getSelectedRow();
        int column = tablePurchase.getColumnCount();
        Object[] val = new Object[column];
        for(int i = 0; i < column; i++) {
            val[i]=tablePurchase.getValueAt(row, i);
        }
        formattedTextFieldSearchID.setText(val[0].toString());
        formattedTextFieldDate.setText(val[1].toString());
        comboBoxProduct.setSelectedItem(val[2]);
        formattedTextFieldQuantity.setText(val[3].toString());
        formattedTextFieldPrice.setText(val[4].toString());
        formattedTextFieldCost.setText(val[5].toString());
        comboBoxSupplier.setSelectedItem(val[6]);
    }

    private boolean checkAllTextFieldsAreFilled() {
        return !formattedTextFieldDate.getText().equals("") && !Objects.equals(comboBoxProduct.getSelectedItem(), "") &&
                !Objects.equals(comboBoxSupplier.getSelectedItem(), "") && formattedTextFieldPrice.getText().equals("") &&
                formattedTextFieldQuantity.getText().equals("") && formattedTextFieldCost.getText().equals("");
    }

    private PurchaseDTO transferTextFieldToPurchaseDTO() {
        try {
            PurchaseDTO purchaseDTO = new PurchaseDTO();
            purchaseDTO.setDate(Date.valueOf(formattedTextFieldDate.getText()));
            purchaseDTO.setQuantity(Integer.parseInt(formattedTextFieldQuantity.getText()));
            purchaseDTO.setPrice(Float.valueOf(formattedTextFieldPrice.getText()));
            purchaseDTO.setCost(Float.valueOf(formattedTextFieldCost.getText()));
            purchaseDTO.setPurchaseId(Integer.parseInt(formattedTextFieldSearchID.getText()));
            //Convert product/supplier name to product/supplier id
            purchaseDTO.setProductId(new PurchaseDAO().getProductID((String) comboBoxProduct.getSelectedItem()));
            purchaseDTO.setSupplierId(new PurchaseDAO().getSupplierID((String) comboBoxSupplier.getSelectedItem()));
            return purchaseDTO;
        }catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Please input correct information");
            System.out.println(e.getMessage());
        }
        return null;
    }

    private PurchaseDTO transferTextFieldToPurchaseDTOWithoutSearchID() throws SQLException {

        try {
            PurchaseDTO purchaseDTO = new PurchaseDTO();

            purchaseDTO.setDate(Date.valueOf(formattedTextFieldDate.getText()));
            purchaseDTO.setQuantity(Integer.parseInt(formattedTextFieldQuantity.getText()));
            purchaseDTO.setPrice(Float.valueOf(formattedTextFieldPrice.getText()));
            purchaseDTO.setCost(Float.valueOf(formattedTextFieldCost.getText()));
            //Convert product/supplier name to product/supplier id
            purchaseDTO.setProductId(new PurchaseDAO().getProductID((String) comboBoxProduct.getSelectedItem()));
            purchaseDTO.setSupplierId(new PurchaseDAO().getSupplierID((String) comboBoxSupplier.getSelectedItem()));
            return purchaseDTO;
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
        formattedTextFieldCost.setText("");
        formattedTextFieldSearchID.setText("");
        comboBoxProduct.setSelectedItem("");
        comboBoxSupplier.setSelectedItem("");
    }

}