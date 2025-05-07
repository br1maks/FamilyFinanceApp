package panel;

import api.ApiClient;
import dto.CreateFamilyDTO;
import dto.FamilyDTO;
import dto.JoinByInviteCodeDTO;
import dto.UserDTO;
import enums.UserRole;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class FamiliesPanel extends JPanel {
    private final JFrame frame;
    private final UserDTO currentUser;
    private final DefaultListModel<FamilyDTO> familiesModel = new DefaultListModel<>();
    private final JList<FamilyDTO> familiesList = new JList<>(familiesModel);

    public FamiliesPanel(JFrame frame, UserDTO currentUser) {
        this.frame = frame;
        this.currentUser = currentUser;
        setLayout(new BorderLayout());
        initComponents();
        loadFamilies();
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton logoutButton = new JButton("Выйти");
        logoutButton.addActionListener(e -> logout());
        topPanel.add(logoutButton);

        if (currentUser.getRole() == UserRole.ROLE_OWNER) {
            JButton adminButton = new JButton("Админ панель");
            adminButton.addActionListener(e -> switchToAdminPanel());
            topPanel.add(adminButton);
        }

        add(topPanel, BorderLayout.NORTH);

        familiesList.setCellRenderer(new FamilyRenderer());
        familiesList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    FamilyDTO selected = familiesList.getSelectedValue();
                    if (selected != null) {
                        frame.getContentPane().removeAll();
                        frame.add(new FamilyPanel(frame, selected.getId(), currentUser));
                        frame.revalidate();
                        frame.repaint();
                    }
                }
            }
        });
        add(new JScrollPane(familiesList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton createFamilyButton = new JButton("Создать семью");
        createFamilyButton.addActionListener(e -> createFamily());
        JButton joinFamilyButton = new JButton("Присоединиться к семье");
        joinFamilyButton.addActionListener(e -> joinFamily());
        JButton refreshButton = new JButton("Обновить");
        refreshButton.addActionListener(e -> loadFamilies());
        buttonPanel.add(createFamilyButton);
        buttonPanel.add(joinFamilyButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadFamilies() {
        try {
            List<FamilyDTO> families = ApiClient.getInstance().getFamilies();
            familiesModel.clear();
            families.forEach(familiesModel::addElement);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load families: " + e.getMessage());
        }
    }

    private void createFamily() {
        String familyName = JOptionPane.showInputDialog(this, "Введите название семьи:");
        if (familyName != null && !familyName.trim().isEmpty()) {
            try {
                CreateFamilyDTO dto = new CreateFamilyDTO();
                dto.setName(familyName);
                ApiClient.getInstance().createFamily(dto);
                JOptionPane.showMessageDialog(this, "Семья успешно создана!");
                loadFamilies();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to create family: " + e.getMessage());
            }
        }
    }

    private void joinFamily() {
        String inviteCode = JOptionPane.showInputDialog(this, "Введите код приглашения:");
        if (inviteCode != null && !inviteCode.trim().isEmpty()) {
            try {
                JoinByInviteCodeDTO dto = new JoinByInviteCodeDTO();
                dto.setInviteCode(inviteCode);
                ApiClient.getInstance().joinFamilyByCode(dto);
                JOptionPane.showMessageDialog(this, "Вы успешно присоединились к семье!");
                loadFamilies();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to join family: " + e.getMessage());
            }
        }
    }

    private void logout() {
        ApiClient.getInstance().logout();
        frame.getContentPane().removeAll();
        frame.add(new LoginPanel(frame));
        frame.revalidate();
        frame.repaint();
    }

    private void switchToAdminPanel() {
        frame.getContentPane().removeAll();
        frame.add(new AdminPanel(frame, currentUser));
        frame.revalidate();
        frame.repaint();
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