package panel;

import api.ApiClient;
import dto.*;
import enums.FamilyMemberRole;
import enums.TransactionType;
import util.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public class FamilyPanel extends JPanel {
    private final JFrame frame;
    private final long familyId;
    private final UserDTO currentUser;
    private final JLabel balanceLabel = new JLabel();
    private final JLabel limitLabel = new JLabel();
    private final JLabel warningLabel = new JLabel("Баланс ниже лимита!");
    private final JPanel balancePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private final DefaultListModel<TransactionDTO> transactionsModel = new DefaultListModel<>();
    private final JList<TransactionDTO> transactionsList = new JList<>(transactionsModel);
    private final DefaultListModel<CategoryDTO> categoriesModel = new DefaultListModel<>();
    private final JList<CategoryDTO> categoriesList = new JList<>(categoriesModel);
    private final DefaultListModel<FamilyMemberDTO> membersModel = new DefaultListModel<>();
    private final JList<FamilyMemberDTO> membersList = new JList<>(membersModel);
    private final DefaultListModel<GoalDTO> goalsModel = new DefaultListModel<>();
    private final JList<GoalDTO> goalsList = new JList<>(goalsModel);
    private final JTabbedPane tabbedPane = new JTabbedPane();

    public FamilyPanel(JFrame frame, long familyId, UserDTO currentUser) {
        this.frame = frame;
        this.familyId = familyId;
        this.currentUser = currentUser;
        setLayout(new BorderLayout());
        initComponents();
        loadData();
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new BorderLayout());
        balancePanel.add(balanceLabel);
        balancePanel.add(limitLabel);
        balancePanel.add(warningLabel);
        warningLabel.setForeground(Color.RED);
        warningLabel.setVisible(false);
        topPanel.add(balancePanel, BorderLayout.WEST);

        JPanel topButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Назад");
        backButton.addActionListener(e -> switchToFamiliesPanel());
        JButton setLimitButton = new JButton("Установить лимит бюджета");
        setLimitButton.addActionListener(e -> setBudgetLimit());
        topButtonPanel.add(backButton);
        topButtonPanel.add(setLimitButton);
        topPanel.add(topButtonPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        JPanel transactionsPanel = new JPanel(new BorderLayout());
        transactionsList.setCellRenderer(new TransactionRenderer());
        transactionsList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    TransactionDTO selected = transactionsList.getSelectedValue();
                    if (selected != null) {
                        showTransactionDetails(selected);
                    }
                }
            }
        });
        transactionsPanel.add(new JScrollPane(transactionsList), BorderLayout.CENTER);
        JPanel transactionButtons = new JPanel(new FlowLayout());
        JButton addTransactionButton = new JButton("Добавить транзакцию");
        addTransactionButton.addActionListener(e -> addTransaction());
        JButton refreshTransactionsButton = new JButton("Обновить транзакции");
        refreshTransactionsButton.addActionListener(e -> refreshTransactions());
        transactionButtons.add(addTransactionButton);
        transactionButtons.add(refreshTransactionsButton);
        transactionsPanel.add(transactionButtons, BorderLayout.SOUTH);
        tabbedPane.addTab("Транзакции", transactionsPanel);

        JPanel categoriesPanel = new JPanel(new BorderLayout());
        categoriesList.setCellRenderer(new CategoryRenderer());
        categoriesPanel.add(new JScrollPane(categoriesList), BorderLayout.CENTER);
        JPanel categoryButtons = new JPanel(new FlowLayout());
        JButton createCategoryButton = new JButton("Создать категорию");
        createCategoryButton.addActionListener(e -> createCategory());
        JButton editCategoryButton = new JButton("Редактировать категорию");
        editCategoryButton.addActionListener(e -> editCategory());
        JButton deleteCategoryButton = new JButton("Удалить категорию");
        deleteCategoryButton.addActionListener(e -> deleteCategory());
        JButton refreshCategoriesButton = new JButton("Обновить категории");
        refreshCategoriesButton.addActionListener(e -> refreshCategories());
        categoryButtons.add(createCategoryButton);
        categoryButtons.add(editCategoryButton);
        categoryButtons.add(deleteCategoryButton);
        categoryButtons.add(refreshCategoriesButton);
        categoriesPanel.add(categoryButtons, BorderLayout.SOUTH);
        tabbedPane.addTab("Категории", categoriesPanel);

        JPanel membersPanel = new JPanel(new BorderLayout());
        membersList.setCellRenderer(new MemberRenderer());
        membersPanel.add(new JScrollPane(membersList), BorderLayout.CENTER);
        JPanel memberButtons = new JPanel(new FlowLayout());
        JButton kickMemberButton = new JButton("Исключить участника");
        kickMemberButton.addActionListener(e -> kickMember());
        JButton setRoleButton = new JButton("Установить роль");
        setRoleButton.addActionListener(e -> setMemberRole());
        JButton refreshMembersButton = new JButton("Обновить участников");
        refreshMembersButton.addActionListener(e -> refreshMembers());
        memberButtons.add(kickMemberButton);
        memberButtons.add(setRoleButton);
        memberButtons.add(refreshMembersButton);
        membersPanel.add(memberButtons, BorderLayout.SOUTH);
        tabbedPane.addTab("Участники", membersPanel);

        JPanel goalsPanel = new JPanel(new BorderLayout());
        goalsList.setCellRenderer(new GoalRenderer());
        goalsPanel.add(new JScrollPane(goalsList), BorderLayout.CENTER);
        JPanel goalButtons = new JPanel(new FlowLayout());
        JButton addGoalButton = new JButton("Добавить цель");
        addGoalButton.addActionListener(e -> addGoal());
        JButton editGoalButton = new JButton("Редактировать цель");
        editGoalButton.addActionListener(e -> editGoal());
        JButton refreshGoalsButton = new JButton("Обновить цели");
        refreshGoalsButton.addActionListener(e -> refreshGoals());
        goalButtons.add(addGoalButton);
        goalButtons.add(editGoalButton);
        goalButtons.add(refreshGoalsButton);
        goalsPanel.add(goalButtons, BorderLayout.SOUTH);
        tabbedPane.addTab("Цели", goalsPanel);

        JPanel settingsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        JButton copyInviteCodeButton = new JButton("Скопировать код приглашения");
        copyInviteCodeButton.addActionListener(e -> copyInviteCode());
        settingsPanel.add(copyInviteCodeButton, gbc);

        gbc.gridy = 1;
        JButton renameFamilyButton = new JButton("Переименовать семью");
        renameFamilyButton.addActionListener(e -> renameFamily());
        settingsPanel.add(renameFamilyButton, gbc);

        gbc.gridy = 2;
        JButton exportTransactionsButton = new JButton("Экспорт транзакций в JSON");
        exportTransactionsButton.addActionListener(e -> exportTransactionsToJson());
        settingsPanel.add(exportTransactionsButton, gbc);

        tabbedPane.addTab("Настройки", settingsPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void loadData() {
        try {
            refreshBudget();

            refreshTransactions();

            refreshCategories();

            refreshMembers();

            refreshGoals();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, Utils.formatErrorMessage("Не удалось загрузить данные: " + e.getMessage()));
        }
    }

    private void refreshBudget() {
        try {
            List<BudgetDTO> budgets = ApiClient.getInstance().getBudget(familyId);
            if (!budgets.isEmpty()) {
                BudgetDTO currentBudget = budgets.get(0);
                balanceLabel.setText("Баланс: " + currentBudget.getAmount().toString());
                limitLabel.setText("Лимит: " + currentBudget.getBudgetLimit().toString());
                warningLabel.setVisible(currentBudget.getAmount().compareTo(currentBudget.getBudgetLimit()) < 0);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, Utils.formatErrorMessage("Не удалось обновить бюджет: " + e.getMessage()));
        }
    }

    private void refreshTransactions() {
        try {
            List<TransactionDTO> transactions = ApiClient.getInstance().getTransactions(familyId, YearMonth.now());
            transactionsModel.clear();
            transactions.forEach(transactionsModel::addElement);
            refreshBudget();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, Utils.formatErrorMessage("Не удалось обновить транзакции: " + e.getMessage()));
        }
    }

    private void refreshCategories() {
        try {
            List<CategoryDTO> categories = ApiClient.getInstance().getCategories(familyId);
            categoriesModel.clear();
            categories.forEach(categoriesModel::addElement);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, Utils.formatErrorMessage("Не удалось обновить категории: " + e.getMessage()));
        }
    }

    private void refreshMembers() {
        try {
            FamilyDTO family = ApiClient.getInstance().getFamily(familyId);
            membersModel.clear();
            family.getMembers().forEach(membersModel::addElement);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, Utils.formatErrorMessage("Не удалось обновить участников: " + e.getMessage()));
        }
    }

    private void refreshGoals() {
        try {
            List<GoalDTO> goals = ApiClient.getInstance().getGoals(familyId);
            goalsModel.clear();
            goals.forEach(goalsModel::addElement);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, Utils.formatErrorMessage("Не удалось обновить цели: " + e.getMessage()));
        }
    }

    private void switchToFamiliesPanel() {
        frame.getContentPane().removeAll();
        frame.add(new FamiliesPanel(frame, currentUser));
        frame.revalidate();
        frame.repaint();
    }

    private void copyInviteCode() {
        try {
            String code = ApiClient.getInstance().getInviteCode(familyId);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(code), null);
            JOptionPane.showMessageDialog(this, "Код приглашения скопирован в буфер обмена!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, Utils.formatErrorMessage("Не удалось скопировать код приглашения: " + e.getMessage()));
        }
    }

    private void renameFamily() {
        String newName = JOptionPane.showInputDialog(this, "Введите новое название семьи:");
        if (newName != null && !newName.trim().isEmpty()) {
            try {
                UpdateFamilyDTO dto = new UpdateFamilyDTO();
                dto.setName(newName);
                ApiClient.getInstance().updateFamily(familyId, dto);
                JOptionPane.showMessageDialog(this, "Название семьи успешно обновлено!");
                loadData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, Utils.formatErrorMessage("Не удалось переименовать семью: " + e.getMessage()));
            }
        }
    }

    private void setBudgetLimit() {
        String limitStr = JOptionPane.showInputDialog(this, "Введите новый лимит бюджета:");
        if (limitStr != null && !limitStr.trim().isEmpty()) {
            try {
                BigDecimal limit = new BigDecimal(limitStr);
                BudgetLimitRequestDTO dto = new BudgetLimitRequestDTO();
                dto.setLimit(limit);
                ApiClient.getInstance().setBudgetLimit(familyId, dto);
                loadData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, Utils.formatErrorMessage("Не удалось установить лимит бюджета: " + e.getMessage()));
            }
        }
    }

    private void exportTransactionsToJson() {
        try {
            List<TransactionDTO> transactions = ApiClient.getInstance().getTransactions(familyId, YearMonth.now());

            StringBuilder json = new StringBuilder("[\n");
            for (int i = 0; i < transactions.size(); i++) {
                TransactionDTO t = transactions.get(i);
                json.append("    {\n");
                json.append("        \"id\": ").append(t.getId()).append(",\n");
                json.append("        \"createdBy\": \"").append(t.getCreatedBy().getUsername()).append("\",\n");
                json.append("        \"createdAt\": \"").append(t.getCreatedAt().toString()).append("\",\n");
                json.append("        \"amount\": ").append(t.getAmount().toString()).append(",\n");
                json.append("        \"type\": \"").append(t.getType() == TransactionType.INCOME ? "INCOME" : "EXPENSE").append("\",\n");
                json.append("        \"category\": \"").append(t.getCategory() != null ? t.getCategory().getName() : "null").append("\"\n");
                json.append("    }");
                if (i < transactions.size() - 1) json.append(",");
                json.append("\n");
            }
            json.append("]");

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File("transactions_" + familyId + "_" + YearMonth.now() + ".json"));
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(json.toString());
                    JOptionPane.showMessageDialog(this, "Транзакции успешно экспортированы в " + file.getAbsolutePath());
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, Utils.formatErrorMessage("Ошибка при сохранении файла: " + e.getMessage()));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, Utils.formatErrorMessage("Не удалось экспортировать транзакции: " + e.getMessage()));
        }
    }

    private void createCategory() {
        String name = JOptionPane.showInputDialog(this, "Введите название категории:");
        if (name != null && !name.trim().isEmpty()) {
            try {
                CreateCategoryDTO dto = new CreateCategoryDTO();
                dto.setName(name);
                dto.setFamilyId(familyId);
                ApiClient.getInstance().createCategory(dto);
                loadData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, Utils.formatErrorMessage("Не удалось создать категорию: " + e.getMessage()));
            }
        }
    }

    private void editCategory() {
        CategoryDTO selected = categoriesList.getSelectedValue();
        if (selected != null) {
            String newName = JOptionPane.showInputDialog(this, "Введите новое название категории:", selected.getName());
            if (newName != null && !newName.trim().isEmpty()) {
                try {
                    UpdateCategoryDTO dto = new UpdateCategoryDTO(newName);
                    ApiClient.getInstance().updateCategory(selected.getId(), dto);
                    loadData();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, Utils.formatErrorMessage("Не удалось обновить категорию: " + e.getMessage()));
                }
            }
        }
    }

    private void deleteCategory() {
        CategoryDTO selected = categoriesList.getSelectedValue();
        if (selected != null) {
            try {
                DeleteCategoryDTO dto = new DeleteCategoryDTO();
                dto.setFamilyId(familyId);
                ApiClient.getInstance().deleteCategory(selected.getId(), dto);
                loadData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, Utils.formatErrorMessage("Не удалось удалить категорию: " + e.getMessage()));
            }
        }
    }

    private void showTransactionDetails(TransactionDTO transaction) {
        String details = String.format(
                "ID транзакции: %d\nСоздал: %s\nДата создания: %s\nСумма: %s\nТип: %s\nКатегория: %s",
                transaction.getId(),
                transaction.getCreatedBy().getUsername(),
                transaction.getCreatedAt().toString(),
                transaction.getAmount().toString(),
                transaction.getType() == TransactionType.INCOME ? "Доход" : "Расход",
                transaction.getCategory() != null ? transaction.getCategory().getName() : "Без категории"
        );
        JOptionPane.showMessageDialog(this, Utils.formatErrorMessage(details), "Детали транзакции", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addTransaction() {
        JPanel panel = new JPanel(new GridLayout(5, 2));
        JTextField amountField = new JTextField();
        JComboBox<TransactionType> typeCombo = new JComboBox<>(new TransactionType[]{TransactionType.INCOME, TransactionType.EXPENSE});
        typeCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("Выберите тип");
                } else {
                    TransactionType type = (TransactionType) value;
                    setText(type == TransactionType.INCOME ? "Доход" : "Расход");
                }
                return this;
            }
        });

        CategoryDTO[] categoriesArray = new CategoryDTO[categoriesModel.size() + 1];
        categoriesArray[0] = null;
        for (int i = 0; i < categoriesModel.size(); i++) {
            categoriesArray[i + 1] = categoriesModel.get(i);
        }
        JComboBox<CategoryDTO> categoryCombo = new JComboBox<>(categoriesArray);
        categoryCombo.setSelectedIndex(0);
        categoryCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("Без категории");
                } else {
                    CategoryDTO category = (CategoryDTO) value;
                    setText(category.getName());
                }
                return this;
            }
        });

        GoalDTO[] goalsArray = new GoalDTO[goalsModel.size() + 1];
        goalsArray[0] = null;
        for (int i = 0; i < goalsModel.size(); i++) {
            goalsArray[i + 1] = goalsModel.get(i);
        }
        JComboBox<GoalDTO> goalCombo = new JComboBox<>(goalsArray);
        goalCombo.setSelectedIndex(0);
        goalCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("Без цели");
                } else {
                    GoalDTO goal = (GoalDTO) value;
                    setText(goal.getName());
                }
                return this;
            }
        });

        panel.add(new JLabel("Сумма:"));
        panel.add(amountField);
        panel.add(new JLabel("Тип:"));
        panel.add(typeCombo);
        panel.add(new JLabel("Категория:"));
        panel.add(categoryCombo);
        panel.add(new JLabel("Цель:"));
        panel.add(goalCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Добавить транзакцию", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                CreateTransactionDTO dto = new CreateTransactionDTO();
                dto.setAmount(new BigDecimal(amountField.getText()));
                dto.setType((TransactionType) typeCombo.getSelectedItem());
                CategoryDTO selectedCategory = (CategoryDTO) categoryCombo.getSelectedItem();
                dto.setCategoryId(selectedCategory != null ? selectedCategory.getId() : null);
                GoalDTO selectedGoal = (GoalDTO) goalCombo.getSelectedItem();
                dto.setGoalId(selectedGoal != null ? selectedGoal.getId() : null);
                dto.setFamilyId(familyId);
                ApiClient.getInstance().createTransaction(dto);
                loadData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, Utils.formatErrorMessage("Не удалось добавить транзакцию: " + e.getMessage()));
            }
        }
    }

    private void kickMember() {
        FamilyMemberDTO selected = membersList.getSelectedValue();
        if (selected != null) {
            try {
                KickMemberDTO dto = new KickMemberDTO();
                dto.setUserId(selected.getUser().getId());
                ApiClient.getInstance().kickMember(familyId, dto);
                loadData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, Utils.formatErrorMessage("Не удалось исключить участника: " + e.getMessage()));
            }
        }
    }

    private void setMemberRole() {
        FamilyMemberDTO selected = membersList.getSelectedValue();
        if (selected != null) {
            JComboBox<FamilyMemberRole> roleCombo = new JComboBox<>(new FamilyMemberRole[]{FamilyMemberRole.ROLE_OWNER, FamilyMemberRole.ROLE_MEMBER});
            roleCombo.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    FamilyMemberRole role = (FamilyMemberRole) value;
                    setText(role == FamilyMemberRole.ROLE_OWNER ? "Админ" : "Участник");
                    return this;
                }
            });
            int result = JOptionPane.showConfirmDialog(this, roleCombo, "Выберите новую роль", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    SetFamilyMemberRoleDTO dto = new SetFamilyMemberRoleDTO();
                    dto.setRole((FamilyMemberRole) roleCombo.getSelectedItem());
                    ApiClient.getInstance().setMemberRole(familyId, selected.getId(), dto);
                    loadData();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, Utils.formatErrorMessage("Не удалось установить роль участника: " + e.getMessage()));
                }
            }
        }
    }

    private void addGoal() {
        JPanel panel = new JPanel(new GridLayout(2, 2));
        JTextField nameField = new JTextField();
        JTextField amountField = new JTextField();

        panel.add(new JLabel("Название цели:"));
        panel.add(nameField);
        panel.add(new JLabel("Сумма цели:"));
        panel.add(amountField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Добавить цель", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                CreateGoalDTO dto = new CreateGoalDTO();
                dto.setName(nameField.getText());
                dto.setGoalAmount(new BigDecimal(amountField.getText()));
                ApiClient.getInstance().createGoal(familyId, dto);
                loadData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, Utils.formatErrorMessage("Не удалось создать цель: " + e.getMessage()));
            }
        }
    }

    private void editGoal() {
        GoalDTO selected = goalsList.getSelectedValue();
        if (selected != null) {
            JPanel panel = new JPanel(new GridLayout(2, 2));
            JTextField nameField = new JTextField(selected.getName());
            JTextField amountField = new JTextField(selected.getGoalAmount().toString());

            panel.add(new JLabel("Название цели:"));
            panel.add(nameField);
            panel.add(new JLabel("Сумма цели:"));
            panel.add(amountField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Редактировать цель", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    UpdateGoalDTO dto = new UpdateGoalDTO();
                    dto.setName(nameField.getText());
                    dto.setGoalAmount(new BigDecimal(amountField.getText()));
                    ApiClient.getInstance().updateGoal(familyId, selected.getId(), dto);
                    loadData();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, Utils.formatErrorMessage("Не удалось обновить цель: " + e.getMessage()));
                }
            }
        }
    }

    private class TransactionRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            TransactionDTO transaction = (TransactionDTO) value;
            String type = transaction.getType() == TransactionType.INCOME ? "Доход" : "Расход";
            setText(String.format("%s: %s (%s)", type, transaction.getAmount(), transaction.getCategory() != null ? transaction.getCategory().getName() : "Без категории"));
            return this;
        }
    }

    private class CategoryRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            CategoryDTO category = (CategoryDTO) value;
            setText(category.getName());
            return this;
        }
    }

    private class MemberRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            FamilyMemberDTO member = (FamilyMemberDTO) value;
            String role = member.getRole() == FamilyMemberRole.ROLE_OWNER ? "Админ" : "Участник";
            setText(String.format("%s (%s)", member.getUser().getUsername(), role));
            return this;
        }
    }

    private class GoalRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            GoalDTO goal = (GoalDTO) value;
            setText(String.format("%s: Цель: %s, Накоплено: %s", goal.getName(), goal.getGoalAmount(), goal.getAccumulatedAmount()));
            return this;
        }
    }
}