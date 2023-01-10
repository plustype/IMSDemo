package com.ims.dao;


import com.ims.database.DBConnection;
import com.ims.dto.PurchaseDTO;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PurchaseDAO {
   Connection conn;
   Statement stmt;
   PreparedStatement pstmt;
   ResultSet rs;

   public PurchaseDAO() throws SQLException {
       conn = new DBConnection().getDBConnection();
       stmt = conn.createStatement();
   }

   public void addPurchase(PurchaseDTO purchaseDTO) {
      try {
         String query = "SELECT * FROM purchase WHERE date = '" + purchaseDTO.getDate() + "' AND product_id = " +
                 purchaseDTO.getProductId() + " AND quantity = " + purchaseDTO.getQuantity() + " AND price = " +
                 purchaseDTO.getPrice() + " AND cost = " + purchaseDTO.getCost() + " AND  supplier_id = " +
                 purchaseDTO.getSupplierId();

         rs = stmt.executeQuery(query);
         if (rs.next())
            JOptionPane.showMessageDialog(null, "Purchase record already exist");
         else
            addPurchaseSQL(purchaseDTO);
      }catch (SQLException e) {
         System.out.println("Add purchase record failed " + e.getMessage());
      }
   }

   private void addPurchaseSQL(PurchaseDTO purchaseDTO) {
      try {
         String query = "INSERT INTO purchase (date, product_id, quantity, price, cost, supplier_id) " +
                 "VALUE (?, ?, ?, ?, ?, ?)";
         pstmt = conn.prepareStatement(query);
         pstmt.setDate(1, purchaseDTO.getDate());
         pstmt.setInt(2, purchaseDTO.getProductId());
         pstmt.setInt(3, purchaseDTO.getQuantity());
         pstmt.setFloat(4, purchaseDTO.getPrice());
         pstmt.setFloat(5, purchaseDTO.getCost());
         pstmt.setInt(6, purchaseDTO.getSupplierId());
         pstmt.executeUpdate();
         JOptionPane.showMessageDialog(null, "Purchase record added");


         //get current stock
         String queryStock = "SELECT stock FROM stock WHERE product_id = ?";
         pstmt = conn.prepareStatement(queryStock);
         pstmt.setInt(1, purchaseDTO.getProductId());
         rs = this.pstmt.executeQuery();
         int currentStock = 0;
         if (rs.next())
            currentStock = rs.getInt(1);

         //After purchase updated, get new stock and update into stock table
         String queryUpdateStock = "UPDATE stock SET stock = ? WHERE product_id = ?";
         pstmt = conn.prepareStatement(queryUpdateStock);
         pstmt.setInt(1, (currentStock + purchaseDTO.getQuantity()));
         pstmt.setInt(2, purchaseDTO.getProductId());
         pstmt.executeUpdate();
      }catch (SQLException e) {
         System.out.println("Purchase record add failed " + e.getMessage());
      }
   }

   public void updatePurchase(PurchaseDTO purchaseDTO) {
      try {
         //get quantity before update
         String queryQuantity = "SELECT quantity FROM purchase WHERE purchase_id = ?";
         pstmt = conn.prepareStatement(queryQuantity);
         pstmt.setInt(1, purchaseDTO.getPurchaseId());
         rs = pstmt.executeQuery();
         int updatedPurchaseQuantity = 0;
         int currentPurchaseQuantity = 0;
         if (rs.next()) {
            currentPurchaseQuantity = rs.getInt(1);
            //get quantity after update
            updatedPurchaseQuantity = purchaseDTO.getQuantity();
         }

         String query = "UPDATE purchase SET date = ?, product_id = ?, quantity = ?, price = ?, cost = ?, supplier_id = ?" +
                 " WHERE purchase_id = ?";

         pstmt = conn.prepareStatement(query);
         pstmt.setDate(1, purchaseDTO.getDate());
         pstmt.setInt(2, purchaseDTO.getProductId());
         pstmt.setInt(3, purchaseDTO.getQuantity());
         pstmt.setFloat(4, purchaseDTO.getPrice());
         pstmt.setFloat(5, purchaseDTO.getCost());
         pstmt.setInt(6, purchaseDTO.getSupplierId());
         pstmt.setInt(7, purchaseDTO.getPurchaseId());
         var result = pstmt.executeUpdate();

         if (result == 1) {
            JOptionPane.showMessageDialog(null, "Purchase record updated successfully");
         } else {
            JOptionPane.showMessageDialog(null, "Wrong purchase ID");
         }


         //get current stock
         String queryStock = "SELECT stock FROM stock WHERE product_id = ?";
         pstmt = conn.prepareStatement(queryStock);
         pstmt.setInt(1, purchaseDTO.getProductId());
         rs = this.pstmt.executeQuery();
         int currentStock = 0;
         if (rs.next())
            currentStock = rs.getInt(1);

         //After purchase updated, get new stock and update into stock table
         int updatedStock = currentStock + (updatedPurchaseQuantity - currentPurchaseQuantity);
         String queryUpdateStock = "UPDATE stock SET stock = ? WHERE product_id = ?";
         pstmt = conn.prepareStatement(queryUpdateStock);
         pstmt.setInt(1, updatedStock);
         pstmt.setInt(2, purchaseDTO.getProductId());
         pstmt.executeUpdate();

      }catch (SQLException e) {
         System.out.println("Update failed: " + e.getMessage());
         JOptionPane.showMessageDialog(null, "Select a row or search a record first");
      }
   }

   public void deletePurchase(int purchaseID) {
      try {
         //Get product ID and quantity for the record which is about to be deleted
         String queryIDAndQuantity = "SELECT product_id, quantity FROM purchase WHERE purchase_id = ?";
         pstmt = conn.prepareStatement(queryIDAndQuantity);
         pstmt.setInt(1, purchaseID);
         rs = pstmt.executeQuery();
         int currentProductID = 0;
         int currentQuantity = 0;
         if (rs.next()) {
            currentProductID = rs.getInt(1);
            currentQuantity = rs.getInt(2);
         }


         String query = "DELETE FROM purchase WHERE purchase_id = ?";
         pstmt = conn.prepareStatement(query);
         pstmt.setInt(1, purchaseID);
         var result = pstmt.executeUpdate();

         if (result == 1) {
            JOptionPane.showMessageDialog(null, "Purchase record deleted");
         } else
            JOptionPane.showMessageDialog(null, "No purchase record found");

         //get current stock
         String queryStock = "SELECT stock FROM stock WHERE product_id = ?";
         pstmt = conn.prepareStatement(queryStock);
         pstmt.setInt(1, currentProductID);
         rs = this.pstmt.executeQuery();
         int currentStock = 0;
         if (rs.next())
            currentStock = rs.getInt(1);

         //After purchase updated, get new stock and update into stock table
         String queryUpdateStock = "UPDATE stock SET stock = ? WHERE product_id = ?";
         pstmt = conn.prepareStatement(queryUpdateStock);
         pstmt.setInt(1, (currentStock - currentQuantity));
         pstmt.setInt(2, currentProductID);
         pstmt.executeUpdate();
      }catch (SQLException e) {
         System.out.println("Delete failed " + e.getMessage());
      }
   }

   public List<String> searchPurchaseID(int purchaseID) {
      try {
         String query = "SELECT date, product_name, quantity, price, cost, supplier_name FROM purchase_view WHERE purchase_id = ?";
         System.out.println(query);
         pstmt = conn.prepareStatement(query);
         pstmt.setInt(1, purchaseID);
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

   public int getSupplierID(String supplierName) {
      HashMap<String, Integer> map = new HashMap<>();
      map.put("Apple", 1);
      map.put("Google", 2);
      map.put("Meta", 3);
      map.put("Netflix", 4);
      map.put("SONY", 11);
      map.put("Microsoft", 12);
      return map.get(supplierName);
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
