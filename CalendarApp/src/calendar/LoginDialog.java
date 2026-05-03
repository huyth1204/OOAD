package calendar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

/**
 * LoginDialog – Man hinh dang nhap don gian
 * Tai khoan co dinh trong code (demo)
 */
public class LoginDialog extends JDialog {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JLabel lblStatus;
    private String loggedInUser = null;

    // Tai khoan co dinh: username -> password
    private static final Map<String, String> ACCOUNTS = new HashMap<>();
    static {
        ACCOUNTS.put("u001", "123");
        ACCOUNTS.put("u002", "123");
        ACCOUNTS.put("u003", "123");
        ACCOUNTS.put("admin", "admin");
    }

    private static final Color COLOR_BG     = new Color(245, 247, 250);
    private static final Color COLOR_HEADER = new Color(37, 99, 235);
    private static final Color COLOR_ERROR  = new Color(239, 68, 68);
    private static final Font  FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font  FONT_LBL     = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font  FONT_FLD     = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font  FONT_BTN     = new Font("Segoe UI", Font.BOLD, 13);

    public LoginDialog() {
        super((Frame) null, "Dang nhap", true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0); // Dong cua so = thoat app
            }
        });
        buildUI();
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(COLOR_BG);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_HEADER);
        header.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        JLabel title = new JLabel("Calendar Appointment App");
        title.setFont(FONT_TITLE);
        title.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Vui long dang nhap de tiep tuc");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(186, 230, 253));
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setBackground(COLOR_HEADER);
        titlePanel.add(title);
        titlePanel.add(sub);
        header.add(titlePanel, BorderLayout.WEST);
        root.add(header, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(28, 32, 20, 32));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(8, 0, 8, 0);

        // Username
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 1; gc.weightx = 0;
        JLabel lblUser = new JLabel("Ten tai khoan:");
        lblUser.setFont(FONT_LBL);
        lblUser.setForeground(new Color(71, 85, 105));
        form.add(lblUser, gc);

        gc.gridx = 1; gc.weightx = 1; gc.insets = new Insets(8, 12, 8, 0);
        txtUsername = new JTextField();
        txtUsername.setFont(FONT_FLD);
        txtUsername.setPreferredSize(new Dimension(220, 36));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        form.add(txtUsername, gc);

        // Password
        gc.gridx = 0; gc.gridy = 1; gc.weightx = 0; gc.insets = new Insets(8, 0, 8, 0);
        JLabel lblPass = new JLabel("Mat khau:");
        lblPass.setFont(FONT_LBL);
        lblPass.setForeground(new Color(71, 85, 105));
        form.add(lblPass, gc);

        gc.gridx = 1; gc.weightx = 1; gc.insets = new Insets(8, 12, 8, 0);
        txtPassword = new JPasswordField();
        txtPassword.setFont(FONT_FLD);
        txtPassword.setPreferredSize(new Dimension(220, 36));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        // Enter trong password = nhan dang nhap
        txtPassword.addActionListener(e -> doLogin());
        form.add(txtPassword, gc);

        // Status
        gc.gridx = 0; gc.gridy = 2; gc.gridwidth = 2; gc.insets = new Insets(4, 0, 0, 0);
        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStatus.setForeground(COLOR_ERROR);
        form.add(lblStatus, gc);

        // Hint tai khoan
        gc.gridy = 3; gc.insets = new Insets(8, 0, 0, 0);

        root.add(form, BorderLayout.CENTER);

        // Button
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 16));
        btnPanel.setBackground(new Color(248, 250, 252));
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        JButton btnLogin = new JButton("Dang nhap");
        btnLogin.setFont(FONT_BTN);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBackground(new Color(37, 99, 235));
        btnLogin.setBorderPainted(false);
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(160, 40));
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> doLogin());

        JButton btnExit = new JButton("Thoat");
        btnExit.setFont(FONT_BTN);
        btnExit.setPreferredSize(new Dimension(100, 40));
        btnExit.addActionListener(e -> System.exit(0));

        btnPanel.add(btnLogin);
        btnPanel.add(btnExit);
        root.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void doLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setText("Vui long nhap day du thong tin!");
            return;
        }

        String correctPassword = ACCOUNTS.get(username);
        if (correctPassword != null && correctPassword.equals(password)) {
            loggedInUser = username;
            dispose(); // Dang nhap thanh cong, dong dialog
        } else {
            lblStatus.setText("Ten tai khoan hoac mat khau sai!");
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }

    /** Tra ve user da dang nhap, null neu chua dang nhap */
    public String getLoggedInUser() {
        return loggedInUser;
    }

    /** Kiem tra user co quyen admin khong */
    public static boolean isAdmin(String userId) {
        return "admin".equals(userId);
    }
}
