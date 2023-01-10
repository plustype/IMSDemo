package com.ims.dao;

import com.ims.database.DBConnection;
import com.ims.dto.SupplierDTO;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {
    Connection conn;
    Statement stmt;
    PreparedStatement pstmt;
    ResultSet rs;


    public SupplierDAO() throws SQLException {
        conn = new DBConnection().getDBConnection();
        stmt = conn.createStatement();
    }

    public void addSupplierDAO(SupplierDTO supplierDTO) {
        try {
            String query = "SELECT * FROM supplier WHERE supplier_name = '" + supplierDTO.getSupplierName() +
                    "' AND country = '" + supplierDTO.getCountry() + "' AND state = '" + supplierDTO.getState() +
                    "' AND address = '" + supplierDTO.getAddress() + "' AND email = '" + supplierDTO.getEmail() + "'";
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next())
                JOptionPane.showMessageDialog(null, "Supplier already exist");
            else
                addSupplierSQL(supplierDTO);
        }catch (SQLException e) {
            System.out.println("Add supplier failed " + e.getMessage());
        }
    }

    private void addSupplierSQL(SupplierDTO supplierDTO) {
        try {
            String query = "INSERT INTO supplier (supplier_name, country, state, address, email) VALUE (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, supplierDTO.getSupplierName());
            pstmt.setString(2, supplierDTO.getCountry());
            pstmt.setString(3,supplierDTO.getState());
            pstmt.setString(4, supplierDTO.getAddress());
            pstmt.setString(5, supplierDTO.getEmail());
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Supplier Added");
        }catch (SQLException e) {
            System.out.println("Supplier add failed " + e.getMessage());
        }
    }

    public List<String> searchSupplierID(int supplierID) {
        try {
            String query = "SELECT supplier_name, country, state, address, email FROM supplier WHERE supplier_id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, supplierID);
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
            System.out.println("Search Supplier ID failed " + e.getMessage());
        }
        return null;
    }

    public void updateSupplier(SupplierDTO supplierDTO) {
        try {
            String query = "UPDATE supplier SET supplier_name = ?, country = ?, state = ?, address = ?, email = ? " +
                    "WHERE supplier_id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, supplierDTO.getSupplierName());
            pstmt.setString(2,supplierDTO.getCountry());
            pstmt.setString(3,supplierDTO.getState());
            pstmt.setString(4,supplierDTO.getAddress());
            pstmt.setString(5,supplierDTO.getEmail());
            pstmt.setInt(6,supplierDTO.getSupplierId());
            var result = pstmt.executeUpdate();

            if (result == 1)
                JOptionPane.showMessageDialog(null, "Update successfully");
            else
                JOptionPane.showMessageDialog(null, "Wrong supplier ID");

        }catch (SQLException e) {
            System.out.println("Update failed " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Select a row or search a record first");
        }
    }

    public void deleteSupplier(int supplierId) {
        try {
            String query = "DELETE FROM supplier WHERE supplier_id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, supplierId);
            var result = pstmt.executeUpdate();

            if (result == 1)
                JOptionPane.showMessageDialog(null, "Supplier deleted");
            else
                JOptionPane.showMessageDialog(null, "No supplier found");
        }catch (SQLException e) {
            System.out.println("Delete failed " +e.getMessage());
        }
    }
}
