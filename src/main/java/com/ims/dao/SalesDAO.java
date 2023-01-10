package com.ims.dao;

import com.ims.database.DBConnection;
import com.ims.dto.SalesDTO;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SalesDAO {

    Connection conn;
    Statement stmt;
    PreparedStatement pstmt;
    ResultSet rs;

    public SalesDAO() throws SQLException {
        conn = new DBConnection().getDBConnection();
        stmt = conn.createStatement();
    }

    public void addSales(SalesDTO salesDTO) {
        try {
            String query = "SELECT * FROM sell WHERE date = '" + salesDTO.getDate() + "' AND product_id = " +
                    salesDTO.getProductId() + " AND quantity = " + salesDTO.getQuantity() + " AND price = " +
                    salesDTO.getPrice() + " AND revenue = " + salesDTO.getRevenue() + " AND  customer_id = " +
                    salesDTO.getCustomerId();

            rs = stmt.executeQuery(query);
            if (rs.next())
                JOptionPane.showMessageDialog(null, "Sales record already exist");
            else
                addSalesSQL(salesDTO);
        }catch (SQLException e) {
            System.out.println("Add sales record failed " + e.getMessage());
        }
    }

    private void addSalesSQL(SalesDTO salesDTO) {
        try {
            String query = "INSERT INTO sell (date, product_id, quantity, price, revenue, customer_id) " +
                    "VALUE (?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(query);
            pstmt.setDate(1, salesDTO.getDate());
            pstmt.setInt(2, salesDTO.getProductId());
            pstmt.setInt(3, salesDTO.getQuantity());
            pstmt.setFloat(4, salesDTO.getPrice());
            pstmt.setFloat(5, salesDTO.getRevenue());
            pstmt.setInt(6, salesDTO.getCustomerId());
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Sales record added");


            //get current stock
            String queryStock = "SELECT stock FROM stock WHERE product_id = ?";
            pstmt = conn.prepareStatement(queryStock);
            pstmt.setInt(1, salesDTO.getProductId());
            rs = this.pstmt.executeQuery();
            int currentStock = 0;
            if (rs.next())
                currentStock = rs.getInt(1);

            //After sales added, get new stock and update into stock table
            String queryUpdateStock = "UPDATE stock SET stock = ? WHERE product_id = ?";
            pstmt = conn.prepareStatement(queryUpdateStock);
            pstmt.setInt(1, (currentStock - salesDTO.getQuantity()));
            pstmt.setInt(2, salesDTO.getProductId());
            pstmt.executeUpdate();
        }catch (SQLException e) {
            System.out.println("Sales record add failed " + e.getMessage());
        }
    }

    public void updateSales(SalesDTO salesDTO) {
        try {
            //get quantity before update
            String queryQuantity = "SELECT quantity FROM sell WHERE sale_id = ?";
            pstmt = conn.prepareStatement(queryQuantity);
            pstmt.setInt(1, salesDTO.getSaleId());
            rs = pstmt.executeQuery();
            int updatedPurchaseQuantity = 0;
            int currentPurchaseQuantity = 0;
            if (rs.next()) {
                currentPurchaseQuantity = rs.getInt(1);
                //get quantity after update
                updatedPurchaseQuantity = salesDTO.getQuantity();
            }

            String query = "UPDATE sell SET date = ?, product_id = ?, quantity = ?, price = ?, revenue = ?, customer_id = ?" +
                    " WHERE sale_id = ?";

            pstmt = conn.prepareStatement(query);
            pstmt.setDate(1, salesDTO.getDate());
            pstmt.setInt(2, salesDTO.getProductId());
            pstmt.setInt(3, salesDTO.getQuantity());
            pstmt.setFloat(4, salesDTO.getPrice());
            pstmt.setFloat(5, salesDTO.getRevenue());
            pstmt.setInt(6, salesDTO.getCustomerId());
            pstmt.setInt(7, salesDTO.getSaleId());
            var result = pstmt.executeUpdate();

            if (result == 1) {
                JOptionPane.showMessageDialog(null, "Sales record updated successfully");
            } else {
                JOptionPane.showMessageDialog(null, "Wrong sales ID");
            }


            //get current stock
            String queryStock = "SELECT stock FROM stock WHERE product_id = ?";
            pstmt = conn.prepareStatement(queryStock);
            pstmt.setInt(1, salesDTO.getProductId());
            rs = this.pstmt.executeQuery();
            int currentStock = 0;
            if (rs.next())
                currentStock = rs.getInt(1);

            //After sales updated, get new stock and update into stock table
            int updatedStock = currentStock - (updatedPurchaseQuantity - currentPurchaseQuantity);
            String queryUpdateStock = "UPDATE stock SET stock = ? WHERE product_id = ?";
            pstmt = conn.prepareStatement(queryUpdateStock);
            pstmt.setInt(1, updatedStock);
            pstmt.setInt(2, salesDTO.getProductId());
            pstmt.executeUpdate();

        }catch (SQLException e) {
            System.out.println("Update failed: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Select a row or search a record first");
        }
    }

    public void deleteSales(int salesID) {
        try {
            //Get product ID and quantity for the record which is about to be deleted
            String queryIDAndQuantity = "SELECT product_id, quantity FROM sell WHERE sale_id = ?";
            pstmt = conn.prepareStatement(queryIDAndQuantity);
            pstmt.setInt(1, salesID);
            rs = pstmt.executeQuery();
            int currentProductID = 0;
            int currentQuantity = 0;
            if (rs.next()) {
                currentProductID = rs.getInt(1);
                currentQuantity = rs.getInt(2);
            }


            String query = "DELETE FROM sell WHERE sale_id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, salesID);
            var result = pstmt.executeUpdate();

            if (result == 1) {
                JOptionPane.showMessageDialog(null, "Sales record deleted");
            } else
                JOptionPane.showMessageDialog(null, "No sales record found");

            //get current stock
            String queryStock = "SELECT stock FROM stock WHERE product_id = ?";
            pstmt = conn.prepareStatement(queryStock);
            pstmt.setInt(1, currentProductID);
            rs = this.pstmt.executeQuery();
            int currentStock = 0;
            if (rs.next())
                currentStock = rs.getInt(1);

            //After sales deleted, get new stock and update into stock table
            String queryUpdateStock = "UPDATE stock SET stock = ? WHERE product_id = ?";
            pstmt = conn.prepareStatement(queryUpdateStock);
            pstmt.setInt(1, (currentStock + currentQuantity));
            pstmt.setInt(2, currentProductID);
            pstmt.executeUpdate();
        }catch (SQLException e) {
            System.out.println("Delete failed " + e.getMessage());
        }
    }

    public List<String> searchSalesID(int salesID) {
        try {
            String query = "SELECT date, product_name, quantity, price, revenue, customer_name FROM sell_view WHERE sale_id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, salesID);
            rs = pstmt.executeQuery();

            List<String> list = new ArrayList<>();
            if (rs.next()) {
                list.add(rs.getString(1));
                list.add(rs.getString(2));
                list.add(String.valueOf(rs.getInt(3)));
                list.add(String.valueOf(rs.getFloat(4)));
                list.add(String.valueOf(rs.getFloat(5)));
                list.add(rs.getString(6));
                return list;
            }
            else
                return null;
        }catch (SQLException e) {
            System.out.println("Search ID failed " + e.getMessage());
        }
        return null;
    }


    public int getCustomerID(String customerName) {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("Steven", 1);
        map.put("James", 2);
        map.put("Kaka", 3);
        map.put("Parker", 5);
        map.put("Sabrina", 6);
        return map.get(customerName);
    }

    public int getProductID(String productName) {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("iPhone14", 1);
        map.put("Nest Camera", 2);
        map.put("Macbook Pro", 3);
        map.put("Apple Watch Serial 4", 4);
        map.put("Playstation 5", 6);
        map.put("XBOX One", 7);
        map.put("NetTV", 8);
        map.put("Meta Device", 9);
        return map.get(productName);
    }
}


