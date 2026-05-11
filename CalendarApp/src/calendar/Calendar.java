package calendar;

import calendar.persistence.PersistenceManager;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

/**
 * Lớp Calendar – chứa toàn bộ business logic
 */
public class Calendar {

    private List<Appointment>  appointments  = new ArrayList<>();
    private List<GroupMeeting> groupMeetings = new ArrayList<>();
    private List<Reminder>     reminderList  = new ArrayList<>();

    private PersistenceManager persistenceManager;

    public Calendar() {}

    public void setPersistenceManager(PersistenceManager pm) {
        this.persistenceManager = pm;
    }

    public void setAppointments(List<Appointment> list) {
        this.appointments = new ArrayList<>(list);
    }

    public void setGroupMeetings(List<GroupMeeting> list) {
        this.groupMeetings = new ArrayList<>(list);
    }

    public void setReminders(List<Reminder> list) {
        this.reminderList = new ArrayList<>(list);
    }

    // ── MSG 5: validateAndSubmit đã bị XÓA ───────────────────────
    // Validation giờ thuộc về UI layer (AddAppointmentDialog)
    // Calendar chỉ nhận Appointment đã hợp lệ qua recordAppointment()

    // ── MSG 6: submitAppointment ──────────────────────────────────
    // Method tổng hợp để xử lý appointment submission
    // Trả về null nếu thành công, hoặc message string nếu có vấn đề
    public String submitAppointment(Appointment appt, String userId) {
        // Bước này chỉ là entry point, logic thực tế ở các method riêng
        // UI sẽ gọi checkTimeConflict, checkGroupMeetingMatch riêng
        // Method này để tuân thủ diagram, nhưng không dùng trong flow hiện tại
        return null;
    }

    // ── MSG 7: checkTimeConflict ──────────────────────────────────
    // Trả về DANH SÁCH tất cả cuộc hẹn bị trùng giờ (không phải chỉ 1)
    public List<Appointment> checkTimeConflict(LocalDateTime start, LocalDateTime end) {
        List<Appointment> conflicts = new ArrayList<>();
        for (Appointment a : appointments) {
            if (start.isBefore(a.getEndTime()) && end.isAfter(a.getStartTime())) {
                conflicts.add(a);
            }
        }
        return conflicts;
    }

    // ── THÊM MỚI: checkLocationConflict ──────────────────────────
    // Kiểm tra trùng phòng: cùng địa điểm + trùng giờ
    public List<Appointment> checkLocationConflict(String location, LocalDateTime start, LocalDateTime end) {
        List<Appointment> conflicts = new ArrayList<>();
        if (location == null || location.isBlank()) return conflicts; // Không có địa điểm thì bỏ qua
        for (Appointment a : appointments) {
            boolean sameLocation = location.trim().equalsIgnoreCase(
                a.getLocation() != null ? a.getLocation().trim() : "");
            boolean timeOverlap = start.isBefore(a.getEndTime()) && end.isAfter(a.getStartTime());
            if (sameLocation && timeOverlap) {
                conflicts.add(a);
            }
        }
        return conflicts;
    }

    // ── MSG 8: checkGroupMeetingMatch ─────────────────────────────
    // Kiểm tra xem appointment có khớp với group meeting nào không
    // Match điều kiện: tên giống nhau + duration gần giống + thời gian trùng khớp
    public GroupMeeting checkGroupMeetingMatch(String name, int duration, LocalDateTime start, LocalDateTime end) {
        for (GroupMeeting gm : groupMeetings) {
            boolean nameMatch = gm.getName().equalsIgnoreCase(name);
            boolean durationMatch = Math.abs(gm.getDuration() - duration) <= 5;
            boolean timeMatch = gm.getStartTime().equals(start) && gm.getEndTime().equals(end);
            
            if (nameMatch && durationMatch && timeMatch) {
                return gm;
            }
        }
        return null;
    }

    // ── MSG 9d: addParticipant ────────────────────────────────────
    public void addParticipant(String userId, GroupMeeting meeting) {
        meeting.addParticipant(userId);
        triggerSave();
    }

    // ── MSG 10: recordAppointment ─────────────────────────────────
    public void recordAppointment(Appointment appt) {
        appointments.add(appt);
        triggerSave();
        System.out.println("  <- MSG 11: appointment saved: " + appt.getId());
    }

    // ── MSG 12: saveNewReminder ───────────────────────────────────
    public void saveNewReminder(Reminder reminder) {
        reminderList.add(reminder);
        triggerSave();
        System.out.println("  <- MSG 13: reminder saved: " + reminder);
    }

    // ── removeAppointment ─────────────────────────────────────────
    public boolean removeAppointment(String id) {
        boolean removed = appointments.removeIf(a -> a.getId().equals(id));
        if (removed) triggerSave();
        return removed;
    }

    private void triggerSave() {
        if (persistenceManager != null) {
            persistenceManager.saveAll(this);
        }
    }

    public List<Appointment>  getAppointments()  { return appointments; }
    public List<Reminder>     getReminderList()   { return reminderList; }
    public List<GroupMeeting> getGroupMeetings()  { return groupMeetings; }
}
