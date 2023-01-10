package com.ims.dao;

import com.ims.database.DBConnection;

import java.sql.*;

public class UserDAO {
    Connection conn;
    Statement stmt;
    PreparedStatement pstmt;


    public UserDAO() throws SQLException {
        conn = new DBConnection().getDBConnection();
        stmt = conn.createStatement();
    }

    public boolean checkLogin(String username, String password, String userType) {

//        String query = "SELECT * FROM user WHERE username = '" + username + "' AND password = '" + password +
//                "' AND type = '" + userType + "';";
//        try {
//            ResultSet rs = stmt.executeQuery(query);
//            if (rs.next()) {
//                return true;
//            }
//        }catch (SQLException e) {
//            System.out.println("Login failed" + e.getMessage());
//        }return false;

        return true;
    }
}


