package calendar.persistence;

import calendar.Appointment;
import calendar.GroupMeeting;
import calendar.Reminder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * PrettyPrinter formats domain objects (Appointment, GroupMeeting, Reminder) into text lines
 * according to the defined persistence format.
 * 
 * Format specifications:
 * - Appointment: id|name|location|startTime|endTime|duration|reminder|isGroup
 * - GroupMeeting: id|name|duration|location|participant1,participant2,participant3
 * - Reminder: id|appointmentId|triggerMinutes|message
 * 
 * Special characters in string fields are escaped:
 * - Pipe (|) → \|
 * - Comma (,) → \,
 * - Backslash (\) → \\
 * - Newline (\n) → \n
 */
public class PrettyPrinter {
    
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    /**
     * Formats an Appointment object into a text line.
     * Format: id|name|location|startTime|endTime|duration|reminder|isGroup
     * 
     * @param appt the Appointment to format
     * @return formatted text line
     */
    public static String formatAppointment(Appointment appt) {
        String id = escapeString(appt.getId());
        String name = escapeString(appt.getName());
        String location = escapeString(appt.getLocation() != null ? appt.getLocation() : "");
        String startTime = formatDateTime(appt.getStartTime());
        String endTime = formatDateTime(appt.getEndTime());
        String duration = String.valueOf(appt.getDuration());
        String reminder = appt.getReminder() != null ? String.valueOf(appt.getReminder()) : "";
        String isGroup = String.valueOf(appt.isGroup());
        
        return String.join("|", id, name, location, startTime, endTime, duration, reminder, isGroup);
    }
    
    /**
     * Formats a GroupMeeting object into a text line.
     * Format: id|name|duration|location|participant1,participant2,participant3
     * 
     * @param meeting the GroupMeeting to format
     * @return formatted text line
     */
    public static String formatGroupMeeting(GroupMeeting meeting) {
        String id = escapeString(meeting.getId());
        String name = escapeString(meeting.getName());
        String duration = String.valueOf(meeting.getDuration());
        String location = escapeString(meeting.getLocation() != null ? meeting.getLocation() : "");
        String participants = formatParticipantsList(meeting.getParticipants());
        
        return String.join("|", id, name, duration, location, participants);
    }
    
    /**
     * Formats a Reminder object into a text line.
     * Format: id|appointmentId|triggerMinutes|message
     * 
     * @param reminder the Reminder to format
     * @return formatted text line
     */
    public static String formatReminder(Reminder reminder) {
        String id = escapeString(reminder.getId());
        String appointmentId = escapeString(reminder.getAppointmentId());
        String triggerMinutes = String.valueOf(reminder.getTriggerMinutes());
        String message = escapeString(reminder.getMessage());
        
        return String.join("|", id, appointmentId, triggerMinutes, message);
    }
    
    /**
     * Formats a list of Appointment objects into text lines.
     * 
     * @param appointments the list of Appointments to format
     * @return list of formatted text lines
     */
    public static List<String> formatAppointments(List<Appointment> appointments) {
        return appointments.stream()
                .map(PrettyPrinter::formatAppointment)
                .collect(Collectors.toList());
    }
    
    /**
     * Formats a list of GroupMeeting objects into text lines.
     * 
     * @param meetings the list of GroupMeetings to format
     * @return list of formatted text lines
     */
    public static List<String> formatGroupMeetings(List<GroupMeeting> meetings) {
        return meetings.stream()
                .map(PrettyPrinter::formatGroupMeeting)
                .collect(Collectors.toList());
    }
    
    /**
     * Formats a list of Reminder objects into text lines.
     * 
     * @param reminders the list of Reminders to format
     * @return list of formatted text lines
     */
    public static List<String> formatReminders(List<Reminder> reminders) {
        return reminders.stream()
                .map(PrettyPrinter::formatReminder)
                .collect(Collectors.toList());
    }
    
    /**
     * Escapes special characters in a string to prevent parsing errors.
     * Escaping rules:
     * - Backslash (\) → \\ (must be first to avoid double-escaping)
     * - Pipe (|) → \|
     * - Comma (,) → \,
     * - Newline (\n) → \n
     * - Carriage return (\r) → \r
     * - Tab (\t) → \t
     * 
     * @param raw the raw string to escape
     * @return escaped string
     */
    public static String escapeString(String raw) {
        if (raw == null) {
            return "";
        }
        
        // Order matters: escape backslash first to avoid double-escaping
        return raw.replace("\\", "\\\\")
                  .replace("|", "\\|")
                  .replace(",", "\\,")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    /**
     * Formats a LocalDateTime into ISO-8601 format (yyyy-MM-ddTHH:mm:ss).
     * 
     * @param dateTime the LocalDateTime to format
     * @return formatted datetime string
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(ISO_FORMATTER);
    }
    
    /**
     * Formats a list of participant IDs into a comma-separated string.
     * Each participant ID is escaped to handle special characters.
     * 
     * @param participants the list of participant IDs
     * @return comma-separated string of escaped participant IDs
     */
    public static String formatParticipantsList(List<String> participants) {
        if (participants == null || participants.isEmpty()) {
            return "";
        }
        
        return participants.stream()
                .map(PrettyPrinter::escapeString)
                .collect(Collectors.joining(","));
    }
}
