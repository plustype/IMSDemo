package com.ims.dao;

import com.ims.database.DBConnection;
import com.ims.dto.CustomerDTO;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    Connection conn;
    Statement stmt;
    PreparedStatement pstmt;
    ResultSet rs;



    public CustomerDAO() throws SQLException {
        conn = new DBConnection().getDBConnection();
        stmt = conn.createStatement();
    }

    public void addCustomerDAO(CustomerDTO customerDTO) {
        try {
            String query = "SELECT * FROM customer WHERE customer_name = '" + customerDTO.getCustomerName() +
                    "' AND country = '" + customerDTO.getCountry() + "' AND state = '" + customerDTO.getState() +
                    "' AND address = '" + customerDTO.getAddress() + "' AND email = '" + customerDTO.getEmail() + "'";
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next())
                JOptionPane.showMessageDialog(null, "Customer already exist");
            else
                addCustomerSQL(customerDTO);
        }catch (SQLException e) {
            System.out.println("Add customer failed " + e.getMessage());
        }
    }

    public void addCustomerSQL(CustomerDTO customerDTO) {
        try {
            String query = "INSERT INTO customer (customer_name, country, state, address, email) VALUE (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, customerDTO.getCustomerName());
            pstmt.setString(2, customerDTO.getCountry());
            pstmt.setString(3,customerDTO.getState());
            pstmt.setString(4, customerDTO.getAddress());
            pstmt.setString(5, customerDTO.getEmail());
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Customer Added");
        }catch (SQLException e) {
            System.out.println("Customer add failed " + e.getMessage());
        }
    }

    public List<String> searchCustomerID(int customerID) {
        try {
            String query = "SELECT customer_name, country, state, address, email FROM customer WHERE customer_id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, customerID);
            rs = pstmt.executeQuery();

            List<String> list = new ArrayList<>();
            if (rs.next()) {
                list.add(rs.getString(1));
                list.add(rs.getString(2));
                list.add(rs.getString(3));
                list.add(rs.getString(4));
                list.add(rs.getString(5));
                return list;
            }
            else
                return null;
        }catch (SQLException e) {
            System.out.println("Search Customer ID failed " + e.getMessage());
        }
        return null;
    }

    public void updateCustomer(CustomerDTO customerDTO) {
        try {
            String query = "UPDATE customer SET customer_name = ?, country = ?, state = ?, address = ?, email = ? " +
                    "WHERE customer_id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, customerDTO.getCustomerName());
            pstmt.setString(2,customerDTO.getCountry());
            pstmt.setString(3,customerDTO.getState());
            pstmt.setString(4,customerDTO.getAddress());
            pstmt.setString(5,customerDTO.getEmail());
            pstmt.setInt(6,customerDTO.getCustomerId());
            var result = pstmt.executeUpdate();
            if (result == 1)
                JOptionPane.showMessageDialog(null, "Update successfully");
            else
                JOptionPane.showMessageDialog(null, "Wrong customer ID");
        }catch (SQLException e) {
            System.out.println("Update failed " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Select a row or search a record first");
        }
    }

    public void deleteCustomer(int customerId) {
        try {
            String query = "DELETE FROM customer WHERE customer_id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, customerId);
            var result = pstmt.executeUpdate();

            if (result == 1)
                JOptionPane.showMessageDialog(null, "Customer deleted");
            else
                JOptionPane.showMessageDialog(null, "No customer found");
        }catch (SQLException e) {
            System.out.println("Delete failed " +e.getMessage());
        }
    }
}
