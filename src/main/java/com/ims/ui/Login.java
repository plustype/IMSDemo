package com.ims.ui;

import com.ims.dao.UserDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.SQLException;

public class Login extends JFrame {
    private JPanel login;
    private JComboBox userTypeBox;
    private JTextField textUser;
    private JPasswordField textPassword;
    private JLabel username;
    private JLabel password;
    private JButton loginButton;
    private JButton clearButton;
    private JLabel titleLabel1;
    private JLabel userTypeLabel;

    public Login() throws HeadlessException {
        setContentPane(login);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Login");
        setSize(300, 400);
        //setVisible(true);


        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clickClearButton();
            }
        });


        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clickLoginButton();
            }
        });
    }

    private String encryptPassword(String input) {
        String enPass = null;
        if (input == null)
            return null;

        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            md5.update(input.getBytes(), 0, input.length());
            enPass = new BigInteger(1, md5.digest()).toString(16);

        }catch (Exception e) {
            System.out.println("encrypt failed " + e.getMessage());
        }
        return enPass;
    }

    private void clickLoginButton() {
        String username = textUser.getText();
        String inputPassword = textPassword.getText();
        String password = encryptPassword(inputPassword);

        String userType;
        userType = (String)userTypeBox.getSelectedItem();

        try {
            if (new UserDAO().checkLogin(username, password, userType)) {
                dispose();
                new Dashboard();
            }else{
                JOptionPane.showMessageDialog(null, "Invalid username or password or user type");
                textUser.setText("");
                textPassword.setText("");
            }
        } catch (SQLException ex) {
            System.out.println("Login failed" + ex.getMessage());;
        }
    }

    private void clickClearButton() {
        textUser.setText("");
        textPassword.setText("");
    }
}
