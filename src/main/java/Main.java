import com.ims.ui.Dashboard;
import com.ims.ui.Login;
import com.ims.ui.Supplier;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        Login login = new Login();
        login.setLocationRelativeTo(null);
        login.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        login.setVisible(true);
    }


}