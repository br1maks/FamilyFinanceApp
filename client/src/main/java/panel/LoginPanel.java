package panel;

import api.ApiClient;
import dto.LoginDTO;
import dto.UserDTO;
import enums.UserRole;
import util.Utils;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {
    private final JFrame frame;
    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private JButton loginButton;

    public LoginPanel(JFrame frame) {
        this.frame = frame;
        setLayout(new GridBagLayout());
        initComponents();
    }

    private void initComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Username:"), gbc);

        gbc.gridy = 1;
        add(usernameField, gbc);

        gbc.gridy = 2;
        add(new JLabel("Password:"), gbc);

        gbc.gridy = 3;
        add(passwordField, gbc);

        gbc.gridy = 4;
        loginButton = new JButton("Login");
        loginButton.addActionListener(e -> handleLogin());
        add(loginButton, gbc);

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> switchToRegisterPanel());
        gbc.gridy = 5;
        add(registerButton, gbc);
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        LoginDTO dto = new LoginDTO();
        dto.setUsername(username);
        dto.setPassword(password);

        try {
            ApiClient.getInstance().login(dto);
            UserDTO user = ApiClient.getInstance().getMeInfo();
            frame.getContentPane().removeAll();
            frame.add(new FamiliesPanel(frame, user));
            frame.revalidate();
            frame.repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Login failed: " + e.getMessage());
        }
    }

    private void switchToRegisterPanel() {
        frame.getContentPane().removeAll();
        frame.add(new RegisterPanel(frame));
        frame.revalidate();
        frame.repaint();
    }
}