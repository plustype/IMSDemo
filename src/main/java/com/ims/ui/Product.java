package com.ims.ui;

import com.ims.dao.ProductDAO;
import com.ims.dao.SupplierDAO;
import com.ims.database.DBConnection;
import com.ims.dto.ProductDTO;
import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Product {
    private JTable tableProduct;
    private JLabel labelTitle;
    private JPanel product;
    private JTextField textName;
    private JTextField textSupplierID;
    private JButton buttonSearchID;
    private JTextField textSearchProductID;
    private JButton buttonAdd;
    private JButton buttonUpdate;
    private JButton buttonDelete;
    private JButton buttonClear;
    private JButton buttonHome;
    private JLabel labelName;
    private JLabel labelSupplierID;

    private Connection conn;
    private PreparedStatement loadTable;
    private PreparedStatement pstmt;

    JFrame frame = new JFrame();

    public void loadTable() {
        try {
            loadTable = conn.prepareStatement("SELECT * FROM product_view");
            ResultSet results = loadTable.executeQuery();

            tableProduct.setModel(DbUtils.resultSetToTableModel(results));
        }catch (SQLException e) {
            System.out.println("Could not load table " + e.getMessage());
        }
    }

    public Product() {
        frame.setContentPane(product);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        conn = new DBConnection().getDBConnection();
        loadTable();

        tableProduct.addMouseListener(new MouseAdapter() {
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
                try {
                    clickDeleteButton();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });

        buttonClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearTextFields();
            }
        });

        buttonSearchID.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    clickSearchIDButton();
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

    private List<String> updateComboBox() {
        String query = "SELECT supplier_id, supplier name FROM supplier";
        try {

            conn = new DBConnection().getDBConnection();
            List<String> list = new ArrayList<>();
            pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("supplier_name"));
            }
            return list;
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }return null;
    }

    private void clickAddButton() throws SQLException {

            if (!checkAllTextFieldsAreFilled())
                JOptionPane.showMessageDialog(null, "Please fill all the information");
            else {
                if (transferTextFieldToProductDTOWithoutSearchID() == null)
                    return;
                new ProductDAO().addProductDAO(transferTextFieldToProductDTOWithoutSearchID());
                clearTextFields();
                loadTable();
            }

    }

    private void clickUpdateButton() throws SQLException {
        try {
            if (!checkAllTextFieldsAreFilled())
                JOptionPane.showMessageDialog(null, "Please fill all the information");
            else
                new ProductDAO().updateProduct(transferTextFieldToProductDTO());
            clearTextFields();
            loadTable();
        }catch (Exception e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "Only Integer is allowed for ID");
            clearTextFields();
        }
    }

    private void clickDeleteButton() throws SQLException {
        try {
            new ProductDAO().deleteProduct(Integer.parseInt(textSearchProductID.getText()));
            clearTextFields();
            loadTable();
        }catch (Exception e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "Only Integer is allowed for Search ID");
            clearTextFields();
        }
    }

    private void clickSearchIDButton() throws SQLException {

        try {
            var list = new ProductDAO().searchProductID(Integer.parseInt(textSearchProductID.getText()));
            if (list != null) {
                textName.setText(list.get(0));
                textSupplierID.setText(list.get(1));
            }
            else {
                clearTextFields();
                JOptionPane.showMessageDialog(null, "Invalid product ID");
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "Only Integer is allowed for Search ID");
            clearTextFields();
        }


    }

    private void clickTableRow() {
        int row = tableProduct.getSelectedRow();
        int column = tableProduct.getColumnCount();
        Object[] val = new Object[column];
        for(int i = 0; i < column; i++) {
            val[i]=tableProduct.getValueAt(row, i);
        }
            textSearchProductID.setText(val[0].toString());
            textName.setText((String) val[1]);
            textSupplierID.setText(val[2].toString());
        }


    private boolean checkAllTextFieldsAreFilled() {
        return !textName.getText().equals("") && !textSupplierID.getText().equals("");
    }

    private ProductDTO transferTextFieldToProductDTO() {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductName(textName.getText());
        productDTO.setSupplierId(Integer.parseInt(textSupplierID.getText()));
        productDTO.setProductId(Integer.parseInt(textSearchProductID.getText()));
        return productDTO;
    }

    private ProductDTO transferTextFieldToProductDTOWithoutSearchID() {
        try {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setProductName(textName.getText());
            productDTO.setSupplierId(Integer.parseInt(textSupplierID.getText()));
            return productDTO;
        }catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Only Integer is allow for ID");
            System.out.println(e.getMessage());
        }
        return null;
    }

    private void clearTextFields() {
        textName.setText("");
        textSupplierID.setText("");
        textSearchProductID.setText("");
    }
}
