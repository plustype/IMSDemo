package com.ims.dao;

import com.ims.database.DBConnection;
import com.ims.dto.CustomerDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class CustomerDAOTest {

    CustomerDAO customerDAO = new CustomerDAO();
    Connection conn = new DBConnection().getDBConnection();
    Statement stmt = conn.createStatement();
    PreparedStatement pstmt;
    ResultSet rs;

    CustomerDAOTest() throws SQLException {
    }

    @BeforeEach
    void beforeEach() throws SQLException {

    }
    @Test
    void addCustomerDAO() throws SQLException {
        try {
        String sql = "SELECT * FROM customer";
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
            int id = rs.getInt("customer_id");
            String name = rs.getString("customer_name");
            String country = rs.getString("country");
            System.out.println("id = " + id + "| name = " + name + "| country = " + country);
        }
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void addCustomerSQL() {
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 2, 3 })
    void testWithValueSource(int argument) {
        assertTrue(argument > 0 && argument < 4);
    }


}