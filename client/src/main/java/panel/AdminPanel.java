package panel;

import api.ApiClient;
import dto.FamilyDTO;
import dto.UserDTO;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AdminPanel extends JPanel {
    private final JFrame frame;
    private final UserDTO currentUser;
    private final DefaultListModel<UserDTO> usersModel = new DefaultListModel<>();
    private final JList<UserDTO> usersList = new JList<>(usersModel);
    private final DefaultListModel<FamilyDTO> familiesModel = new DefaultListModel<>();
    private final JList<FamilyDTO> familiesList = new JList<>(familiesModel);

    public AdminPanel(JFrame frame, UserDTO currentUser) {
        this.frame = frame;
        this.currentUser = currentUser;
        setLayout(new BorderLayout());
        initComponents();
        loadData();
    }

    private void initComponents() {
        JPanel usersPanel = new JPanel(new BorderLayout());
        usersList.setCellRenderer(new UserRenderer());
        usersPanel.add(new JScrollPane(usersList), BorderLayout.CENTER);
        JPanel usersButtons = new JPanel(new FlowLayout());
        JButton banButton = new JButton("Забанить");
        banButton.addActionListener(e -> banSelectedUser());
        JButton unbanButton = new JButton("Разбанить");
        unbanButton.addActionListener(e -> unbanSelectedUser());
        JButton deleteUserButton = new JButton("Удалить");
        deleteUserButton.addActionListener(e -> deleteSelectedUser());
        JButton refreshUsersButton = new JButton("Обновить");
        refreshUsersButton.addActionListener(e -> {
            try {
                refreshUsers();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to refresh users: " + ex.getMessage());
            }
        });
        usersButtons.add(banButton);
        usersButtons.add(unbanButton);
        usersButtons.add(deleteUserButton);
        usersButtons.add(refreshUsersButton);
        usersPanel.add(usersButtons, BorderLayout.SOUTH);

        JPanel familiesPanel = new JPanel(new BorderLayout());
        familiesList.setCellRenderer(new FamilyRenderer());
        familiesPanel.add(new JScrollPane(familiesList), BorderLayout.CENTER);
        JPanel familiesButtons = new JPanel(new FlowLayout());
        JButton deleteFamilyButton = new JButton("Удалить семью");
        deleteFamilyButton.addActionListener(e -> deleteSelectedFamily());
        JButton refreshFamiliesButton = new JButton("Обновить");
        refreshFamiliesButton.addActionListener(e -> {
            try {
                refreshFamilies();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to refresh families: " + ex.getMessage());
            }
        });
        familiesButtons.add(deleteFamilyButton);
        familiesButtons.add(refreshFamiliesButton);
        familiesPanel.add(familiesButtons, BorderLayout.SOUTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Пользователи", usersPanel);
        tabbedPane.addTab("Семьи", familiesPanel);

        add(tabbedPane, BorderLayout.CENTER);

        JButton backButton = new JButton("Назад");
        backButton.addActionListener(e -> switchToFamiliesPanel());
        add(backButton, BorderLayout.SOUTH);
    }

    private void loadData() {
        try {
            refreshUsers();
            refreshFamilies();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load data: " + e.getMessage());
        }
    }

    private void refreshUsers() throws Exception {
        List<UserDTO> users = ApiClient.getInstance().getAllUsers();
        usersModel.clear();
        users.forEach(usersModel::addElement);
    }

    private void refreshFamilies() throws Exception {
        List<FamilyDTO> families = ApiClient.getInstance().findAllFamilies();
        familiesModel.clear();
        families.forEach(familiesModel::addElement);
    }

    private void banSelectedUser() {
        UserDTO selected = usersList.getSelectedValue();
        if (selected != null && selected.getId() != currentUser.getId()) {
            try {
                ApiClient.getInstance().banUser(selected.getId());
                refreshUsers();
                JOptionPane.showMessageDialog(this, "User banned successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to ban user: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Cannot ban yourself or no user selected!");
        }
    }

    private void unbanSelectedUser() {
        UserDTO selected = usersList.getSelectedValue();
        if (selected != null) {
            try {
                ApiClient.getInstance().unbanUser(selected.getId());
                refreshUsers();
                JOptionPane.showMessageDialog(this, "User unbanned successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to unban user: " + e.getMessage());
            }
        }
    }

    private void deleteSelectedUser() {
        UserDTO selected = usersList.getSelectedValue();
        if (selected != null && selected.getId() != currentUser.getId()) {
            try {
                ApiClient.getInstance().deleteUser(selected.getId());
                refreshUsers();
                JOptionPane.showMessageDialog(this, "User deleted successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to delete user: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Cannot delete yourself or no user selected!");
        }
    }

    private void deleteSelectedFamily() {
        FamilyDTO selected = familiesList.getSelectedValue();
        if (selected != null) {
            try {
                ApiClient.getInstance().deleteFamily(selected.getId());
                refreshFamilies();
                JOptionPane.showMessageDialog(this, "Family deleted successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to delete family: " + e.getMessage());
            }
        }
    }

    private void switchToFamiliesPanel() {
        frame.getContentPane().removeAll();
        frame.add(new FamiliesPanel(frame, currentUser));
        frame.revalidate();
        frame.repaint();
    }

    private class UserRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            UserDTO user = (UserDTO) value;
            setText(String.format("%s (%s)", user.getUsername(), user.getRole()) + (user.getIsAccountNonLocked() ? "" : " - Забанен"));
            return this;
        }
    }

    private class FamilyRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            FamilyDTO family = (FamilyDTO) value;
            setText(String.format("%s (Owner: %s)", family.getName(), family.getOwner().getUsername()));
            return this;
        }
    }
}