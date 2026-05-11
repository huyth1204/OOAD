package calendar;

import calendar.persistence.RoomConflictChecker;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class AddAppointmentDialog extends JDialog {

    private final Calendar calendar;
    private final String currentUserId;
    private boolean confirmed = false;

    private JTextField txtName, txtLocation, txtStart, txtEnd;
    private JComboBox<String> cmbReminder;
    private JLabel lblStatus;
    private JButton btnSubmit;

    private static final DateTimeFormatter DT_FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final Color COLOR_ERROR   = new Color(239, 68, 68);
    private static final Color COLOR_SUCCESS = new Color(34, 197, 94);
    private static final Color COLOR_WARN    = new Color(234, 88, 12);
    private static final Color COLOR_INFO    = new Color(37, 99, 235);
    private static final Font  FONT_LBL  = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font  FONT_FLD  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font  FONT_BTN  = new Font("Segoe UI", Font.BOLD,  13);

    public AddAppointmentDialog(Frame owner, Calendar calendar, String userId) {
        super(owner, "Them cuoc hen moi", true);
        this.calendar      = calendar;
        this.currentUserId = userId;
        initDialog();
        buildUI();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initDialog() {
        setResizable(false);
        getContentPane().setBackground(new Color(245, 247, 250));
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(new Color(245, 247, 250));
        root.add(buildDialogHeader(), BorderLayout.NORTH);
        root.add(buildForm(),         BorderLayout.CENTER);
        root.add(buildButtons(),      BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JPanel buildDialogHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(37, 99, 235));
        p.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
        JLabel lbl = new JLabel("Them cuoc hen moi");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setForeground(Color.WHITE);
        p.add(lbl, BorderLayout.WEST);
        JLabel lblTime = new JLabel("Hien tai: " + LocalDateTime.now().format(DT_FMT));
        lblTime.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTime.setForeground(new Color(186, 230, 253));
        p.add(lblTime, BorderLayout.EAST);
        return p;
    }

    private JPanel buildForm() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 0, 6, 0);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.WEST;

        addFormRow(p, gc, 0, "Ten cuoc hen *", txtName = makeField(380, "VD: Hop nhom PTTKHTDT"));
        addFormRow(p, gc, 1, "Dia diem / Phong", txtLocation = makeField(380, "VD: Phong B102 (de trong neu khong co)"));

        LocalDateTime def = LocalDateTime.now().withSecond(0).withNano(0);
        txtStart = makeField(180, def.format(DT_FMT));
        txtEnd   = makeField(180, def.plusHours(1).format(DT_FMT));
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        timePanel.setBackground(Color.WHITE);
        timePanel.add(txtStart);
        JLabel dash = new JLabel("->");
        dash.setFont(FONT_LBL);
        timePanel.add(dash);
        timePanel.add(txtEnd);
        JLabel hint = new JLabel("(yyyy-MM-dd HH:mm)");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(new Color(148, 163, 184));
        timePanel.add(hint);
        addFormRow(p, gc, 2, "Thoi gian * (Bat dau -> Ket thuc)", timePanel);

        String[] reminderOpts = {"Khong nhac nho", "5 phut truoc", "15 phut truoc",
                                  "30 phut truoc", "1 gio truoc", "1 ngay truoc"};
        cmbReminder = new JComboBox<>(reminderOpts);
        cmbReminder.setFont(FONT_FLD);
        cmbReminder.setPreferredSize(new Dimension(220, 32));
        addFormRow(p, gc, 3, "Nhac nho", cmbReminder);

        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gc.gridx = 0; gc.gridy = 4; gc.gridwidth = 2;
        gc.insets = new Insets(8, 0, 0, 0);
        p.add(lblStatus, gc);
        return p;
    }

    private void addFormRow(JPanel p, GridBagConstraints gc, int row, String label, Component field) {
        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 1;
        gc.weightx = 0; gc.insets = new Insets(6, 0, 6, 16);
        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_LBL);
        lbl.setForeground(new Color(71, 85, 105));
        lbl.setPreferredSize(new Dimension(260, 28));
        p.add(lbl, gc);
        gc.gridx = 1; gc.weightx = 1;
        gc.insets = new Insets(6, 0, 6, 0);
        p.add(field, gc);
    }

    private JTextField makeField(int width, String placeholder) {
        JTextField f = new JTextField();
        f.setFont(FONT_FLD);
        f.setPreferredSize(new Dimension(width, 32));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        f.setForeground(new Color(148, 163, 184));
        f.setText(placeholder);
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (f.getText().equals(placeholder)) {
                    f.setText("");
                    f.setForeground(new Color(30, 41, 59));
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (f.getText().isBlank()) {
                    f.setText(placeholder);
                    f.setForeground(new Color(148, 163, 184));
                }
            }
        });
        return f;
    }

    private JPanel buildButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        p.setBackground(new Color(248, 250, 252));
        p.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        JButton btnCancel = new JButton("Huy");
        btnCancel.setFont(FONT_BTN);
        btnCancel.setPreferredSize(new Dimension(100, 36));
        btnCancel.addActionListener(e -> dispose());
        btnSubmit = new JButton("Luu cuoc hen");
        btnSubmit.setFont(FONT_BTN);
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setBackground(new Color(37, 99, 235));
        btnSubmit.setBorderPainted(false);
        btnSubmit.setFocusPainted(false);
        btnSubmit.setPreferredSize(new Dimension(160, 36));
        btnSubmit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSubmit.addActionListener(e -> runAddFlow());
        p.add(btnCancel);
        p.add(btnSubmit);
        return p;
    }

    private void runAddFlow() {
        btnSubmit.setEnabled(false);
        setStatus("Dang xu ly...", COLOR_INFO);

        String name     = getFieldValue(txtName,     "VD: Hop nhom PTTKHTDT");
        String location = getFieldValue(txtLocation, "VD: Phong B102 (de trong neu khong co)");
        String startStr = getFieldValue(txtStart,    null);
        String endStr   = getFieldValue(txtEnd,      null);

        if (name.isBlank()) {
            setStatus("[Loi] Ten cuoc hen khong duoc de trong!", COLOR_ERROR);
            btnSubmit.setEnabled(true);
            return;
        }

        LocalDateTime start, end;
        try {
            start = LocalDateTime.parse(startStr, DT_FMT);
            end   = LocalDateTime.parse(endStr,   DT_FMT);
        } catch (DateTimeParseException ex) {
            setStatus("[Loi] Dinh dang thoi gian sai. Dung: yyyy-MM-dd HH:mm", COLOR_ERROR);
            btnSubmit.setEnabled(true);
            return;
        }

        int duration = (int) java.time.Duration.between(start, end).toMinutes();
        if (duration <= 0) {
            setStatus("[Loi] Thoi gian ket thuc phai sau thoi gian bat dau!", COLOR_ERROR);
            btnSubmit.setEnabled(true);
            return;
        }

        Integer reminder = parseReminder();
        // Loai cuoc hen (nhom/ca nhan) se duoc tu dong xac dinh
        // dua tren logic nghiep vu (checkGroupMeetingMatch)
        boolean isGroup = false; // Mac dinh la ca nhan

        // ── VALIDATION THUỘC VỀ UI LAYER ────────────────────────────
        // Theo mô tả: "The UI will prevent the user from entering..."
        // Không cần gọi calendar.validateAndSubmit() nữa vì đã validate ở trên:
        // - name.isBlank() đã check
        // - duration <= 0 đã check
        // UI chỉ tạo Appointment khi dữ liệu đã hợp lệ

        Appointment appt = new Appointment(
            "appt-" + System.currentTimeMillis(),
            name, location, start, end, duration, reminder, isGroup
        );

        // ── 1. TRUNG GIO (chi trong lich cua chinh user nay) ─────
        List<Appointment> timeConflicts = calendar.checkTimeConflict(start, end);
        if (!timeConflicts.isEmpty()) {
            StringBuilder sb = new StringBuilder("<html><b>Xung dot thoi gian!</b><br><br>");
            sb.append("Ban da co cuoc hen trung gio:<br>");
            for (Appointment c : timeConflicts) {
                sb.append("- <b>").append(c.getName()).append("</b>")
                  .append(" (").append(c.formatStart()).append(" - ").append(c.formatEnd()).append(")<br>");
            }
            sb.append("<br>Ban muon lam gi?</html>");

            int choice = JOptionPane.showOptionDialog(this, sb.toString(),
                "Xung dot thoi gian cua ban",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null,
                new String[]{"Chon gio khac", "Xoa cuoc hen cu va them moi", "Huy"},
                "Chon gio khac");

            if (choice == 0) {
                setStatus("Vui long chon gio khac.", COLOR_WARN);
                btnSubmit.setEnabled(true);
                return;
            } else if (choice == 1) {
                for (Appointment c : timeConflicts) calendar.removeAppointment(c.getId());
                setStatus("Da xoa " + timeConflicts.size() + " cuoc hen cu.", COLOR_SUCCESS);
            } else {
                btnSubmit.setEnabled(true);
                return;
            }
        }

        // ── 2. TRUNG PHONG VOI USER KHAC ─────────────────────────
        if (location != null && !location.isBlank()) {
            List<String[]> roomConflicts = RoomConflictChecker
                .checkRoomConflictAcrossUsers(currentUserId, location, start, end);

            if (!roomConflicts.isEmpty()) {
                StringBuilder sb = new StringBuilder("<html><b>Phong da duoc dat!</b><br><br>");
                sb.append("Phong <b>").append(location).append("</b> bi trung voi:<br>");
                for (String[] c : roomConflicts) {
                    sb.append("- User <b>").append(c[0]).append("</b>: ")
                      .append(c[1]).append(" (").append(c[2]).append(")<br>");
                }
                sb.append("<br>Ban muon lam gi?</html>");

                int choice = JOptionPane.showOptionDialog(this, sb.toString(),
                    "Trung phong voi user khac",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null,
                    new String[]{"Chon phong khac", "Huy"},
                    "Chon phong khac");

                // Bat ky lua chon nao cung khong cho tiep tuc
                setStatus("Phong da duoc dat. Vui long chon phong khac.", COLOR_WARN);
                btnSubmit.setEnabled(true);
                return;
            }

            // ── 3. TRUNG PHONG TRONG CHINH LICH CUA MINH ─────────
            List<Appointment> myRoomConflicts = calendar.checkLocationConflict(location, start, end);
            if (!myRoomConflicts.isEmpty()) {
                StringBuilder sb = new StringBuilder("<html><b>Trung phong trong lich cua ban!</b><br><br>");
                sb.append("Phong <b>").append(location).append("</b> ban da dat:<br>");
                for (Appointment c : myRoomConflicts) {
                    sb.append("- <b>").append(c.getName()).append("</b>")
                      .append(" (").append(c.formatStart()).append(" - ").append(c.formatEnd()).append(")<br>");
                }
                sb.append("<br>Ban muon lam gi?</html>");

                int choice = JOptionPane.showOptionDialog(this, sb.toString(),
                    "Trung phong trong lich cua ban",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null,
                    new String[]{"Chon phong khac", "Huy"},
                    "Chon phong khac");

                setStatus("Phong da duoc dat. Vui long chon phong khac.", COLOR_WARN);
                btnSubmit.setEnabled(true);
                return;
            }
        }

        // ── 4. KIEM TRA CUOC HOP NHOM ────────────────────────────
        GroupMeeting gm = calendar.checkGroupMeetingMatch(name, duration);
        if (gm != null) {
            int choice = JOptionPane.showOptionDialog(this,
                "<html><b>Phat hien cuoc hop nhom phu hop!</b><br><br>" +
                "Ten: <b>" + gm.getName() + "</b><br>" +
                "Thoi luong: " + gm.getDuration() + " phut<br>" +
                "Dia diem: " + gm.getLocation() + "<br>" +
                "Thanh vien: " + gm.getParticipants().size() + " nguoi<br><br>" +
                "Ban co muon tham gia khong?</html>",
                "Phat hien cuoc hop nhom",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                new String[]{"Tham gia nhom", "Tao hen rieng"},
                "Tham gia nhom");
            if (choice == 0) {
                calendar.addParticipant(currentUserId, gm);
                appt.setGroup(true);
            }
        }

        // ── 5. LUU ───────────────────────────────────────────────
        calendar.recordAppointment(appt);
        if (reminder != null) {
            calendar.saveNewReminder(new Reminder(
                "rem-" + System.currentTimeMillis(),
                appt.getId(), reminder, "Nhac: " + appt.getName()
            ));
        }

        confirmed = true;
        JOptionPane.showMessageDialog(this,
            "<html><b>Cuoc hen da duoc luu thanh cong!</b><br><br>" +
            "Ten: <b>" + appt.getName() + "</b><br>" +
            "Thoi gian: " + appt.formatStart() + " - " + appt.formatEnd() + "<br>" +
            "Thoi luong: " + appt.getDuration() + " phut" +
            (appt.isGroup() ? "<br>Loai: <b>Cuoc hop nhom</b>" : "") +
            (reminder != null ? "<br>Nhac nho: " + formatReminder(reminder) : "") +
            "</html>",
            "Xac nhan", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    private String getFieldValue(JTextField f, String placeholder) {
        String val = f.getText().trim();
        if (placeholder != null && val.equals(placeholder)) return "";
        return val;
    }

    private Integer parseReminder() {
        return switch (cmbReminder.getSelectedIndex()) {
            case 1 -> 5;   case 2 -> 15;  case 3 -> 30;
            case 4 -> 60;  case 5 -> 1440; default -> null;
        };
    }

    private String formatReminder(int minutes) {
        return switch (minutes) {
            case 5 -> "5 phut truoc"; case 15 -> "15 phut truoc";
            case 30 -> "30 phut truoc"; case 60 -> "1 gio truoc";
            case 1440 -> "1 ngay truoc"; default -> minutes + " phut truoc";
        };
    }

    private void setStatus(String msg, Color color) {
        lblStatus.setText(msg);
        lblStatus.setForeground(color);
    }

    public boolean isConfirmed() { return confirmed; }
}
