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

    // ── MSG 6: checkTimeConflict ──────────────────────────────────
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
    public GroupMeeting checkGroupMeetingMatch(String name, int duration) {
        for (GroupMeeting gm : groupMeetings) {
            if (gm.getName().equalsIgnoreCase(name) &&
                Math.abs(gm.getDuration() - duration) <= 5) {
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
