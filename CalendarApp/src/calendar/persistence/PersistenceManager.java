package calendar.persistence;

import calendar.Appointment;
import calendar.Calendar;
import calendar.GroupMeeting;
import calendar.Reminder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * PersistenceManager – điều phối load/save toàn bộ dữ liệu giữa Calendar và file txt.
 *
 * Sử dụng:
 *   PersistenceManager pm = new PersistenceManager("./data");
 *   pm.loadAll(calendar);   // gọi khi khởi động
 *   pm.saveAll(calendar);   // gọi khi tắt app hoặc sau mỗi thay đổi
 */
public class PersistenceManager {

    private static final Logger log = Logger.getLogger(PersistenceManager.class.getName());

    private static final String FILE_APPOINTMENTS  = "appointments.txt";
    private static final String FILE_GROUP_MEETINGS = "groupmeetings.txt";
    private static final String FILE_REMINDERS     = "reminders.txt";

    private final FileHandler fileHandler;

    public PersistenceManager(String dataDirectory) throws IOException {
        this.fileHandler = new FileHandler(dataDirectory);
    }

    // ── Load ──────────────────────────────────────────────────────

    /**
     * Load tất cả dữ liệu từ file vào Calendar.
     * Nếu file không tồn tại → bỏ qua (không báo lỗi).
     * Nếu một dòng lỗi → bỏ qua dòng đó, tiếp tục dòng sau.
     */
    public void loadAll(Calendar calendar) {
        List<Appointment>  appointments  = loadAppointments();
        List<GroupMeeting> groupMeetings = loadGroupMeetings();
        List<Reminder>     reminders     = loadReminders();

        calendar.setAppointments(appointments);
        calendar.setGroupMeetings(groupMeetings);
        calendar.setReminders(reminders);

        log.info(String.format("Load xong: %d cuộc hẹn, %d họp nhóm, %d nhắc nhở",
            appointments.size(), groupMeetings.size(), reminders.size()));
    }

    private List<Appointment> loadAppointments() {
        List<Appointment> list = new ArrayList<>();
        try {
            List<String> lines = fileHandler.readLines(FILE_APPOINTMENTS);
            int skipped = 0;
            for (int i = 0; i < lines.size(); i++) {
                try {
                    list.add(DataParser.parseAppointment(lines.get(i)));
                } catch (ParseException e) {
                    skipped++;
                    log.warning("Bỏ qua dòng " + (i + 1) + " trong appointments.txt: " + e.getMessage());
                }
            }
            if (skipped > 0)
                log.warning("appointments.txt: bỏ qua " + skipped + " dòng lỗi / " + lines.size() + " tổng");
        } catch (IOException e) {
            log.warning("Không đọc được appointments.txt: " + e.getMessage());
        }
        return list;
    }

    private List<GroupMeeting> loadGroupMeetings() {
        List<GroupMeeting> list = new ArrayList<>();
        try {
            List<String> lines = fileHandler.readLines(FILE_GROUP_MEETINGS);
            int skipped = 0;
            for (int i = 0; i < lines.size(); i++) {
                try {
                    list.add(DataParser.parseGroupMeeting(lines.get(i)));
                } catch (ParseException e) {
                    skipped++;
                    log.warning("Bỏ qua dòng " + (i + 1) + " trong groupmeetings.txt: " + e.getMessage());
                }
            }
            if (skipped > 0)
                log.warning("groupmeetings.txt: bỏ qua " + skipped + " dòng lỗi / " + lines.size() + " tổng");
        } catch (IOException e) {
            log.warning("Không đọc được groupmeetings.txt: " + e.getMessage());
        }
        return list;
    }

    private List<Reminder> loadReminders() {
        List<Reminder> list = new ArrayList<>();
        try {
            List<String> lines = fileHandler.readLines(FILE_REMINDERS);
            int skipped = 0;
            for (int i = 0; i < lines.size(); i++) {
                try {
                    list.add(DataParser.parseReminder(lines.get(i)));
                } catch (ParseException e) {
                    skipped++;
                    log.warning("Bỏ qua dòng " + (i + 1) + " trong reminders.txt: " + e.getMessage());
                }
            }
            if (skipped > 0)
                log.warning("reminders.txt: bỏ qua " + skipped + " dòng lỗi / " + lines.size() + " tổng");
        } catch (IOException e) {
            log.warning("Không đọc được reminders.txt: " + e.getMessage());
        }
        return list;
    }

    // ── Save ──────────────────────────────────────────────────────

    /**
     * Lưu toàn bộ dữ liệu từ Calendar ra file txt.
     */
    public void saveAll(Calendar calendar) {
        saveAppointments(calendar.getAppointments());
        saveGroupMeetings(calendar.getGroupMeetings());
        saveReminders(calendar.getReminderList());
        log.info("saveAll xong.");
    }

    public void saveAppointments(List<Appointment> list) {
        try {
            List<String> lines = PrettyPrinter.formatAppointments(list);
            fileHandler.writeLines(FILE_APPOINTMENTS, lines);
        } catch (IOException e) {
            log.severe("Lỗi lưu appointments.txt: " + e.getMessage());
        }
    }

    public void saveGroupMeetings(List<GroupMeeting> list) {
        try {
            List<String> lines = PrettyPrinter.formatGroupMeetings(list);
            fileHandler.writeLines(FILE_GROUP_MEETINGS, lines);
        } catch (IOException e) {
            log.severe("Lỗi lưu groupmeetings.txt: " + e.getMessage());
        }
    }

    public void saveReminders(List<Reminder> list) {
        try {
            List<String> lines = PrettyPrinter.formatReminders(list);
            fileHandler.writeLines(FILE_REMINDERS, lines);
        } catch (IOException e) {
            log.severe("Lỗi lưu reminders.txt: " + e.getMessage());
        }
    }
}
