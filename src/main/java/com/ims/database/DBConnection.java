package com.ims.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String DB_Name = "ims_1.0";
    private static final String Connection_Str = "jdbc:mysql://localhost:3306/" + DB_Name;
    private static final String User_Name = "root";
    private static final String Password = "woaibaobao1984";
    private Connection conn;

    public Connection getDBConnection() {
        try {
            conn = DriverManager.getConnection(Connection_Str, User_Name, Password);
            System.out.println("Database is connected");
        }catch (SQLException e) {
            System.out.println("Can not connect to database" + e.getMessage());
        }
        return conn;
    }
}
