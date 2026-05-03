package calendar;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Appointment {
    private String id;
    private String name;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int duration; // phút
    private Integer reminder; // phút trước, null = không nhắc
    private boolean isGroup;

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

    public Appointment(String id, String name, String location,
                       LocalDateTime startTime, LocalDateTime endTime,
                       int duration, Integer reminder, boolean isGroup) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.reminder = reminder;
        this.isGroup = isGroup;
    }

    // ── Getters & Setters ──
    public String getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public int getDuration() { return duration; }
    public Integer getReminder() { return reminder; }
    public boolean isGroup() { return isGroup; }
    public void setGroup(boolean group) { isGroup = group; }

    public String formatStart() { return startTime.format(FORMATTER); }
    public String formatEnd()   { return endTime.format(FORMATTER); }

    @Override
    public String toString() {
        String type = isGroup ? "[Nhóm]  " : "[Cá nhân]";
        String loc  = (location != null && !location.isEmpty()) ? location : "Chưa có địa điểm";
        String rem  = (reminder != null) ? "  🔔 Nhắc " + reminder + " phút trước" : "";
        return String.format("%s %-40s | 📍 %-15s | 🕐 %s – %s | ⏱ %d phút%s",
            type, name, loc, formatStart(), formatEnd(), duration, rem);
    }
}
