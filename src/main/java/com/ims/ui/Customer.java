package com.ims.ui;

import com.ims.dao.CustomerDAO;
import com.ims.dao.SupplierDAO;
import com.ims.database.DBConnection;
import com.ims.dto.CustomerDTO;
import com.ims.dto.SupplierDTO;
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

public class Customer {
    private JTable tableCustomer;
    private JPanel panelCustomer;
    private JLabel labelTitle;
    private JTextField textFieldName;
    private JTextField textFieldCountry;
    private JTextField textFieldState;
    private JTextField textFieldAddress;
    private JTextField textFieldEmail;
    private JButton buttonSearchID;
    private JTextField textFieldSearchID;
    private JButton buttonAdd;
    private JButton buttonUpdate;
    private JButton buttonDelete;
    private JButton buttonClear;
    private JButton buttonHome;
    private JLabel labelName;
    private JLabel labelState;
    private JLabel labelCountry;
    private JLabel labelAddress;
    private JLabel labelEmail;

    private Connection conn;
    private PreparedStatement loadTable;

    JFrame frame = new JFrame();

    public Customer() {

        frame.setContentPane(panelCustomer);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1024,768);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        conn = new DBConnection().getDBConnection();
        tableLoad();
        tableCustomer.addMouseListener(new MouseAdapter() {
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
        buttonHome.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clickBackButton();
            }
        });
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
    }

    private void tableLoad() {
        try {
            loadTable = conn.prepareStatement("SELECT * FROM customer" );
            ResultSet results = loadTable.executeQuery();
            tableCustomer.setModel(DbUtils.resultSetToTableModel(results));
        }catch (SQLException e) {
            System.out.println("Couldn't load table" + e.getMessage());
        }
    }

    private void clickTableRow() {
        int row = tableCustomer.getSelectedRow();
        int column = tableCustomer.getColumnCount();
        Object[] val = new Object[column];
        for(int i = 0; i < column; i++) {
            val[i]=tableCustomer.getValueAt(row, i);
        }

        textFieldSearchID.setText(val[0].toString());
        textFieldName.setText((String) val[1]);
        textFieldCountry.setText((String) val[2]);
        textFieldState.setText((String) val[3]);
        textFieldAddress.setText((String) val[4]);
        textFieldEmail.setText((String) val[5]);
    }

    private void clickBackButton() {
        frame.dispose();
        new Dashboard();
    }

    private void clickAddButton() throws SQLException {
        if (checkAllTextFieldsAreFilled())
            JOptionPane.showMessageDialog(null, "Please fill all the information");
        else {
            new CustomerDAO().addCustomerDAO(transferTextFieldToCustomerDTOWithoutSearchID());
            clearTextFields();
            tableLoad();
        }
    }

    private void clickSearchButton() throws SQLException {

        try {
            var list = new CustomerDAO().searchCustomerID(Integer.parseInt(textFieldSearchID.getText()));


            if (list != null) {
                textFieldName.setText(list.get(0));
                textFieldCountry.setText(list.get(1));
                textFieldState.setText(list.get(2));
                textFieldAddress.setText(list.get(3));
                textFieldEmail.setText(list.get(4));
            } else {
                clearTextFields();
                JOptionPane.showMessageDialog(null, "Invalid Supplier ID");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "Only number is allowed for 'Search ID' button");
            clearTextFields();
        }
    }

    private void clickUpdateButton() throws SQLException {
        try {
            if (checkAllTextFieldsAreFilled())
                JOptionPane.showMessageDialog(null, "Please fill all the information");
            else {
                new CustomerDAO().updateCustomer(transferTextFieldToCustomerDTO());
                clearTextFields();
                tableLoad();
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "Only number is allowed for 'Search ID' button");
            clearTextFields();
        }
    }

    private void clickDeleteButton() throws SQLException {
        try {
            new CustomerDAO().deleteCustomer(Integer.parseInt(textFieldSearchID.getText()));
            clearTextFields();
            tableLoad();
        }catch (Exception e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "Only number is allowed for 'Search ID' button");
            clearTextFields();
        }
    }

    private boolean checkAllTextFieldsAreFilled() {
        return textFieldName.getText().equals("") || textFieldCountry.getText().equals("") || textFieldState.getText().equals("")
                || textFieldAddress.getText().equals("") || textFieldEmail.getText().equals("");
    }

    private CustomerDTO transferTextFieldToCustomerDTO() {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setCustomerId(Integer.parseInt(textFieldSearchID.getText()));
        customerDTO.setCustomerName(textFieldName.getText());
        customerDTO.setCountry(textFieldCountry.getText());
        customerDTO.setState(textFieldState.getText());
        customerDTO.setAddress(textFieldAddress.getText());
        customerDTO.setEmail(textFieldEmail.getText());
        return customerDTO;
    }

    private CustomerDTO transferTextFieldToCustomerDTOWithoutSearchID() {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setCustomerName(textFieldName.getText());
        customerDTO.setCountry(textFieldCountry.getText());
        customerDTO.setState(textFieldState.getText());
        customerDTO.setAddress(textFieldAddress.getText());
        customerDTO.setEmail(textFieldEmail.getText());
        return customerDTO;
    }

    private void clearTextFields() {
        textFieldName.setText("");
        textFieldCountry.setText("");
        textFieldState.setText("");
        textFieldAddress.setText("");
        textFieldEmail.setText("");
        textFieldSearchID.setText("");
    }
}

