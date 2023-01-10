package com.ims.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;

public class Dashboard extends JFrame {
    private JPanel panelDashboard;
    private JLabel titleLabel;
    private JButton buttonSupplier;
    private JButton buttonCustomer;
    private JButton buttonProduct;
    private JButton buttonSell;
    private JButton buttonPurchase;
    private JButton buttonStock;
    private JButton buttonUser;
    private JButton buttonReport;


//    public static void main(String[] args) {
//        JFrame frame = new JFrame("Dashboard");
//        frame.setContentPane(new Dashboard().panel1);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();
//        frame.setVisible(true);
//    }

    JFrame dashBoard = new JFrame();
    public Dashboard() {
        dashBoard.setTitle("Dashboard");
        dashBoard.setContentPane(panelDashboard);
        dashBoard.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dashBoard.setSize(1024,768);
        dashBoard.setLocationRelativeTo(null);
        dashBoard.setVisible(true);



        buttonSupplier.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    clickSupplierButton();
                } catch (ParseException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        buttonCustomer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clickCustomerButton();
            }
        });
        buttonProduct.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dashBoard.dispose();
                new Product();
            }
        });
        buttonPurchase.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dashBoard.dispose();
                new Purchase();
            }
        });
        buttonStock.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dashBoard.dispose();
                new Stock();
            }
        });
        buttonSell.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dashBoard.dispose();
                new Sales();
            }
        });
        buttonReport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dashBoard.dispose();
                new Report();
            }
        });
    }

    private void clickSupplierButton() throws ParseException {
        dashBoard.dispose();
        new Supplier();
    }

    private void clickCustomerButton() {
        dashBoard.dispose();
        new Customer();
    }


}
