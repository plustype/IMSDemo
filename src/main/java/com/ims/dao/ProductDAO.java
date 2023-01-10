package com.ims.dao;

import com.ims.database.DBConnection;
import com.ims.dto.ProductDTO;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProductDAO {
    Connection conn;
    Statement stmt;
    PreparedStatement pstmt;
    ResultSet rs;

    public ProductDAO() throws SQLException {
        conn = new DBConnection().getDBConnection();
        stmt = conn.createStatement();
    }

    public void addProductDAO(ProductDTO productDTO) {
        try {
            String query = "SELECT * FROM product WHERE product_name = '" + productDTO.getProductName() +
                    "' AND supplier_id = " + productDTO.getSupplierId();

            rs = stmt.executeQuery(query);

            if (rs.next())
                JOptionPane.showMessageDialog(null, "Product already exist");
            else
                addProductSQL(productDTO);
        }catch (SQLException e) {
            System.out.println("Add product failed " + e.getMessage());
        }
    }

    private void addProductSQL(ProductDTO productDTO) {
        try {
            String query = "INSERT INTO product (product_name, supplier_id) VALUE (?, ?)";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1,productDTO.getProductName());
            pstmt.setInt(2, productDTO.getSupplierId());
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Product added");

            //Get product ID which is just added for new product
            String queryGetProductID = "SELECT product_id FROM product WHERE product_name = ?";
            pstmt = conn.prepareStatement(queryGetProductID);
            pstmt.setString(1, productDTO.getProductName());
            rs = pstmt.executeQuery();
            int currentProductID = 0;
            if (rs.next())
                currentProductID = rs.getInt(1);

            //Create a stock record in stock table for new added product, the stock quantity is 0
            String queryInsertProductToStock = "INSERT INTO stock (product_id, stock) VALUE (" + currentProductID + ", 0)";
            System.out.println(queryInsertProductToStock);
            stmt.executeUpdate(queryInsertProductToStock);
        }catch (SQLException e) {
            System.out.println("Product add failed " + e.getMessage());
        }
    }

    public void updateProduct(ProductDTO productDTO) {
        try {
            String query = "UPDATE product SET product_name = ?, supplier_id = ? WHERE product_id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, productDTO.getProductName());
            pstmt.setInt(2, productDTO.getSupplierId());
            pstmt.setInt(3, productDTO.getProductId());

            var result = pstmt.executeUpdate();
            if (result == 1)
                JOptionPane.showMessageDialog(null, "Update successfully");
            else
                JOptionPane.showMessageDialog(null, "Wrong product ID");
        }catch (SQLException e) {
            System.out.println("Update failed " + e.getMessage());
            System.out.println("Select a row or search a record first");
        }
    }

    public List<String> searchProductID(int productID) {
        try {
            String query = "SELECT product_name, supplier_id FROM product WHERE product_id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, productID);
            rs = pstmt.executeQuery();

            List<String> list = new ArrayList<>();
            if (rs.next()) {
                list.add(rs.getString(1));
                list.add(String.valueOf(rs.getInt(2)));
                return list;
            }
            else
                return null;
        }catch (SQLException e) {
            System.out.println("Search ID failed " + e.getMessage());
        }
        return null;
    }

    public void deleteProduct(int productID) {
        try {
            String query = "DELETE FROM product WHERE product_id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, productID);
            var result = pstmt.executeUpdate();

            if (result == 1)
                JOptionPane.showMessageDialog(null, "Product deleted");
            else
                JOptionPane.showMessageDialog(null, "No product found");
        }catch (SQLException e) {
            System.out.println("Delete failed " + e.getMessage());
        }
    }

}
