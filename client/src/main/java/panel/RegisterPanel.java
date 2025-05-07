package panel;

import api.ApiClient;
import dto.RegisterDTO;
import util.Utils;

import javax.swing.*;
import java.awt.*;

public class RegisterPanel extends JPanel {
    private final JFrame frame;
    private final JTextField usernameField = new JTextField(20);
    private final JTextField firstNameField = new JTextField(20);
    private final JTextField lastNameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);

    public RegisterPanel(JFrame frame) {
        this.frame = frame;
        setLayout(new GridBagLayout());
        initComponents();
    }

    private void initComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Имя пользователя:"), gbc);

        gbc.gridx = 1;
        add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Имя:"), gbc);

        gbc.gridx = 1;
        add(firstNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Фамилия:"), gbc);

        gbc.gridx = 1;
        add(lastNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Пароль:"), gbc);

        gbc.gridx = 1;
        add(passwordField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton registerButton = new JButton("Зарегистрироваться");
        registerButton.addActionListener(e -> register());
        JButton backButton = new JButton("Вернуться к входу");
        backButton.addActionListener(e -> switchToLoginPanel());
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);
    }

    private void register() {
        try {
            RegisterDTO dto = new RegisterDTO();
            dto.setUsername(usernameField.getText());
            dto.setFirstName(firstNameField.getText());
            dto.setLastName(lastNameField.getText());
            dto.setPassword(new String(passwordField.getPassword()));
            ApiClient.getInstance().register(dto);
            JOptionPane.showMessageDialog(this, "Регистрация прошла успешно! Пожалуйста, войдите.");
            switchToLoginPanel();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, Utils.formatErrorMessage("Ошибка регистрации: " + e.getMessage()));
        }
    }

    private void switchToLoginPanel() {
        frame.getContentPane().removeAll();
        frame.add(new LoginPanel(frame));
        frame.revalidate();
        frame.repaint();
    }
}