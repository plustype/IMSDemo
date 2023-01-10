package com.ims.dao;

import com.ims.database.DBConnection;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StockDAO {
    Connection conn;
    Statement stmt;
    PreparedStatement pstmt;
    ResultSet rs;


    public StockDAO() throws SQLException {
        conn = new DBConnection().getDBConnection();
        stmt = conn.createStatement();
    }

    public List<String> searchProductID(int productID) {
        try {
            String query = "SELECT product_name, stock FROM stock_view WHERE product_id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, productID);
            rs = pstmt.executeQuery();

            List<String> list = new ArrayList<>();
            if (rs.next()) {
                list.add(rs.getString(1));
                list.add(String.valueOf(rs.getInt(2)));
                return list;
            }
            else {
                return null;
            }
        }catch (SQLException e) {
            System.out.println("Search product ID failed " + e.getMessage());
        }
        return null;
    }
}
