package calendar;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddAppointmentDialog extends JDialog {

    private final Calendar calendar;
    private final String currentUserId;
    private boolean confirmed = false;

    private JTextField txtName, txtLocation;
    private JSpinner spnStartDate, spnStartTime, spnEndDate, spnEndTime;
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

        // Thoi gian bat dau
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        JPanel startPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        startPanel.setBackground(Color.WHITE);
        
        spnStartDate = makeDateSpinner(now.toLocalDate());
        spnStartTime = makeTimeSpinner(now.toLocalTime());
        startPanel.add(new JLabel("Ngay:"));
        startPanel.add(spnStartDate);
        startPanel.add(new JLabel("Gio:"));
        startPanel.add(spnStartTime);
        
        addFormRow(p, gc, 2, "Thoi gian bat dau *", startPanel);

        // Thoi gian ket thuc
        LocalDateTime endDefault = now.plusHours(1);
        JPanel endPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        endPanel.setBackground(Color.WHITE);
        
        spnEndDate = makeDateSpinner(endDefault.toLocalDate());
        spnEndTime = makeTimeSpinner(endDefault.toLocalTime());
        endPanel.add(new JLabel("Ngay:"));
        endPanel.add(spnEndDate);
        endPanel.add(new JLabel("Gio:"));
        endPanel.add(spnEndTime);
        
        addFormRow(p, gc, 3, "Thoi gian ket thuc *", endPanel);

        String[] reminderOpts = {"Khong nhac nho", "5 phut truoc", "15 phut truoc",
                                  "30 phut truoc", "1 gio truoc", "1 ngay truoc"};
        cmbReminder = new JComboBox<>(reminderOpts);
        cmbReminder.setFont(FONT_FLD);
        cmbReminder.setPreferredSize(new Dimension(220, 32));
        addFormRow(p, gc, 4, "Nhac nho", cmbReminder);

        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gc.gridx = 0; gc.gridy = 5; gc.gridwidth = 2;
        gc.insets = new Insets(8, 0, 0, 0);
        p.add(lblStatus, gc);
        return p;
    }

    private JSpinner makeDateSpinner(LocalDate initialDate) {
        Date date = Date.from(initialDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        SpinnerDateModel model = new SpinnerDateModel(date, null, null, java.util.Calendar.DAY_OF_MONTH);
        JSpinner spinner = new JSpinner(model);
        
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "dd/MM/yyyy");
        spinner.setEditor(editor);
        spinner.setFont(FONT_FLD);
        spinner.setPreferredSize(new Dimension(120, 32));
        
        return spinner;
    }

    private JSpinner makeTimeSpinner(LocalTime initialTime) {
        Date date = Date.from(initialTime.atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant());
        SpinnerDateModel model = new SpinnerDateModel(date, null, null, java.util.Calendar.MINUTE);
        JSpinner spinner = new JSpinner(model);
        
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "HH:mm");
        spinner.setEditor(editor);
        spinner.setFont(FONT_FLD);
        spinner.setPreferredSize(new Dimension(80, 32));
        
        return spinner;
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

        // MSG 4: User enters data
        String name     = getFieldValue(txtName,     "VD: Hop nhom PTTKHTDT");
        String location = getFieldValue(txtLocation, "VD: Phong B102 (de trong neu khong co)");

        // MSG 5a: validateInput() - UI validates before sending to Calendar
        if (name.isBlank()) {
            setStatus("[Loi] Ten cuoc hen khong duoc de trong!", COLOR_ERROR);
            btnSubmit.setEnabled(true);
            return;
        }

        LocalDateTime start, end;
        try {
            start = getDateTimeFromSpinners(spnStartDate, spnStartTime);
            end   = getDateTimeFromSpinners(spnEndDate, spnEndTime);
        } catch (Exception ex) {
            setStatus("[Loi] Dinh dang thoi gian sai!", COLOR_ERROR);
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
        boolean isGroup = false; // Mac dinh la ca nhan, se duoc set = true neu join group meeting

        Appointment appt = new Appointment(
            "appt-" + System.currentTimeMillis(),
            name, location, start, end, duration, reminder, isGroup
        );

        // BUOC 1: Kiem tra trung phong (cung dia diem + cung thoi gian) - KIEM TRA TAT CA USERS
        if (location != null && !location.isBlank()) {
            List<Appointment> locationConflicts = calendar.checkLocationConflictAllUsers(location, start, end);
            if (!locationConflicts.isEmpty()) {
                // Co trung phong → Hoi user: Tham gia cuoc hen nay HOAC Chon phong khac
                StringBuilder sb = new StringBuilder("<html><b>Trung phong!</b><br><br>");
                sb.append("Phong <b>").append(location).append("</b> da co cuoc hen:<br>");
                for (Appointment c : locationConflicts) {
                    sb.append("- <b>").append(c.getName()).append("</b>")
                      .append(" (").append(c.formatStart()).append(" - ").append(c.formatEnd()).append(")<br>");
                }
                sb.append("<br>Ban muon lam gi?</html>");

                int choice = JOptionPane.showOptionDialog(this, sb.toString(),
                    "Trung phong",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                    new String[]{"✅ Tham gia cuoc hen nay", "🔄 Chon phong khac"},
                    "🔄 Chon phong khac");

                if (choice == 0) {
                    // User chon "Tham gia cuoc hen nay" → Tao appointment moi cho user nay
                    Appointment existingAppt = locationConflicts.get(0);
                    
                    // Tao appointment moi voi cung thong tin nhung ID khac
                    Appointment joinedAppt = new Appointment(
                        "appt-" + System.currentTimeMillis(),
                        existingAppt.getName(),
                        existingAppt.getLocation(),
                        existingAppt.getStartTime(),
                        existingAppt.getEndTime(),
                        existingAppt.getDuration(),
                        reminder, // Su dung reminder ma user chon
                        existingAppt.isGroup()
                    );
                    
                    // Luu appointment vao calendar cua user hien tai
                    calendar.recordAppointment(joinedAppt);
                    
                    // Luu reminder neu co
                    if (reminder != null) {
                        calendar.saveNewReminder(new Reminder(
                            "rem-" + System.currentTimeMillis(),
                            joinedAppt.getId(), reminder, "Nhac: " + joinedAppt.getName()
                        ));
                    }
                    
                    JOptionPane.showMessageDialog(this,
                        "<html><b>Ban da tham gia cuoc hen!</b><br><br>" +
                        "Ten: <b>" + existingAppt.getName() + "</b><br>" +
                        "Thoi gian: " + existingAppt.formatStart() + " - " + existingAppt.formatEnd() + "<br>" +
                        "Dia diem: " + existingAppt.getLocation() + "<br><br>" +
                        "<i>Cuoc hen da duoc them vao lich cua ban.</i></html>",
                        "Xac nhan", JOptionPane.INFORMATION_MESSAGE);
                    confirmed = true;
                    dispose();
                    return;
                } else {
                    // User chon "Chon phong khac" → Focus vao o dia diem
                    setStatus("Vui long chon phong khac.", COLOR_WARN);
                    txtLocation.requestFocus();
                    txtLocation.selectAll();
                    btnSubmit.setEnabled(true);
                    return;
                }
            }
        }

        // BUOC 2: Kiem tra trung gio (khac dia diem) - Chi canh bao, cho phep tiep tuc
        List<Appointment> timeConflicts = calendar.checkTimeConflict(start, end);
        if (!timeConflicts.isEmpty()) {
            // Loc ra nhung cuoc hen trung gio NHUNG khac dia diem
            List<Appointment> differentLocationConflicts = new ArrayList<>();
            for (Appointment c : timeConflicts) {
                boolean sameLocation = location != null && !location.isBlank() && 
                                      location.trim().equalsIgnoreCase(c.getLocation() != null ? c.getLocation().trim() : "");
                if (!sameLocation) {
                    differentLocationConflicts.add(c);
                }
            }

            if (!differentLocationConflicts.isEmpty()) {
                StringBuilder sb = new StringBuilder("<html><b>Canh bao: Trung thoi gian!</b><br><br>");
                sb.append("Ban da co cuoc hen trung gio (nhung khac dia diem):<br>");
                for (Appointment c : differentLocationConflicts) {
                    sb.append("- <b>").append(c.getName()).append("</b>")
                      .append(" (").append(c.formatStart()).append(" - ").append(c.formatEnd()).append(")")
                      .append(" tai <i>").append(c.getLocation() != null && !c.getLocation().isBlank() ? c.getLocation() : "khong co dia diem").append("</i><br>");
                }
                sb.append("<br>Ban co muon tiep tuc tao cuoc hen moi khong?</html>");

                int choice = JOptionPane.showConfirmDialog(this, sb.toString(),
                    "Canh bao trung thoi gian",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (choice != JOptionPane.YES_OPTION) {
                    setStatus("Da huy tao cuoc hen.", COLOR_WARN);
                    btnSubmit.setEnabled(true);
                    return;
                }
            }
        }

        // MSG 8: checkGroupMeetingMatch(name, dur)
        GroupMeeting gm = calendar.checkGroupMeetingMatch(name, duration, start, end);
        if (gm != null) {
            // MSG 9a: return groupMeeting (or null)
            // Đã match chính xác thời gian + tên + duration
            // → Có thể join vào group meeting này
            
            // MSG 9b: askJoinGroupMeeting - UI hỏi user (không phải Calendar hỏi)
            int choice = JOptionPane.showOptionDialog(this,
                "<html><b>Phat hien cuoc hop nhom phu hop!</b><br><br>" +
                "Ten: <b>" + gm.getName() + "</b><br>" +
                "Thoi gian: " + gm.formatStart() + " - " + gm.formatEnd() + "<br>" +
                "Thoi luong: " + gm.getDuration() + " phut<br>" +
                "Dia diem: " + gm.getLocation() + "<br>" +
                "Thanh vien: " + gm.getParticipants().size() + " nguoi<br><br>" +
                "Ban co muon tham gia khong?</html>",
                "Phat hien cuoc hop nhom",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                new String[]{"Tham gia nhom", "Tao hen rieng"},
                "Tham gia nhom");
            // MSG 9c: user confirms
            if (choice == 0) {
                // MSG 9d: addParticipant(user, meeting)
                calendar.addParticipant(currentUserId, gm);
                appt.setGroup(true);
            }
        } else {
            // KHÔNG match group meeting
            // → Kiểm tra xem có OVERLAP với group meeting nào không
            List<GroupMeeting> allGroupMeetings = calendar.getGroupMeetings();
            for (GroupMeeting meeting : allGroupMeetings) {
                // Check overlap: thời gian trùng NHƯNG không match chính xác
                boolean timeOverlap = start.isBefore(meeting.getEndTime()) && end.isAfter(meeting.getStartTime());
                boolean sameLocation = location != null && !location.isBlank() && 
                                      location.trim().equalsIgnoreCase(meeting.getLocation() != null ? meeting.getLocation().trim() : "");
                
                if (timeOverlap && sameLocation) {
                    // Overlap với group meeting → Conflict!
                    JOptionPane.showMessageDialog(this,
                        "<html><b>Xung dot voi cuoc hop nhom!</b><br><br>" +
                        "Phong <b>" + location + "</b> da co cuoc hop nhom:<br>" +
                        "- <b>" + meeting.getName() + "</b><br>" +
                        "- Thoi gian: " + meeting.formatStart() + " - " + meeting.formatEnd() + "<br>" +
                        "- Thanh vien: " + meeting.getParticipants().size() + " nguoi<br><br>" +
                        "Vui long chon thoi gian hoac phong khac.</html>",
                        "Trung phong voi cuoc hop nhom",
                        JOptionPane.WARNING_MESSAGE);
                    setStatus("Trung phong voi cuoc hop nhom.", COLOR_WARN);
                    btnSubmit.setEnabled(true);
                    return;
                }
            }
        }

        // MSG 10: recordAppointment(appt)
        calendar.recordAppointment(appt);
        
        // MSG 12: saveNewReminder(reminder)
        if (reminder != null) {
            calendar.saveNewReminder(new Reminder(
                "rem-" + System.currentTimeMillis(),
                appt.getId(), reminder, "Nhac: " + appt.getName()
            ));
        }

        // MSG 14: showConfirmation(appt)
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

    private LocalDateTime getDateTimeFromSpinners(JSpinner dateSpinner, JSpinner timeSpinner) {
        Date dateValue = (Date) dateSpinner.getValue();
        Date timeValue = (Date) timeSpinner.getValue();
        
        LocalDate date = dateValue.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime time = timeValue.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        
        return LocalDateTime.of(date, time);
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
