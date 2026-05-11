package calendar;

import calendar.persistence.DataParser;
import calendar.persistence.FileHandler;
import calendar.persistence.ParseException;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CalendarUI extends JFrame {

    private final Calendar calendar;
    private final String currentUserId;

    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblTotal, lblGroup, lblReminder;
    private JCheckBox chkShowAll; // Bo loc hien thi

    private static final Color COLOR_BG       = new Color(245, 247, 250);
    private static final Color COLOR_HEADER   = new Color(37,  99, 235);
    private static final Color COLOR_BTN_ADD  = new Color(34, 197, 94);
    private static final Color COLOR_BTN_DEL  = new Color(239, 68, 68);
    private static final Color COLOR_GROUP    = new Color(124, 58, 237);
    private static final Color COLOR_PERSONAL = new Color(37,  99, 235);
    private static final Color COLOR_OTHER    = new Color(100, 116, 139); // Mau cua user khac
    private static final Font  FONT_TITLE     = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font  FONT_LABEL     = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font  FONT_BOLD      = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font  FONT_BTN       = new Font("Segoe UI", Font.BOLD,  13);
    private static final DateTimeFormatter DT_FMT =
        DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

    public CalendarUI(Calendar calendar, String userId) {
        this.calendar = calendar;
        this.currentUserId = userId;
        initFrame();
        initComponents();
        refreshTable();
    }

    private void initFrame() {
        setTitle("Calendar Appointment App");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 640);
        setMinimumSize(new Dimension(900, 500));
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BG);
        setLayout(new BorderLayout(0, 0));
    }

    private void initComponents() {
        add(buildHeader(),  BorderLayout.NORTH);
        add(buildCenter(),  BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);
    }

    // ── Header ────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(COLOR_HEADER);
        p.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JLabel title = new JLabel("Calendar Appointment App");
        title.setFont(FONT_TITLE);
        title.setForeground(Color.WHITE);
        p.add(title, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightPanel.setBackground(COLOR_HEADER);

        JLabel user = new JLabel("User: " + currentUserId);
        user.setFont(FONT_LABEL);
        user.setForeground(new Color(186, 230, 253));

        JButton btnLogout = new JButton("Dang xuat");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setForeground(new Color(37, 99, 235));
        btnLogout.setBackground(Color.WHITE);
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> doLogout());

        rightPanel.add(user);
        rightPanel.add(btnLogout);
        p.add(rightPanel, BorderLayout.EAST);
        return p;
    }

    private void doLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Ban co chac muon dang xuat khong?",
            "Dang xuat", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                LoginDialog loginDialog = new LoginDialog();
                loginDialog.setVisible(true);
                String newUser = loginDialog.getLoggedInUser();
                if (newUser != null) Main.startApp(newUser);
                else System.exit(0);
            });
        }
    }

    // ── Center ────────────────────────────────────────────────────
    private JPanel buildCenter() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(COLOR_BG);
        p.setBorder(BorderFactory.createEmptyBorder(16, 20, 0, 20));
        p.add(buildStatsAndFilter(), BorderLayout.NORTH);
        p.add(buildTablePanel(),     BorderLayout.CENTER);
        return p;
    }

    // Stats + checkbox loc
    private JPanel buildStatsAndFilter() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(COLOR_BG);

        // Stats ben trai
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        statsPanel.setBackground(COLOR_BG);
        lblTotal    = makeStatCard("Tong cuoc hen", "0", new Color(37,99,235));
        lblGroup    = makeStatCard("Cuoc hop nhom", "0", new Color(124,58,237));
        lblReminder = makeStatCard("Co nhac nho",   "0", new Color(234,88,12));
        statsPanel.add(lblTotal.getParent());
        statsPanel.add(lblGroup.getParent());
        statsPanel.add(lblReminder.getParent());
        p.add(statsPanel, BorderLayout.WEST);

        // Checkbox hien thi tat ca ben phai
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filterPanel.setBackground(COLOR_BG);
        chkShowAll = new JCheckBox("Hien lich tat ca user");
        chkShowAll.setFont(FONT_BOLD);
        chkShowAll.setBackground(COLOR_BG);
        chkShowAll.setForeground(new Color(37, 99, 235));
        chkShowAll.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        chkShowAll.addActionListener(e -> refreshTable()); // Tu dong load lai khi tick
        filterPanel.add(chkShowAll);
        p.add(filterPanel, BorderLayout.EAST);

        return p;
    }

    private JLabel makeStatCard(String labelText, String value, Color accent) {
        JPanel card = new JPanel(new BorderLayout(6, 2));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226,232,240), 1, true),
            BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(new Color(100,116,139));
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 24));
        val.setForeground(accent);
        card.add(lbl, BorderLayout.NORTH);
        card.add(val, BorderLayout.CENTER);
        return val;
    }

    // Table - bỏ cột "Loại"
    private JScrollPane buildTablePanel() {
        String[] cols = {"#", "User", "Ten cuoc hen", "Dia diem", "Bat dau", "Ket thuc", "Thoi luong", "Nhac nho"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(FONT_LABEL);
        table.setRowHeight(32);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(241,245,249));
        table.setSelectionBackground(new Color(219,234,254));
        table.setSelectionForeground(Color.BLACK);
        table.setBackground(Color.WHITE);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(new Color(248,250,252));
        header.setForeground(new Color(71,85,105));
        header.setPreferredSize(new Dimension(0, 36));

        int[] widths = {35, 70, 220, 120, 120, 120, 90, 100};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Renderer cot User: to sang neu la minh, mo neu la user khac
        table.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                lbl.setHorizontalAlignment(CENTER);
                if (currentUserId.equals(val)) {
                    lbl.setForeground(new Color(37, 99, 235));
                    lbl.setFont(FONT_BOLD);
                } else {
                    lbl.setForeground(COLOR_OTHER);
                    lbl.setFont(FONT_LABEL);
                }
                return lbl;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226,232,240), 1));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    // ── Footer ────────────────────────────────────────────────────
    private JPanel buildFooter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        p.setBackground(COLOR_BG);
        p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226,232,240)));

        JButton btnAdd = makeButton("Them cuoc hen moi", COLOR_BTN_ADD);
        JButton btnDel = makeButton("Xoa cuoc hen",      COLOR_BTN_DEL);
        JButton btnRef = makeButton("Lam moi",           new Color(100,116,139));

        btnAdd.addActionListener(e -> openAddDialog());
        btnDel.addActionListener(e -> deleteSelected());
        btnRef.addActionListener(e -> refreshTable());

        p.add(btnAdd);
        p.add(btnDel);
        p.add(btnRef);
        return p;
    }

    private JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BTN);
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 38));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }

    // ── Refresh table ─────────────────────────────────────────────
    public void refreshTable() {
        tableModel.setRowCount(0);

        // Lay danh sach appointments can hien thi
        List<Object[]> rows = new ArrayList<>();

        if (chkShowAll != null && chkShowAll.isSelected()) {
            // Hien tat ca user: doc tu tung thu muc data/userId
            loadAllUsersAppointments(rows);
        } else {
            // Chi hien cua minh
            for (Appointment a : calendar.getAppointments()) {
                rows.add(buildRow(currentUserId, a));
            }
        }

        // Do nguoc danh sach (moi nhat len tren)
        for (int i = rows.size() - 1; i >= 0; i--) {
            Object[] row = rows.get(i);
            // Cap nhat so thu tu
            row[0] = rows.size() - i;
            tableModel.addRow(row);
        }

        // Cap nhat stats (luon tinh tren du lieu cua minh)
        List<Appointment> myList = calendar.getAppointments();
        long groups    = myList.stream().filter(Appointment::isGroup).count();
        long reminders = calendar.getReminderList().size();
        lblTotal.setText(String.valueOf(myList.size()));
        lblGroup.setText(String.valueOf(groups));
        lblReminder.setText(String.valueOf(reminders));
    }

    private Object[] buildRow(String userId, Appointment a) {
        String reminder = a.getReminder() != null ? a.getReminder() + " phut" : "-";
        return new Object[]{
            0, // so thu tu, cap nhat sau
            userId,
            a.getName(),
            a.getLocation() != null && !a.getLocation().isEmpty() ? a.getLocation() : "-",
            a.formatStart(),
            a.formatEnd(),
            a.getDuration() + " phut",
            reminder
        };
    }

    /** Doc appointments cua tat ca user tu thu muc ./data/ */
    private void loadAllUsersAppointments(List<Object[]> rows) {
        File dataRoot = new File("./data");
        if (!dataRoot.exists()) return;

        File[] userDirs = dataRoot.listFiles(File::isDirectory);
        if (userDirs == null) return;

        for (File userDir : userDirs) {
            String userId = userDir.getName();
            if (userId.equals(currentUserId)) {
                // Du lieu cua chinh minh lay tu calendar (da load san)
                for (Appointment a : calendar.getAppointments()) {
                    rows.add(buildRow(userId, a));
                }
            } else {
                // Doc file cua user khac
                try {
                    FileHandler fh = new FileHandler(userDir.getPath());
                    List<String> lines = fh.readLines("appointments.txt");
                    for (String line : lines) {
                        try {
                            Appointment a = DataParser.parseAppointment(line);
                            rows.add(buildRow(userId, a));
                        } catch (ParseException ignored) {}
                    }
                } catch (Exception ignored) {}
            }
        }
    }

    // ── Delete ───────────────────────────────────────────────────
    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui long chon mot cuoc hen.", "Chua chon", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Chi cho xoa cuoc hen cua chinh minh
        String ownerUserId = (String) tableModel.getValueAt(row, 1);
        if (!ownerUserId.equals(currentUserId)) {
            JOptionPane.showMessageDialog(this, "Ban chi co the xoa cuoc hen cua chinh minh!", "Khong co quyen", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = (String) tableModel.getValueAt(row, 2);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Xoa cuoc hen: \"" + name + "\"?",
            "Xac nhan xoa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            String startStr = (String) tableModel.getValueAt(row, 4);
            calendar.getAppointments().stream()
                .filter(a -> a.getName().equals(name) && a.formatStart().equals(startStr))
                .findFirst()
                .ifPresent(a -> {
                    calendar.removeAppointment(a.getId());
                    refreshTable();
                    JOptionPane.showMessageDialog(this, "Da xoa cuoc hen \"" + name + "\".", "Thanh cong", JOptionPane.INFORMATION_MESSAGE);
                });
        }
    }

    // ── Add dialog ────────────────────────────────────────────────
    private void openAddDialog() {
        AddAppointmentDialog dialog = new AddAppointmentDialog(this, calendar, currentUserId);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) refreshTable();
    }

    public void run() {
        SwingUtilities.invokeLater(() -> setVisible(true));
    }
}
