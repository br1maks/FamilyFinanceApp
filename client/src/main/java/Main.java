import panel.LoginPanel;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Приложение для семейного бюджета");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            LoginPanel loginPanel = new LoginPanel(frame);
            frame.add(loginPanel);
            frame.setVisible(true);
        });
    }
}