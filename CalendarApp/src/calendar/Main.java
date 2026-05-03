package calendar;

import calendar.persistence.PersistenceManager;

import javax.swing.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Hien man hinh dang nhap
        LoginDialog loginDialog = new LoginDialog();
        loginDialog.setVisible(true);

        String userId = loginDialog.getLoggedInUser();
        if (userId == null) System.exit(0);

        startApp(userId);
    }

    /** Khoi dong app voi user cu the - goi duoc tu ca Main va logout */
    public static void startApp(String userId) {
        System.out.println("[Main] Dang nhap: " + userId);

        String dataDir = "./data/" + userId;
        Calendar calendar = new Calendar();
        PersistenceManager pm = null;

        try {
            pm = new PersistenceManager(dataDir);
            System.out.println("[Main] Data folder: " + new java.io.File(dataDir).getAbsolutePath());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                "Khong tao duoc thu muc data: " + e.getMessage(),
                "Canh bao", JOptionPane.WARNING_MESSAGE);
        }

        if (pm != null) pm.loadAll(calendar);

        if (calendar.getAppointments().isEmpty() && calendar.getGroupMeetings().isEmpty()) {
            seedSampleData(calendar);
        }

        if (pm != null) {
            calendar.setPersistenceManager(pm);
            pm.saveAll(calendar);
        }

        final PersistenceManager finalPm = pm;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (finalPm != null) finalPm.saveAll(calendar);
        }));

        final Calendar finalCalendar = calendar;
        SwingUtilities.invokeLater(() -> {
            CalendarUI ui = new CalendarUI(finalCalendar, userId);
            ui.run();
        });
    }

    private static void seedSampleData(Calendar calendar) {
        calendar.setGroupMeetings(List.of(
            new GroupMeeting("gm001", "Hop nhom PTTKHTDT",         90,  "Phong B102",   List.of("u002", "u003")),
            new GroupMeeting("gm002", "Seminar Ky thuat phan mem", 120, "Hoi truong A", List.of("u004"))
        ));
        calendar.setAppointments(List.of(
            new Appointment("appt-s1", "Bao cao tien do mon PTTKHTDT", "Phong C305",
                LocalDateTime.of(2026,5,10,9,0),  LocalDateTime.of(2026,5,10,10,30), 90, 15,   false),
            new Appointment("appt-s2", "Hop nhom PTTKHTDT", "Phong B102",
                LocalDateTime.of(2026,5,12,14,0), LocalDateTime.of(2026,5,12,15,30), 90, null, true)
        ));
    }
}
